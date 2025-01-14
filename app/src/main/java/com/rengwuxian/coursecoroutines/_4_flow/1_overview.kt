package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
  // StateFlow // state flow
//  SharedFlow // event flow
//  Flow // data flow
//  Channel
//  async {  }
}

/*

Channel 和 Flow 整体上它的内容可以总结为一个子：流。数据流、事件流、状态流。

StateFlow 简介
在开发里面最常用的是 StateFlow，StateFlow 提供状态订阅，可以存储状态，当状态的值发生改变的时候就会通知到所有订阅的位置，就能实现界面自动更新之类的自动化操作。

SharedFlow 简介
StateFlow 内部是用 SharedFlow 实现，SharedFlow 也是一种订阅工具，但它提供的是事件订阅而不是状态订阅。

事件订阅和状态订阅的区别之一：
事件订阅：在一个事件触发之后再进行事件的订阅，这个事件原则上就不用推送到订阅者那边，当然也可以配置成依然推送
状态订阅：在状态更新之后发生的状态订阅，状态肯定会推送到订阅者

Flow 简介
SharedFlow 内部是用 Flow 实现，Flow 并不是一个订阅模型，不像 StateFlow 是状态订阅工具，也不像 SharedFlow 是事件流订阅工具，它事实上是一个数据流工具。
将状态流 StateFlow、事件流 SharedFlow 和数据流 Flow 区分开来是为了更方便理解区别。

Channel 简介
还有一个比 Flow 更基础的概念 Channel，Channel 跟 Flow 不完全是一个体系的，但实现上 Channel 是 Flow 下层的一个关键支撑。
Flow 核心关注点是数据流，而 Channel 事实上是一个协程间协作的工具，它提供的是在协程之间传递数据的功能。
可以将 Channel 理解为支持多条数据的 async()，async() 是一次性的，Channel 可以多次发送数据让别的协程使用。
如果你的数据流并不需要跨线程，就应该用 Flow 而不是 Channel，否则会容易引来一些性能上的问题，Channel 也没有 Flow 灵活和方便好用。

 */