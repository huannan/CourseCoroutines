package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    // Channel 即实现了 SendChannel 可以调用 send() 发送数据，又实现了 ReceiveChannel 可以调用 receive() 接收数据。
    // Channel 是一个接口，Channel() 是一个工厂函数，根据传参不同创建不同的 Channel
    val channel = Channel<List<Contributor>>()
    scope.launch {
        while (isActive) {
            channel.send(gitHub.contributors("square", "retrofit"))
            println("send complete")
        }
    }
    scope.launch {
        while (isActive) {
            println("Contributors 1: ${channel.receive()}")
        }
    }
    scope.launch {
        while (isActive) {
            println("Contributors 2: ${channel.receive()}")
        }
    }
    delay(10000)
}

/*

手动创建 Channel 和使用 produce() 可以分场景使用：
如果你的业务逻辑比较散需要把代码散开来写，可以手动创建 Channel、手动开启协程
如果希望把 Channel 的创建及数据的发送和协程放在一起，可以直接用 produce() 简化代码结构

Channel 的本质
Channel 的本质就是一个在不同协程之间传递数据的通道，任何协程都能调用它的 send() 发送数据和调用 receive() 读取数据。
之所以拆分成 SendChannel 和 ReceiveChannel 仅是为了暴露尽量少的 API 提供给开发者，实际在使用的对象都是同时实现了这两个接口。
可以把 Channel 理解为是一个队列，承担类似 Java 里的阻塞式队列 BlockingQueue 的角色，只不过 Channel 是协程版的，把阻塞式的实现改成了挂起式；当从队列添加数据但元素满了或者从队列读取数据但队列为空，就会将协程挂起而不是把线程卡住。
    元素满了，send会挂起发送协程
    元素为空，receive会挂起接收协程

Channel 的工作模式
因为 Channel 是这种挂起式的队列，超过一个Receiver的时候数据会被瓜分的，所以也导致了它不适合做可订阅的事件流。
在上面示例代码中，只有一个发送者但有多个订阅者，由于每一个 send() 只能被一个 receive() 收到，如果其中一个订阅者接收了数据，那么另一个就得等待直到 send() 有下一个数据另一个订阅者才能接收；这就导致每个订阅者都没法获取完整的事件序列，即订阅者不能共享每一个事件。

Channel 的使用场景
Channel 适合的场景是，只能用它做一些小范围的内部订阅，整个功能模块独立都是自己做的不对外提供，在这个模块内需要一个单点的订阅。如果有看 Compose 的源码会发现会有挺多地方就是使用的 Channel 做单点订阅。
Channel 目前更多的是作为 Flow 的下层 API 支持，事件订阅应该使用 SharedFlow 而不是 Channel。

 */