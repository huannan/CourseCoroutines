package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking<Unit> {
  CoroutineScope(EmptyCoroutineContext).launch {

  }
  GlobalScope.launch {
    delay(100)
    println("launch finished")
  }
  // 不能直接cancel GlobalScope
  // GlobalScope.cancel()
  val job = GlobalScope.async {
    delay(1000)
  }
  println("job parent: ${job.parent}")
  // 及时关闭协程
  job.cancel()

  delay(10000)
}

/*

GlobalScope是一个特殊的CoroutineScope，它是一个单例。

可以看到 GlobalScope 重写了 coroutineContext，直接返回了 EmptyCoroutineContext。所以说 GlobalScope 真正的特点是它没有内置的 Job，因为 coroutineContext 就是一个空的上下文，自然也没有 Job。
没有 Job 的 CoroutineScope 有什么作用呢？没有 Job 说明它创建的协程就没有父协程，确切的说它创建的协程的 Job 就没有父 Job。

GlobalScope 的使用场景：在不需要和生命周期绑定又想启动协程的地方使用，因为没有父 Job 也就不会因任何组件的关闭而自动取消协程。
CoroutineScope(EmptyCoroutineContext).launch 这么写又比较麻烦，所以提供了GlobalScope。使用它时做好及时关闭协程即可，并不是不能使用的东西。

 */