package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ObsoleteCoroutinesApi::class)
fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val sender = scope.actor<Int> {
    for (num in this) {
      println("Number: $num")
    }
  }
  scope.launch {
    for (num in 1..100) {
      sender.send(num)
      delay(1000)
    }
  }
  delay(10000)
}

/*

把 SendChannel 暴露出来
前面提到把 Channel 的创建及数据发送和协程放在一起可以使用 produce() 简化代码，实际上还提供了 把 Channel 的创建及数据接收和协程放在一起的函数：actor()。
* produce() 是提供一个协程，然后在内部创建一个 Channel 对象出来，并把 ReceiveChannel 用返回值的形式暴露出来，然后在内部提供 SendChannel。
* actor() 是提供一个协程，然后在内部创建一个 Channel 对象出来，并把 SendChannel 用返回值的形式暴露出来，然后在内部提供 ReceiveChannel。


 */