package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking<Unit> {
  val job1 = Job()
  val job2 = Job()
  val scope = CoroutineScope(Dispatchers.IO + job1
      + CoroutineName("MyCoroutine") + job2)
  // [[CoroutineName(MyCoroutine), job2], Dispatchers.IO]
  println("job1: $job1, job2: $job2")
  println("CoroutineContext: ${scope.coroutineContext}")
  scope.launch {
    val job: Job? = coroutineContext[Job]
    val interceptor: CoroutineDispatcher? = coroutineContext[CoroutineDispatcher]
    println("coroutineContext: $coroutineContext")
    println("coroutineContext after minusKey() ${coroutineContext.minusKey(Job)}")
  }
  delay(10000)
}

/*

CoroutineContext合并(plus()函数)规则：
1. 有多个 CoroutineContext 合并，会两两合并成一个 CombineContext；合并不是按顺序的，合并成新的 CombineContext 时 CoroutineContext 合并的顺序是可能会调整的
2. 有相同类型的 CoroutineContext 合并，最后添加的相同类型的 CoroutineContext 会把之前的替换移除
3. 大部分情况下直接合并相同类型的 CoroutineContext 会报错，即是没报错也是新的替换旧的，没有意义

CoroutineContext 的获取：
coroutineContext[Job]
coroutineContext[ContinuationInterceptor]
coroutineContext[CoroutineDispatcher]

CoroutineContext 的移除：
coroutineContext.minusKey(Job)

 */