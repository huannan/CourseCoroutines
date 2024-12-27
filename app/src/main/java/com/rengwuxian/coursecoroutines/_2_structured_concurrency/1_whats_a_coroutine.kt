package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  // 启动线程，可以拿到线程对象，可以完成对「单独业务线」的管理，比如获取元数据或结束线程等；但是协程里面不存在等价物
  // val thread = thread {  }

  val scope = CoroutineScope(Dispatchers.IO)
  var innerJob: Job? = null
  var innerScope: CoroutineScope? = null
  // 协程launch返回Job是为了做责任的区分，而不是返回StandaloneCoroutine，StandaloneCoroutine实现了Job接口
  val outerJob = scope.launch(Dispatchers.Default) {
    innerJob = coroutineContext[Job]
    innerScope = this
    launch {

    }
  }
  outerJob.cancel()

  scope.async {

  }

  println("outerJob: $outerJob")
  println("innerJob: $innerJob")
  println("outerJob === innerJob: ${outerJob === innerJob}")
  println("outerJob === innerScope: ${outerJob === innerScope}")
}