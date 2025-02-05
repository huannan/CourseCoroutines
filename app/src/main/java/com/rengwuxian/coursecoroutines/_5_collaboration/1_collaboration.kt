package com.rengwuxian.coursecoroutines._5_collaboration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)

    // 在 Java 有 CountDownLatch，它可以做到等待多个线程，比如要等待两个线程后再执行后续代码，可以将 CountDownLatch 调整为 2
    val latch = CountDownLatch(2)
    thread {
        latch.await()
    }
    thread {
        Thread.sleep(1000)
        latch.countDown()
    }
    thread {
        Thread.sleep(2000)
        latch.countDown()
    }

    // join() 函数实现 CountDownLatch 效果
    val preJob1 = launch {
        delay(1000)
    }
    val preJob2 = launch {
        delay(2000)
    }
    launch {
        preJob1.join()
        preJob2.join()
    }

    // Channel 实现 CountDownLatch 效果，最接近的实现方式
    val countDown = 2
    val channel = Channel<Unit>(countDown)
    launch {
        repeat(countDown) {
            channel.receive()
        }
    }
    launch {
        delay(1000)
        channel.send(Unit)
    }
    launch {
        delay(2000)
        channel.send(Unit)
    }

    val job1 = scope.launch {
        println("Job 1 started")
        delay(2000)
        println("Job 1 done")
    }
    val deferred = scope.async {
        println("Deferred started")
        delay(3000)
        println("Deferred done")
        "rengwuxian"
    }
    scope.launch {
        println("Job 2 started")
        delay(1000)
        job1.join()
        println("Deferred result: ${deferred.await()}")
        println("Job 2 done")
    }
    delay(10000)
}

/*

协程间的协作和等待
协程天生就是并行的，不管是同等级的协程、还是父子协程、还是互相之间完全没有关系的协程，它们之间都是并行的；如果我们希望 协程之间互相等待，有两种方式：
* Job 的 join() 函数
* 如果用 async() 启动的协程，可以用返回的 Deferred 对象的 await() 函数

 */