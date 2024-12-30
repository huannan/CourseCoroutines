package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.system.exitProcess

fun main() = runBlocking<Unit> {
    // // 针对所有线程的意料之外的异常进行善后工作
    // Thread.setDefaultUncaughtExceptionHandler { t, e ->
    //     // UncaughtExceptionHandler 是异常处理的最后一道拦截，此时线程已经结束执行无法修复，只能收尾后杀死应用或重启
    //     // 记录异常日志收尾后将程序杀死或重启，防止进程处于混乱状态
    //     println("Caught default: $e")
    //     exitProcess(1)
    // }
    //
    // val thread = object : Thread() {
    //     override fun run() {
    //         // 使用try修复意料之中异常，修复流程，此时线程还没结束
    //         try {
    //
    //         } catch (e: NullPointerException) {
    //
    //         }
    //         throw RuntimeException("Thread error!")
    //     }
    //
    // }
    // // 单独设置线程异常处理器，设置后不会走Thread.setDefaultUncaughtExceptionHandler
    // // 善后工作比较通用，所以一般很少单独给线程设置
    // // thread.setUncaughtExceptionHandler { t, e ->
    // //     println("Caught $e")
    // // }
    // thread.start()

    // 协程抛出的异常如果不处理，最终也会给DefaultUncaughtExceptionHandler处理
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        // UncaughtExceptionHandler 是异常处理的最后一道拦截，此时线程已经结束执行无法修复，只能收尾后杀死应用或重启
        // 记录异常日志收尾后将程序杀死或重启，防止进程处于混乱状态
        println("Caught default: $e")
        exitProcess(1)
    }
    val scope = CoroutineScope(EmptyCoroutineContext)
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught in Coroutine: $exception")
    }
    // 通常我们使用协程可以独立的完成一个完整的功能，在某个子协程抛出异常时能取消掉整个协程树的条件下（这在线程是很麻烦的），又能将异常汇报到一个地方（CoroutineExceptionHandler），然后做一些善后工作就很方便。
    // 协程的异常管理总结：CoroutineExceptionHandler是针对整个协程树的「未知异常」进行「善后工作」，所以只能在最外层协程中设置
    // CoroutineExceptionHandler对标UncaughtExceptionHandler
    scope.launch(handler) {
        launch {
            throw RuntimeException("Error!")
        }
        launch {

        }
    }
    delay(10000)
}
