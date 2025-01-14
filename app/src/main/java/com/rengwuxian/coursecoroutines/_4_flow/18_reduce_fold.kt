package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  val list = listOf(1, 2, 3, 4, 5)
  list.reduce { acc, i -> acc + i }.let { println("List reduced to $it") }
  list.runningReduce { acc, i -> acc + i }.let { println("New List: $it") }
  list.fold(10) { acc, i -> acc + i }.let { println("List folded to $it") }
  list.fold("ha") { acc, i -> "$acc - $i" }.let { println("List folded to string: $it") }
  list.runningFold("ha") { acc, i -> "$acc - $i" }.let { println("New String List: $it") }
  scope.launch {
    val sum = flow1.reduce { accumulator, value -> accumulator + value }
    println("Sum is $sum")
    flow1.runningReduce { accumulator, value -> accumulator + value }
      .collect { println("runningReduce: $it") }
    flow1.fold("ha") { acc, i -> "$acc - $i" }.let { println("Flow folded to string: $it") }
    flow1.runningFold("ha") { acc, i -> "$acc - $i" }
      .collect { println("Flow.runningFold(): $it") }
    flow1.scan("ha") { acc, i -> "$acc - $i" }
      .collect { println("Flow.scan(): $it") }
  }
  delay(10000)
}

/*

reduce()、runningReduce()
reduce() 就是等差数列的操作符，把所有元素融合到一起来计算一个类型不变的最终的结果；如果只有一个元素不会计算会直接返回。
runningReduce() 是一个运行中的 reduce()，并返回一个新的元素储存对象，能拿到计算过程的每个步骤的结果。

Flow 的 reduce() 会在内部调用 collect() 直接启动 Flow 的收集过程，直到所有元素都处理完成并返回最终对应类型的结果；不关心中间过程只关注结果。
Flow 的 runningReduce() 会返回一个新的 Flow 对象，计算过程中每一步的结果都会作为一条数据发送，需要手动 collect() 收集发送的每一步计算后的结果；会参与计算的每个过程。


fold()、runningFold()
fold() 和 reduce() 的效果是类似的，它们的区别是：
* fold() 需要提供一个初始值，即使是只有一个元素也会和初始值计算；reduce() 只有一个元素时不会计算会直接返回
* fold() 提供的初始值类型可以和计算的元素不同，但整个计算过程和返回结果的类型要和初始值的类型一致

Flow 的 fold() 和 runningFold() 也和列表的一致

 */