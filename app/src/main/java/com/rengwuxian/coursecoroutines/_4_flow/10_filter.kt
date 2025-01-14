package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, null, 3, null, 4, 5) // Int?
  val flow2 = flowOf(1, 2, 3, "rengwuxian.com", "扔物线",
    listOf("A", "B"), listOf(1, 2))
  scope.launch {
    flow1.filterNotNull().filter { it % 2 == 0 }.collect { println("1: $it") }
    flow1.filterNotNull().filterNot { it % 2 == 0 }.collect { println("2: $it") }
    // filterIsInstance() 已经指定了过滤输出字符串的 List，但最终 Int 类型的 List 也输出了，原因是 reified 只能解决外层的泛型类型判断，List 内部的类型会被类型擦除无法判断，所以都会输出。
    flow2.filterIsInstance<List<String>>().collect { println("3: $it") }
    // 等价于
    flow2.filterIsInstance(List::class).collect { println("4: $it") }
    flow2.filter { it is List<*> && it.firstOrNull()?.let { item -> item is String } == true }
      .collect { println("5: $it") }
  }
  delay(10000)
}

/*

filter()
filter() 操作符可以留下符合条件的数据，过滤不符合条件的数据

filterNot()
filterNot() 和 filter() 逻辑相反，filterNot() 留下不符合条件的数据，过滤符合条件的数据

filterNotNull()
filterNotNull() 会把非空的数据留下，过滤掉为 null 的数据

filterIsInstance()
filterIsInstance() 是把符合指定类型的元素留下，过滤掉不符合类型的元素

 */