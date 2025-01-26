package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    // 需要提供状态初始值，返回值定义成val即可
    val name = MutableStateFlow("rengwuxian")
    val flow1 = flow {
        emit(1)
        delay(1000)
        emit(2)
        delay(1000)
        emit(3)
    }
    name.asStateFlow()
    val state = flow1.stateIn(scope)
    scope.launch {
        name.collect {
            println("State: $it")
        }
    }
    scope.launch {
        delay(2000)
        name.emit("扔物线")
    }
    delay(10000)
}

/*

StateFlow 的概念
* StateFlow 是一种特殊的 SharedFlow，SharedFlow 是把数据流的收集收窄到了事件流的订阅，StateFlow 则是进一步的收窄，从事件流的订阅收窄到了状态的订阅。
* StateFlow 实际上就是一个仅仅保存最新的一个事件的 SharedFlow 事件流，可以理解为可以订阅的状态

数据订阅步骤：
1. 把数据包起来，可以提供初始值
2. 数据的定义
3. 数据的更新

MutableStateFlow
支持外部更新状态的StateFlow

stateIn()
将 Flow 转换成 StateFlow

asStateFlow()
可以把可读写的 MutableStateFlow 转换为只读的 StateFlow，用来对外暴露的时候把写数据的功能给隐藏。

 */