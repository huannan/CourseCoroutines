package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
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
    val sharedFlow = flow1.shareIn(scope, SharingStarted.WhileSubscribed(), 2)
    scope.launch {
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

replay双功能参数：
配置缓冲功能（buffer）的大小：对于来不及消费的数据，先缓冲下来
配置缓存功能（cache）的大小：对于已经使用完的数据，缓存下来，有新订阅的时候直接发送出来

started参数：
配置背后用于生产数据的Flow的启动时间，有三个数值：

SharingStarted.Eagerly：调用 shareIn() 创建 SharedFlow 的同时立即启动数据的生产
SharingStarted.Lazily：ShredFlow 被调用第一次 collect() 时才会启动数据的生产
SharingStarted.WhileSubscribed()：可以把上游的数据流结束和重启的规则，它是一种复杂化的 Lazily，不仅是在第一次订阅的时候启动上游的数据流，而且在下游所有订阅全都结束之后，它会把上游 Flow 的生产流程也结束掉，这时候如果再有订阅，它就会重新启动上游的数据流。但是之前的缓存也会立即发送。
    stopTimeoutMillis：默认是0.下游所有订阅全都结束之后，上游 Flow 的生产流程的结束的延时，如果此时有新的订阅，则不会重启上游 Flow 的生产流程
    replayExpirationMillis：默认是无限大。缓存的失效时间，下游所有订阅全都结束之后，并且也设置了stopTimeoutMillis，清空缓存的延时，如果此时有新的订阅则缓存不会失效

关于 SharedFlow 订阅的结束
SharedFlow 并不会因为生产流程的结束而结束订阅，即数据生产都发送完了，SharedFlow 的 collect() 会一直运行，直到外部协程的取消而抛异常结束。

WhileSubscribed() 的适用场景：
假设软件里有一个可以被订阅的事件流，这个事件流会在多个地方被订阅，而同时这个事件流还非常的重即生产流程非常消耗资源，所以想要在所有订阅都结束的时候及时的结束生产；这种场景就很适合用 WhileSubscribed() 配置自动结束、自动重启的 SharedFlow。

 */