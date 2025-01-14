package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3)
  scope.launch {
    flow1.customOperator().collect { println("1: $it") }
    flow1.double().collect { println("2: $it") }
  }
  delay(10000)
}

fun <T> Flow<T>.customOperator(): Flow<T> = flow {
  collect {
    emit(it)
    emit(it)
  }
}

fun Flow<Int>.double(): Flow<Int> = channelFlow {
  collect {
    send(it * 2)
  }
}

/*

自定义 Flow 操作符
自定义 Flow 操作符其实就是用一个现成的 Flow 对象来创建另一个 Flow 对象，自定义操作符其实就是一个 Flow 的扩展函数。

自定义 Flow 操作符主要有以下步骤：
1. 定义 Flow 的扩展函数，提供入参类型和返回值类型（具体类型或泛型）
2. 在函数体用 flow() 或 channelFlow() 创建一个空的 Flow 对象
3. 在函数体调用 collect() 收集上游的数据
4. 在 collect() 内调用 emit() 或 send()，此时就连接了上游和下游
5. 基于上面的基础自定义 Flow 处理发送数据到下游

 */