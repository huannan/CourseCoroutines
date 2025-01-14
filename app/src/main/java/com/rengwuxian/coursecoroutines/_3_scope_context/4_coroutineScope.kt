package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        // coroutineScope()会等待所以子协程的结束才结束
        val startTime1 = System.currentTimeMillis()
        coroutineScope {
            launch {
                delay(2000)
            }
            delay(1000)
            println("Duration within coroutineScope: ${System.currentTimeMillis() - startTime1}")
        }
        println("Duration of coroutineScope: ${System.currentTimeMillis() - startTime1}")

        val startTime = System.currentTimeMillis()
        launch {
            delay(1000)
            println("Duration within launch: ${System.currentTimeMillis() - startTime}")
        }
        println("Duration of launch: ${System.currentTimeMillis() - startTime}")

        // 结构化异常管理
        val name = try {
            coroutineScope {
                val deferred1 = async { "rengwuxian" }
                val deferred2: Deferred<String> = async { throw RuntimeException("Error!") }
                "${deferred1.await()} ${deferred2.await()}"
            }
        } catch (e: Exception) {
            e.message
        }
        println("Full name: $name")

        // supervisorScope()中抛出的异常，不会导致父协程被取消
        launch {
            println("Parent started")
            supervisorScope {
                launch {
                    throw RuntimeException("Error!")
                }
            }
            println("Parent finished")
        }
    }

    delay(10000)
}

private suspend fun someFun() = coroutineScope {
    launch {

    }
}


/*

coroutineScope()函数会创建一个 CoroutineScope 然后在这个 scope 里面执行它的 block 代码块里的代码，这个 CoroutineScope 会继承当前的 coroutineContext，以及用当前的 Job 来作为内部的父 Job。

coroutineScope() 也是会创建一个子协程，和用 launch 创建子协程很像，区别是：
1. coroutineScope() 不能像 launch 一样定制 CoroutineContext
2. coroutineScope() 是一个挂起函数，运行时是串行执行的，会等待它内部的代码块（包括它里面的子协程）都执行完成才返回继续后续的代码；launch 启动协程是并行执行的，启动协程后就继续执行后续的代码
3. coroutineScope() 有返回值，launch 没有返回值

需要注意的是，我们用 coroutineScope() 和 launch 对比并不是想着 [什么时候用 coroutineScope() 替换 launch 使用]，只是因为它们内部的工作原理有很大的相似之处，但它们的应用场景是完全不同的。
coroutineScope()函数的常用场景是：
1. 当我们想在挂起函数里启动协程但又没有启动协程的环境时，就用 coroutineScope() 提供 CoroutineScope 的环境
    private suspend fun someFun() = coroutineScope {
        launch {

        }
}
2. coroutineScope() 可以用来封装完整的功能逻辑，尤其是针对协程的结构化异常管理，在抛出异常时正确捕获try-catch异常能让整个外部协程继续正常工作而不会导致整个协程树崩溃
    val name = try {
        coroutineScope {
            val deferred1 = async { "rengwuxian" }
            val deferred2: Deferred<String> = async { throw RuntimeException("Error!") }
            "${deferred1.await()} ${deferred2.await()}"
        }
    } catch (e: Exception) {
        e.message
    }
    println("Full name: $name")

supervisorScope() 和 coroutineScope() 在功能上是相同的，不同的是 supervisorScope() 创建子协程是一个类似于 SupervisorJob 的子 Job。
但大多数时候挂起函数里启动的各个子协程通常对于挂起函数的总流程都是有用的，在 coroutineScope() 抛异常通常来说整个挂起函数就失去价值了，它就应该坏掉抛异常。所以 supervisorScope() 的适用场景并不多。

 */