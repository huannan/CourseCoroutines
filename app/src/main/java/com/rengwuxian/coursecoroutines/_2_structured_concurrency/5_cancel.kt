package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine

// 协程的交互式取消
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext) // Default
    val job = launch(Dispatchers.Default) {
        // 不配合取消
        // suspendCoroutine<String> {
        //
        // }

        // 配合取消
        // suspendCancellableCoroutine<String> {
        //
        // }

        var count = 0
        while (true) {
            // 如果不需要清理工作，可以直接使用ensureActive()
            // ensureActive()
            // public fun Job.ensureActive(): Unit {
            //     if (!isActive) throw getCancellationException()
            // }
            if (!isActive) {
                // clear
                println("Clear")
                // 取消协程不是用return，而是抛异常，协程会处理的特殊异常，会接住这个异常并把自己取消
                throw CancellationException()
            }
            count++
            if (count % 100_000_000 == 0) {
                println(count)
            }
            if (count % 1_000_000_000 == 0) {
                break
            }
        }

        // var count = 0
        // while (true) {
        //     println("count: ${count++}")
        //     try {
        //         // 协程里几乎所有的挂起函数（包括等待型函数例如 delay）都会抛CancellationException，除了suspendCoroutine不支持取消
        //         delay(500)
        //     } catch (e: CancellationException) {
        //         println("Cancelled")
        //         // Clear
        //         // 可以捕获住CancellationException，但是要谨慎，因为逻辑会继续走，但是不能调用挂起函数了，一调用就会抛CancellationException
        //         // 因此，在捕获住CancellationException之后，需要再次将其抛出
        //         throw e
        //     } finally {
        //         // 如果不需要捕获异常做额外处理
        //         // Clear
        //     }
        // }
    }
    delay(1000)
    job.cancel()
}

// 题外话
// Java进程只会等待用户线程都结束，程序就结束，而不会等待守护线程。需要看协程的线程是否为守护线程
// Daemon Thread
// User Thread