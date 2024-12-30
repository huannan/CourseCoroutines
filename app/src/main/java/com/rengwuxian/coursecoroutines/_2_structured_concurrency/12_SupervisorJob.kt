package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(SupervisorJob())
  val supervisorJob = SupervisorJob()
  val job = Job()
  scope.launch {
    println("Parent started")
    val handler = CoroutineExceptionHandler { _, exception ->
      println("Caught in handler: $exception")
    }
    launch(SupervisorJob(coroutineContext.job) + handler) {
      launch {
        throw RuntimeException("Error!")
      }
    }
  }
  delay(1000)
  // println("Parent Job cancelled: ${job.isCancelled}")
  println("Parent finished")
  delay(10000)
}

/*

SupervisorJob的作用是：它的子协程因为非CancellationException异常取消时，父协程不会连带性的被取消。
SupervisorJob是一个总管Job，子Job抛异常的时候不会取消SupervisorJob，而且会负责把异常抛到线程世界当中，因此可以对SupervisorJob的直接子Job设置CoroutineExceptionHandler。

常用方式：
1. 第一种常见的方式是 SupervisorJob 作为子协程和父协程之间的桥梁，类似半链条的方式。SupervisorJob 子协程抛出异常不会影响外部父协程被取消。
    launch(SupervisorJob(coroutineContext.job) + handler) {
      launch {
        throw RuntimeException("Error!")
      }
    }
2. 第二种方式是将 SupervisorJob 提供给 CoroutineScope。用这个 scope 启动的所有协程在抛异常时，都不会触发外面的 CoroutineScope 的取消；一旦外面的 CoroutineScope 取消，scope 启动的所有协程都会被取消。
   val scope = CoroutineScope(SupervisorJob())
   scope.launch {
     throw RuntimeException("Error!")
   }

 */