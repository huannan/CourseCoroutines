package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  val flow2 = flow {
    delay(100)
    emit(1)
    delay(100)
    emit(2)
    delay(100)
    emit(3)
  }
  scope.launch {
    flow1.map { if (it == 3) null else it + 1 }.collect { println("1: $it") }
    // 等价于 flow.map { it + 1 }.filterNotNull().collect {}
    flow1.mapNotNull { if (it == 3) null else it + 1 }.collect { println("2: $it") }
    flow1.map { if (it == 3) null else it + 1 }.filterNotNull()
    flow1.filter { it != 3 }.map { it + 1 }
    flow2.mapLatest { delay(120); it + 1}.collect { println("3: $it") }
  }
  delay(10000)
}

/*

map() 需要让你提供一个算法，把上游过来的数据转换成另一个数据发送到下游。
mapNotNull() 是先 map() 转换后的数据如果为空元素就过滤掉，等价于 map() 后 filterNotNull() 的结合。
mapLatest() 是一个 [有了新数据就停止旧数据的转换流程] 的 map() 的变种。
  一般的 Flow 操作符是同步的，即发送了数据到下游才开始下一条数据的发送生产，mapLatest() 是异步操作符，会在处理这一条上游数据的过程中，上游依然可以生产下一条数据；如果下一条上游到来了，它会取消正在处理的上一条数据，直接开始处理这条上游的新数据，即 mapLatest() 只关注最新的数据。

 */