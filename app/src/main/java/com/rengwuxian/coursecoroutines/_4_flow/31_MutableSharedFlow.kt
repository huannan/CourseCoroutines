package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        emit(1)
        delay(1000)
        emit(2)
        delay(1000)
        emit(3)
    }
    val clickFlow = MutableSharedFlow<String>()
    val readonlyClickFlow = clickFlow.asSharedFlow()
    val sharedFlow = flow1.shareIn(scope, SharingStarted.WhileSubscribed(), 2)
    scope.launch {
        clickFlow.emit("Hello")
        delay(1000)
        clickFlow.emit("Hi")
        delay(1000)
        clickFlow.emit("你好")
        val parent = this
        launch {
            delay(4000)
            parent.cancel()
        }
        delay(1500)
        sharedFlow.collect {
            println("SharedFlow in Coroutine 1: $it")
        }
    }
    scope.launch {
        delay(5000)
        sharedFlow.collect {
            println("SharedFlow in Coroutine 2: $it")
        }
    }
    delay(10000)
}

/*

MutableSharedFlow 的使用场景
* 一直以来我们讲解 Flow 都是在内部调用 emit() 生产数据，然后在一个地方调用 collect() 收集发送过来的数据；
* 但我们的业务需求可能需要能支持在外部调用 emit() 发送数据，比如 UI 交互点击事件，在用户点击按钮的时候，可以从它的点击监听回调能调用一下 emit()，而不是只能从上游的 Flow 把事件发送出来，这是一种很正常的需求。
* 需要能在外部调用 emit() 发送数据要用 MutableSharedFlow

* replay双功能参数：
配置缓冲功能（buffer）的大小：对于来不及消费的数据，先缓冲下来
配置缓存功能（cache）的大小：对于已经使用完的数据，缓存下来，有新订阅的时候直接发送出来

* extraBufferCapacity额外的缓冲的参数：
实际缓冲大小是replay+extraBufferCapacity

* onBufferOverflow缓冲溢出策略：
有订阅者的时候，来不及消费的时候的缓冲溢出策略

为什么 Flow 不直接提供可以外部发送数据的 Flow？
* Flow 不提供外部发送数据的原因也很简单，Flow 数据流本就是一个需要指定规则然后按规则一条条把数据发送出来，本来就不需要从外部发送数据，如果还提供外部发送数据，内部和外部的数据混乱数据源不统一，反而更容易让开发者不小心写出错误代码。
* SharedFlow 是事件流，它天然就是需要从各个地方发送数据的，Flow 数据流的限制就不需要了，允许让我们在任何协程发送数据。

MutableSharedFlow 和 shareIn() 的选择：
* 如果要创建一个事件流，在外部生产数据发送数据源，就用 MutableSharedFlow
* 如果已经有了一个生产事件流的 Flow，不需要自己写生产数据的代码，直接将 Flow 用 shareIn() 转成 SharedFlow 即可

asSharedFlow()
* 希望把 MutableSharedFlow 暴露出来给外部去订阅，但又不希望让外部也来发送数据的时候，可以通过 asSharedFlow() 将 MutableSharedFlow 转换成 SharedFlow
* SharedFlow 只能读不能写

 */