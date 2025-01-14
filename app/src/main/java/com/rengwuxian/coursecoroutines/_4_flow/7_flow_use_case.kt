package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)

    // 两个协程的 collect() 都完整拿到了数据。
    val numsFlow = flow {
        emit(1)
        delay(100)
        emit(2)
    }
    scope.launch {
        numsFlow.collect {
            println("A: $it")
        }
    }
    scope.launch {
        delay(50)
        numsFlow.collect {
            println("B: $it")
        }
    }

    scope.launch {
        // showWeather(weatherFlow)
        weatherFlow.collect {
            println("Weather: $it")
        }
        // log("done")
    }

    delay(10000)
}

// 只负责数据的生产获取
val weatherFlow = flow {
    while (true) {
        emit(getWeather())
        delay(60000)
    }
}

// 只负责数据的消费处理
suspend fun showWeather(flow: Flow<String>) {
    flow.collect {
        println("Weather: $it")
    }
    // 数据流都收集完执行结束了，才会执行后续的代码，否则会一直在 collect()
    println("done")
}

suspend fun getWeather() = withContext(Dispatchers.IO) {
    "Sunny"
}

/*

Flow 的工作原理
Flow 的核心工作原理：emit() 只是充当一个发送数据的占位符的作用，将 emit() 的执行替换成 collect() 执行的代码，就是 Flow 的核心工作原理。
Flow 的工作原理：由 Flow 对象提供数据流的生产逻辑，然后在收集流程里执行这套生产逻辑并处理每一条数据。


[热] 和 Flow 的 [冷] 指的是什么
* Channel 的 [热] 指的是 Channel 有自己独立的生产线，它调用一次 send() 就生产一条数据，跟是否调用 receive() 来取数据是无关的，没有调用 receive() 也能调用 send() 生产数据。
* Flow 的 [冷] 是指的 Flow 只提供生产规则，每次 collect() 都会有一次完整的生产流程。
简单理解就是，Channel 在你不取数据的时候就已经开启生产流程，它是独立的、统一化的生产流程；Flow 只有在 collect() 的时候才开始生产，每次 collect() 都分别进行的、互不干扰的、分散的生产流程。


Flow 的应用场景
了解了 Flow 的工作原理，那么什么时候我们会需要 Flow 呢？我们都说 Flow 是数据流，那什么是数据流？
数据流就是需要 [连续的处理同类型的数据]，当我们需要把数据生产和数据消费的功能拆分开处理的时候，就是 Flow 的应用场景。

比如只想在一个模块里提供一个 [持续获取并显示天气数据] 更新到界面的功能，但天气信息的持续获取我不想提供，我希望由外界提供给我，外界怎么获取天气数据、多久获取一次都不关心，这时候就能用 Flow
 */