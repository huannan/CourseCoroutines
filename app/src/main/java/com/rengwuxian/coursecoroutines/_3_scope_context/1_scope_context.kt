package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

fun main() = runBlocking<Unit> {
  val context = Dispatchers.IO + Job() + Job()
  val scope = CoroutineScope(Dispatchers.IO)
  val job = scope.launch {
    this.coroutineContext[Job]
    coroutineContext.job
    coroutineContext[ContinuationInterceptor]
  }
  delay(10000)
}

/*

CoroutineContext的定位:CoroutineContext的概念其实顾名思义就是「协程的上下文」，所有信息都是上下文，它们根据功能不同划分成了不同的分类，每个信息都是一个CoroutineContext。比如管理流程用的是Job，管理线程用的是ContinuationInterceptor等等。

CoroutineScope的定位:
1. CoroutineScope是CoroutineContext的容器，用于给已有协程或通过它启动的协程提供上下文信息
2. 用于启动新协程

 */