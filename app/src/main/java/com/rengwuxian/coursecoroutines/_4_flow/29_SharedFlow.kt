package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
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

    val sharedFlow = flow1.shareIn(scope, SharingStarted.Eagerly)
    scope.launch {
        delay(500)
        sharedFlow.collect {
            println("SharedFlow in Coroutine 1: $it")
        }
    }
    // Channel: hot
    // FLow: cold
    scope.launch {
        delay(1500)
        sharedFlow.collect {
            println("SharedFlow in Coroutine 2: $it")
        }
    }

    Ticker.start()
    // flow2类似SharedFlow，背靠的数据源是独立运作/动态的，所以可以从业务角度看作「热」的
    val flow2 = callbackFlow {
        Ticker.subscribe { trySend(it) }
        awaitClose()
    }
    // scope.launch {
    //     delay(2500)
    //     flow2.collect {
    //         println("flow2 - 1: $it")
    //     }
    // }
    // scope.launch {
    //     delay(1500)
    //     flow2.collect {
    //         println("flow2 - 2: $it")
    //     }
    // }

    delay(10000)
}

object Ticker {
    private var time = 0
        set(value) { // Kotlin setter
            field = value
            subscribers.forEach { it(value) }
        }

    private val subscribers = mutableListOf<(Int) -> Unit>()

    fun subscribe(subscriber: (Int) -> Unit) {
        subscribers += subscriber
    }

    fun start() {
        GlobalScope.launch {
            while (true) {
                delay(1000)
                time++
            }
        }
    }
}

/*

概况
SharedFlow 和 StateFlow 都是一种特殊的 Flow 的变种，SharedFlow 是把 Flow 的功能从数据流的收集改成了事件流；StateFlow 是 SharedFlow 的细化细分，StateFlow 将事件流改成了状态订阅，经过这两个 Flow 的变种一下就把 Flow 的适用场景切换到了一个非常实用的范围。

数据流的收集和事件订阅的区别
Flow 虽然是个数据流但是它只是设定好了数据流的规则，而并不是直接开始启动数据流开始生产，生产过程是在调用 collect() 时才启动的，并且是每次调用 collect() 都分别启动一个新的数据流。这也是为什么 Flow 提供数据收集的函数名为 collect() 而不是 subscribe()。
实际上事件订阅就是一种特殊类型的数据收集，用数据收集的功能是能实现事件订阅的功能，这种事件订阅的 API 在 Flow 也有提供就是 SharedFlow。

launchIn() 和 shareIn() 的区别
* launchIn() 会立即用你指定的 CoroutineScope 启动一个协程，然后调用 collect() 在这个协程启动收集流程
* shareIn() 可以将一个已存在的 Flow 转换成 SharedFlow，同样的也是调用 collect() 收集；shareIn() 可以定制指定数据收集的时机，比如修改为 SharingStarted.Eagerly 立即启动
* shareIn() 会创建一个新的 Flow，返回的 Flow 类型就是 SharedFlow；SharedFlow 实际上只是把上游 Flow 发送的每条数据做转发，把上游 Flow 发送的每条数据转发到下游每个调用 collect() 的 FlowCollector。shareIn() 创建了 SharedFlow 把 [数据生产和数据收集流程分拆开]，这个特点让 SharedFlow 相比传统的 Flow，倒不如说 SharedFlow 更像是 Channel。

SharedFlow 与 Flow、Channel 的区别
* SharedFlow 和 Channel 不一样的是，SharedFlow 不是瓜分式的，而是每条数据都会发送到每一个进行中的 collect()。
* 普通的 Flow 多次调用 collect() 都独立完整跑一次流程，SharedFlow 是多次调用 collect() 只跑一次流程，即用 SharedFlow 事件订阅调用 collect() 发生在数据发送之后，调用 collect() 前发送的数据将丢失。

接下来我们再聊聊 [冷] 和 [热] 的话题。
 [冷] 和 [热] 的区分：在官方说法，Channel 是 [热] 的，Flow 是 [冷] 的，Channel 的 [热] 其实就是不读取数据它也可以发送，Flow 的 [冷] 是只在每次 collect() 被调用的时候才会启动数据发送流程。

SharedFlow的 [热] 的本质 / SharedFlow关于 [冷] 和 [热] 的理解：
* SharedFlow 虽然是 Flow，但它是 [热] 的，因为 SharedFlow 的活跃状态跟它是否正在被调用 collect() 函数来收集数据是无关的，所以它的活跃状态是独立的，这就跟 Channel 一样了，所以它是 [热] 的。
* SharedFlow 的 [热] 和 Channel 的 [热] 不太一样：Channel 的 [热] 是真的数据的发送和读取两个流程完全独立的；SharedFlow 的 [热] 其实并不是技术角度的描述，而是业务逻辑角度的。
* SharedFlow的本质依然是在 collect() 被调用时才开始生产，本质上 SharedFlow 依然是 [冷] 的，但是由于它背靠着一个独立运作的 Flow，所以它生产出来的数据跟 collect() 的调用并没有绑定，而是独立生产的。
* 所以我们说 SharedFlow 是 [冷] 的那就是从技术角度分析，说它是 [热] 的那就是从业务逻辑角度分析，两个说法都对。

SharedFlow / shareIn() 的适用场景
* 数据来源共享：如果想要一个 Flow 它被收集多次的时候都可以共享相同的数据生产流程，就可以用 shareIn() 将 Flow 转成 SharedFlow，再让下游去收集 SharedFlow，多次的收集之间是依赖的同一个数据流
* 生产提前启动：SharedFlow 能做到数据生产的提前启动，如果有一个 Flow 有耗时的初始化的操作，但不希望在调用 collect() 的时候等待这个初始化，也可以将 Flow 转成 SharedFlow，因为在这里的目的并不是共享，而是为了提前启动生产
* 事件订阅：因为 SharedFlow 是 [热] 的，生产流程是独立的，那么在开始生产之后才开始收集，那就会漏掉之前生产的数据，所以 SharedFlow 也适合对从头开始收集数据没有需求的场景，也就是「事件订阅」场景

SharedFlow / shareIn() 的适用场景的理解
* SharedFlow 的效果是把 [数据生产和数据收集流程分拆开]，这个效果让 SharedFlow 可以满足各种需求场景，比如事件订阅、提前启动生产、数据来源共享等，通常来讲我们也会把它用在事件流订阅的场景。
* shareIn() 的适用场景本质上就是 [数据生产和数据收集流程分拆开] 的需求，都可以将 Flow 转成 SharedFlow 来解决。SharedFlow 的 [热] 就是我们使用 SharedFlow 的根本原因。
* 但是要注意：SharedFlow 并不会因为生产流程的结束而结束订阅，即数据生产都发送完了，SharedFlow 的 collect() 会一直运行，直到外部协程的取消而抛异常结束。





 */