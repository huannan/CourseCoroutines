package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  scope.launch {
    flow1.withIndex().collect { (index, data) ->
      println("1: $index - $data")
    }
    flow1.collectIndexed { index, value ->
      println("1: $index - $value")
    }
  }
  delay(10000)
}

/*

withIndex() 会给每个 Flow 发送的元素加上一个序号，序号从0开始。
collectIndexed() 也是给元素加上编号，只不过它是在收集的时候加编号，withIndex() 可以在中间流程加编号。

 */