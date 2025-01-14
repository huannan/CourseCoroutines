package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  scope.launch {
    flow1.onEach {
      println("onEach 1: $it")
    }.onEach {
      println("onEach 2: $it")
    }.filter {
      it % 2 == 0
    }.onEach {
      println("onEach 3: $it")
    }.collect {
      println("collect: $it")
    }
  }
  delay(10000)
}

/*

onEach() 是一个数据监听器作用的操作符，会返回一个新的 Flow 但会把数据原样返回到下游。比如数据从上游发送到下游时经过它可以做一些日志打印的操作。
可以多次调用 onEach()

 */