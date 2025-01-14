package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  scope.launch {
    // [1, 2]、[3, 4]、最后一次是[5]
    flow1.chunked(2).collect { println("chunked: $it") }
  }
  delay(10000)
}

/*

chunked() 需要提供一个分块后元素数量的数值，把 Flow 分块然后输出一个新的、元素类型是 List 的 Flow，每个 List 装载的就是分块后的数据。

 */