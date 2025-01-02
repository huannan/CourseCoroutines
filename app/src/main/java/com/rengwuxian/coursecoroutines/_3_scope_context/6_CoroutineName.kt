package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  thread {  }.name = "MyThread"
  val name = CoroutineName("MyCoroutine")
  val scope = CoroutineScope(Dispatchers.IO + name)
  withContext(name) {

  }
  scope.launch {
    println("CoroutineName: ${coroutineContext[CoroutineName]?.name}")
  }
  delay(10000)
}

/*

CoroutineName与Thread.name类似，主要用于测试和调试时更方便的查看代码运行是否符合在我们设计的工作流程上正确执行。

 */