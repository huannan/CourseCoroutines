package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught in Coroutine: $exception")
    }

    scope.launch(handler) {
        println("Parent1 started")
        val deferred = async {
            delay(1000)
            // async中抛异常的影响：async所在的协程树，会取消这个协程树；deferred.await()调用所在的协程树也会取消
            throw RuntimeException("Error!")
        }
        launch(Job()) {
            println("Parent2 started")
            try {
                deferred.await()
            } catch (e: Exception) {
                println("Caught in await: $e")
            }
            try {
                delay(1000)
            } catch (e: Exception) {
                println("Caught in delay: $e")
            }
            println("Parent2 finished")
        }
        delay(3000)
        println("Parent1 finished")
        // delay(100)
        // cancel()
    }

    // val deferred = scope.async(handler) {
    //     throw RuntimeException("Error!")
    // }
    // deferred.await()

    delay(10000)
}

/*

1. 在async里面抛异常时是有「双重影响」：它不仅会用这个异常来触发它所在的协程树的结构化异常处理流程取消协程；还会直接让它的await()调用的协程也抛出这个异常，进而取消目标协程树
2. async和launch的另一个异常流程的区别是：即使async作为最外层父协程，对async设置CoroutineExceptionHandler也是没有效果的。
    async不会往线程世界抛异常，因为async抛出的异常要给await()，await()还是运行在协程的
    而launch会把内部的异常抛给线程世界是因为它已经是整个流程的终点了

 */