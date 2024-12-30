package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    var childJob: Job? = null
    val parentJob = scope.launch {
        childJob = launch {
            launch {
                println("Grand child started")
                delay(3000)
                println("Grand child done")
            }
            delay(1000)
            throw IllegalStateException("User invalid!")
            // throw CancellationException()
        }
        println("Parent started")
        delay(3000)
        println("Parent done")
    }

    // CoroutineExceptionHandler
    parentJob.cancel()

    // 抛异常前的打印
    // delay(500)
    // println("isActive: parent - ${parentJob.isActive}, child - ${childJob?.isActive}")
    // println("isCancelled: parent - ${parentJob.isCancelled}, child - ${childJob?.isCancelled}")
    // 抛异常后的打印
    // delay(1000)
    // println("isActive: parent - ${parentJob.isActive}, child - ${childJob?.isActive}")
    // println("isCancelled: parent - ${parentJob.isCancelled}, child - ${childJob?.isCancelled}")

    delay(10000)
}

/*
取消流程和异常流程本质上是同一套逻辑。当在协程抛出的是CancellationException时，协程会走简化版的异常流程，也就是取消流程。

取消流程和异常流程的区别：
1. 取消流程的连带取消只是向内的，即协程取消只会连带性的取消它的子协程。异常流程的连带取消是双向的，不仅会向内取消它的子协程，还会向外取消它的父协程（逐级向上递归取消），每一个被取消的父协程序也会把它们的每个子协程取消，直到整个协程树都被取消。
    具体可以参考JobSupport的childCancelled方法
    public open fun childCancelled(cause: Throwable): Boolean {
        if (cause is CancellationException) return true
        return cancelImpl(cause) && handlesException
    }
2. 异常流程只有在协程内部抛异常才能触发；而取消流程除了协程内部抛异常，还可以通过调用cancel()触发（只能填CancellationException，普通异常不对开发者开发传入）
3. 异常流程抛出的异常，最终会暴露给线程世界

父子协程：协程都是在流程上并行执行，但是逻辑上是的父子包含关系，即一个父流程包含多个子流程的关系

协程结构化管理特性：
1. 父协程的取消会自动取消子协程（大流程取消，子流程也应该取消）
2. 父协程会等待所有子协程执行完毕，父协程才会取消（只有所以子流程都完成，父流程才算是完成）
3. 子协程的取消不会自动取消父协程（子流程正常完成，不影响父流程）；子协程的异常取消会自动取消父协程（可以理解成子流程坏掉了，父流程就失去意义了）

 */