package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.measureTime

// 协程的结构化取消：父协程的取消会导致子协程被取消，具体过程：
// 1. Job 在 cancel() 里面修改自己的 isActive 变成 false，作为一个父协程还会调用所有子协程 Job 的 cancel()，也会修改子协程 isActive 变成 false；由于 isActive 状态被改变，在检查点协程各自会抛出 CancellationException 异常
// 2. 协程什么时候抛 CancellationException 是不确定的，因为协程代码要执行到检查点检查 isActive 时才会抛出异常。并且每个协程在抛 CancellationException 之后，还会产生跟对自己调用 cancel() 一样的效果去修改 isActive 变成 false，以及调用所有子 Job 的 cancel()
fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val parentJob = scope.launch {
    val childJob = launch {
      println("Child job started")
      delay(3000)
      println("Child job finished")
    }
  }
  delay(1000)
  parentJob.cancel()
  measureTime { parentJob.join() }.also { println("Duration: $it") }
  delay(10000)
}

// 子协程能拒绝父协程的取消吗？
// 不能。可以强行捕获CancellationException，但是后续无法调用挂起函数，程序运行乱套，不推荐