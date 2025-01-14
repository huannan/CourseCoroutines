package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val receiver = scope.produce {
    while (isActive) {
      val data = gitHub.contributors("square", "retrofit")
      send(data)
    }
  }
  launch {
    // 模拟复杂的业务逻辑
    delay(5000)
    while (isActive) {
      // 类似await()，但是可以反复调用获取多次
      println("Contributors: ${receiver.receive()}")
    }
  }
  delay(10000)
}

/*

async()的本质是将数据的发送和接收进行了分离，方便在其它协程使用结果。

使用 Channel 只需要三个步骤：
produce() 启动一个 [生产数据给别的协程来用] 的协程。可以直接把 ProducerScope 当成正常协程 CoroutineScope 来用就行， 在它里面也可以启动子协程，协程取消流程和异常流程都适用。
send() 发送数据
使用 produce() 返回的 ReceiveChannel 对象，在其他协程调用 receive() 获取 send() 发送的数据

了解了 Channel 的使用方式，我们简单类比下 async() 和 Channel 的区别：
启动协程的区别：async() 返回的 Deferred 对象，produce() 返回的 ReceiveChannel 对象
接收结果的区别：Deferred.await() 返回结果是一次性的，多次调用返回相同的返回值；ReceiveChannel.receive() 每次调用从 send() 获取不同的返回值

 */