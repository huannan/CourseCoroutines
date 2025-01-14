package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val list = buildList {
//    while (true) {
      add(getData())
//    }
  }
  for (num in list) {
    println("List item: $num")
  }
  val nums = sequence {
    while (true) {
      yield(1)
    }
  }.map { "number $it" }
  for (num in nums) {
    println(num)
  }

  val numsFlow = flow {
    while (true) {
      emit(getData())
    }
  }.map { "number $it" }
  val scope = CoroutineScope(EmptyCoroutineContext)
  scope.launch {
    numsFlow.collect {
      println(it)
    }
  }
  delay(10000)
}

suspend fun getData(): Int {
  return 1
}

/*

Flow 的功能定位
Channel 是一个跨协程传递数据的工具，随着 Flow 的推出，Channel 更多的是作为底层工具来使用，如果需要事件流和数据流更推荐使用 Flow。Channel 现在更多的是作为 Flow API 的关键底层支撑，提供给 Flow 跨协程的能力，从功能上 Flow 也更完整，比如后续也推出了 SharedFlow 处理事件流和 StateFlow 处理状态流。
Flow 的功能定位可以将它看成是一个协程版的 Sequence，Sequence 的定位是提供一个边生产边消费的数据序列，Flow 提供了一个支持挂起函数的数据流。


惰性生产机制：Sequence
Sequence 是一种机制而不是一种新的数据结构，可以将它看成一个队列 Queue，但和 Queue 的区别是，Queue 会将所有数据都提前准备好，而 Sequence 是用完一条数据才生产下一条数据，简单说就是惰性的，没有所有元素的概念不会等待所有元素都准备好才处理数据，而是提供一个生产数据的机制，在需要的时候用这个机制生产数据。
有了 Sequence 惰性的机制，当我们在实际项目中比如需要获取一次网络数据就处理一次网络数据的场景，相比 List 在处理速度上就会快得多。
但 Sequence 仅支持内置的 yield() 和 yieldAll() 两个挂起函数使用，因为 SequenceScope 被标记了 @RestrictsSuspension 注解。
如果我们要用挂起函数又需要有 Sequence 惰性机制的能力，这时候就要用 Flow。

// 获取到 List 对象时已经装载了两个数据
val list = buildList {
	add(1)
	add(2)
}
for (num in list) {
	println("List item: $num")
}
// 获取到 Sequence 对象时是没有数据的，会在使用时才生产添加数据并消费数据
// 用一条才生产一条，比如下面两个元素，用完第一个元素后才生产第二个元素
val nums = sequence {
	// 在这里只提供了生产数据的机制，在需要使用数据时才生产
	yield(1) // 相当于 add 添加元素
	yield(2)
}
for (num in nums) {
	println(num)
}


挂起式的 Sequence：Flow
在机制上 Flow 和 Sequence 是一样的，所以也是为什么把 Flow 说成是协程版的 Sequence。
Flow 通过 emit() 发送数据，在协程通过 collect() 接收生产的数据。


 */
