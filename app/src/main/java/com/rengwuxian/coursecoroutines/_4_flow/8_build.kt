package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

fun main() = runBlocking<Unit> {
  val flow1 = flowOf(1, 2, 3)
  val flow2 = listOf(1, 2, 3).asFlow()
  val flow3 = setOf(1, 2, 3).asFlow()
  val flow4 = sequenceOf(1, 2, 3).asFlow()
  val channel = Channel<Int>()
  val flow5 = channel.consumeAsFlow()
  val flow6 = channel.receiveAsFlow()
  val flow7 = channelFlow {
    launch {
      delay(2000)
      send(2)
    }
    delay(1000)
    send(1)
  }
  val flow8 = flow {
    launch {
      delay(2000)
      emit(2)
    }
    delay(1000)
    emit(1)
  }
  val flow9 = callbackFlow {
    gitHub.contributorsCall("square", "retrofit")
      .enqueue(object : Callback<List<Contributor>> {
        override fun onResponse(call: Call<List<Contributor>>, response: Response<List<Contributor>>) {
          trySend(response.body()!!)
          close()
        }

        override fun onFailure(call: Call<List<Contributor>>, error: Throwable) {
          cancel(CancellationException(error))
        }
      })
    // 使用 callbackFlow 会强制要求调用 awaitClose() 否则会抛异常
    awaitClose()
  }
  val scope = CoroutineScope(EmptyCoroutineContext)
  scope.launch {
    flow9.collect {
      println("channelFlow with callback: $it")
    }
    /*flow8.collect {
      println("channelFlow: $it")
    }*/
    /*flow5.collect {
      println("Flow6 - 1: $it")
    }*/
  }
  scope.launch {
    /*flow5.collect {
      println("Flow6 - 2: $it")
    }*/
  }
  /*channel.send(1)
  channel.send(2)
  channel.send(3)
  channel.send(4)*/
  delay(10000)
}

/*

Flow 的创建
* 直接用 flow() 函数创建，然后在 lambda 提供生产发送数据的处理
  val flow = flow {
    emit(1)
  }
* 创建一个或一串数据转换成 Flow：flowOf()
* 转换成 Flow：asFlow()。asFlow() 是扩展函数，可以将 List、Set 等对象转换成 Flow
* Channel 转换为 Flow：consumeAsFlow()、receiveAsFlow() 和 channelFlow()
  从上游看是由 Channel 生产数据，然后交给下游的 Flow，下游 Flow 直到每次调用 collect() 的时候才会发送数据。简单理解就是上游 Channel 一直在发送数据，下游的 Flow 调用 collect() 才会释放数据否则就掐着不放数据到下游。
  用 Channel 转换的 Flow 也有不同的地方：多个协程调用 collect() 会瓜分数据
  用 Channel 转换的 Flow 虽然从整体流程上可以看成是 [热] 的也能看成是 [冷] 的，但从行为模式上整体流程还是 [热] 的，因为接收的数据不是相互独立的会瓜分 Channel 发送的数据。
  consumeAsFlow() 和 receiveAsFlow() 的区别：
  * consumeAsFlow() 只能被消费一次，调用多次 collect() 会抛出异常
  * receiveAsFlow() 多次接收不会抛异常
* channelFlow()
  channelFlow() 相比上面提到的 consumeAsFlow() 和 receiveAsFlow() 是完全不同的：
  * consumeAsFlow() 和 receiveAsFlow() 是用一个现成的 Channel 作为数据源，所有 collect() 来共享消费瓜分 Channel 发送的数据
  * channelFlow() 是直到调用 collect() 才会创建 Channel，多次调用 collect() 就会创建多个 Channel，这些 Channel 的生产流程是互相隔离、各自独立的
  channelFlow 的使用场景：
  * 需要在 Flow 启动子协程
  * Flow 是不允许切换协程调用 emit()，而你有跨协程生产数据的需求
* callbackFlow() 支持与回调协作转换

一般将回调 API 转换成协程使用的 suspendCancellationCoroutine()。callbackFlow() 可以看成是 Flow 版的 suspendCancellationCoroutine()，一个负责单次的回调，一个负责多次回调的数据流。
suspendCancellationCoroutine() 与 callbackFlow() 的区别是：
* suspendCancellationCoroutine() 能处理单次的回调切换到协程环境
* callbackFlow() 可以处理连续的回调


Flow 为什么不允许切换协程？
作为开发者，我们可以明确知道collect所运行的线程，如果Flow切换了线程，那么collect所运行的线程就不明确了。Flow的collect方法在哪个线程执行，那么它所接收到的数据，就一定是在哪个线程中产生的。
所以为什么 Flow 不允许切换协程调用 emit()，是因为这会导致和开发者在调用 collect() 的预期出现不一致的结果，即预期在这个协程运行，但代码又在另一个协程运行，进而引发各种隐患。
在协程这一点上 Flow 和 channelFlow() 的区别是：
channelFlow() 可以切协程是因为用的就是 Channel，Channel 做的事情就是跨协程的，Channel 发送数据，Channel 转换为 Flow 后，调用 flow.collect() 数据的接收还是在 collect() 所在的协程，所以并不会有问题。

 */