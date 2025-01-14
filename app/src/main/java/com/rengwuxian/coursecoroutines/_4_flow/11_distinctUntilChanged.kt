package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf("rengwuxian", "RengWuXian", "rengwuxian.com")
  scope.launch {
    flow1.distinctUntilChanged().collect { println("1: $it") } // == equals()
    flow1.distinctUntilChanged { a, b -> a.uppercase() == b.uppercase() }.collect { println("2: $it") }
    flow1.distinctUntilChangedBy { it.uppercase() }.collect { println("3: $it") }
  }
  delay(10000)
}

/*

distinctUntilChanged() 和 distinctUntilChangedBy() 两个操作符也是用来过滤的，不过它们不是根据元素自身来过滤，而是用来去重的，即连续发送了两个重复的数据，新的数据就不再发送，间隔非连续的重复数据还是可以接收到。

distinctUntilChanged()
去除连续重复的数据，有间隔的还是可以接收到
内部是用的 kotlin 的 == 即 equals() 判断的
distinctUntilChanged() 也可以自定义去重逻辑

distinctUntilChangedBy() 可以对元素进行转换后做去重，lambda 是转换为对应的处理后生成 key，不会对原数据有影响
 */