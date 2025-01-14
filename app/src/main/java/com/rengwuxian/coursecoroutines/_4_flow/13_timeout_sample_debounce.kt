package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        emit(0)
        delay(500)
        emit(1)
        delay(800)
        emit(2)
        delay(900)
        emit(3)
        delay(1000)
    }
    scope.launch {
        // try {
        //     flow1.timeout(1.seconds).collect { println("1: $it") }
        // } catch (e: TimeoutCancellationException) {
        //     // 接收数据超时处理
        //     println("time out")
        // }
        // flow1.sample(1.seconds).collect { println("2: $it") }
        flow1.debounce(1.seconds).collect { println("3: $it") }
    }
    delay(10000)
}

/**
 * 如果要做事件点击的去抖动，我们可以自定义 Flow 操作符：
 */
fun <T> Flow<T>.throttle(timeWindow: Duration): Flow<T> = flow {
    var lastTime = 0L
    collect {
        if (System.currentTimeMillis() - lastTime > timeWindow.inWholeMilliseconds) {
            emit(it)
            lastTime = System.currentTimeMillis()
        }
    }
}

/*

timeout()
timeout() 会从 Flow 调用 collect() 开始计时，当超过设定的时间 Flow 还没结束并且没有发出下一条数据，就会抛出 TimeoutCancellationException；如果在设定时间内有收到数据，就会重新开始计时。
可以捕获TimeoutCancellationException，处理超时的情况

sample()
sample() 会从 Flow 调用 collect() 开始，每隔设定时间内发送过来的数据，只把最新的一条保留接收，其他数据就会被丢弃。适合用在固定时间点刷新的场景，将刷新点之前发送的所有数据只取最新的。

debounce()
debounce() 会从 Flow 调用 collect() 开始，如果在设定时间内接收到新数据，就会将旧数据丢弃新数据开启新一轮的等待，直到没有新数据超过设定时间了才发送到下游。
debounce() 是不适合做点击事件的去抖动的，正常点击事件的响应需要及时的，连续的点击我们应该响应第一次。如果用 debounce() 就会出现需要等待到设定时间超时了才响应点击，会出现响应延迟的问题，在用户体验上是比较差的。

 */