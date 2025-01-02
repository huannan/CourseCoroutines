package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  scope.launch {
    showDispatcher()
  }
  delay(10000)
}

private fun flowFun() {
  flow<String> {
    coroutineContext
  }
  GlobalScope.launch {
    flow<String> {
      // 获取到 GlobalScope.launch 的 coroutineContext
      coroutineContext
      // 获取到 flow 的 coroutineContext
      currentCoroutineContext()
    }
  }
}

private suspend fun showDispatcher() {
  delay(1000)
  println("Dispatcher: ${coroutineContext[ContinuationInterceptor]}")
}

/*

挂起函数肯定是运行在协程上的，也就是外面肯定会包一个 CoroutineScope，那么 [在挂起函数获取 CoroutineScope 的 coroutineContext] 也是合理的要求。所以 kotlin 是有提供给我们在挂起函数获取 coroutineContext 的属性

另外提供的 currentCoroutineContext() 函数虽然也是拿的 coroutineContext，它主要的作用是在协程使用时避免命名冲突的

 */