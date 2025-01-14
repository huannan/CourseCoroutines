package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.unstableGitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        try {
            for (i in 1..5) {
                // 数据库读数据
                // 网络请求
                emit(i)
            }
        } catch (e: Exception) {
            println("Error in flow(): $e")
            throw e
        }
    }.map { throw NullPointerException() }
        .onEach { throw NullPointerException() }
        .transform<Int, Int> {
            val data = it * 2
            emit(data)
            emit(data)
        }
    // Exception Transparency
    scope.launch {
        try {
            flow1.collect {
                val contributors = unstableGitHub.contributors("square", "retrofit")
                println("Contributors: $contributors")
            }
        } catch (e: TimeoutException) {
            println("Network error: $e")
        } catch (e: NullPointerException) {
            println("Null data: $e")
        }
    }
    delay(10000)
}

private fun fun1() {
    fun2()
}

private fun fun2() {
    fun3()
}

private fun fun3() {
    throw NullPointerException("User null")
}

/*

Flow 的核心工作原理：emit() 只是充当一个发送数据的占位符的作用，将 emit() 的执行替换成 collect() 执行的代码，就是 Flow 的核心工作原理。
根据 Flow 的工作原理，下游的异常异常实际上会先被 Flow 内部的try-catch捕获。但这样捕获异常会出现的问题是，收集数据的 try-catch 是无效的，因为发生异常时，异常已经被生产数据的 try-catch 捕获，并且出现异常时无法正常走协程的异常流程。

Flow 异常处理的原则是：上游的 Flow 不应该吞掉下游的异常，包括 Flow 数据流经过的每一个上游操作符都不能去捕获异常，即上游的生产过程让下游的数据处理过程的异常变得不可见了，这在 Flow 是不允许的。为了保证异常的可见性，[不要在 Flow 用 try/catch] 指的是不要用 try/catch 包住 emit()。
如果真的要try-catch，那么应该重新将异常抛出来。


 */
