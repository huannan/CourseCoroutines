package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        // 不能在Flow中用withContext包住emit
        // withContext(Dispatchers.IO) {
        //     emit(10)
        // }
        println("CoroutineContext in flow(): ${currentCoroutineContext()}")
        for (i in 1..2) {
            emit(i)
        }
    }
        .map {
            println("CoroutineContext in map() 1: ${currentCoroutineContext()}")
            it * 2
        }
        .flowOn(Dispatchers.IO)
        .flowOn(Dispatchers.Default)
        .map {
            println("CoroutineContext in map() 2: ${currentCoroutineContext()}")
            it * 2
        }
        .flowOn(newFixedThreadPoolContext(2, "TestPool"))

    val flow2 = channelFlow {
        println("CoroutineContext in channelFlow(): ${currentCoroutineContext()}")
        for (i in 1..5) {
            send(i)
        }
    }
        .map { it }
        .flowOn(Dispatchers.IO)

    scope.launch {
        flow1.map {
            it + 1
        }.onEach {
            println("Data: $it - ${currentCoroutineContext()}")
        }.flowOn(Dispatchers.IO)
            .collect {}

        flow2.collect()
    }
    /*flow1.map {
      it + 1
    }.onEach {
      println("Data: $it - ${currentCoroutineContext()}")
    }.launchIn(scope + Dispatchers.IO)*/
    delay(10000)
}

/*

flowOn() 操作符是用来定制 Flow 运行的 CoroutineContext，大多数时候是用它来切线程。它只会关注 Flow 的上游，即如果使用 flowOn() 切换线程，那么 flowOn() 上游发送的数据才会在 flowOn() 指定的线程运行，下游还是在启动协程时所在的线程运行。

CoroutineContext 的融合
* 当我们尝试连续使用 flowOn() 时，Flow 会出现 [融合] 的情况，即两个 flowOn() 会共用同一个 Flow 对象，但它们的 CoroutineContext 会合并到一起（CoroutineContext 类型不同时合并，类型相同时只保留一个），flowOn() 右边的 CoroutineContext 加到左边 flowOn() 的 CoroutineContext
    冲突的情况下会保留左边的
* flowOn还会跟channelFlow融合

flowOn() 为什么只切换上游的 CoroutineContext？（与catch() 操作符类似，只捕获上游的异常）
原因也很简单，使用 Flow 是为了将数据的生产和数据的收集拆分开，那就有可能由多个开发人员负责，比如一个负责开发生产数据的流程，一个负责开发收集数据的流程，如果 flowOn() 把上下游的 CoroutineContext 都切换了，就会导致下游的代码行为变得难以预期，开发收集数据流程的开发以为我的程序会在这个协程的 CoroutineContext 运行，实际上被上游切换了。

withContext() 和 flowOn() 都可以切换线程，二者的选择主要考虑切换线程的颗粒度：
* 如果只想在某一个操作符切换线程（不能包住emit），那可以用 withContext()
* 如果针对的是整个 Flow 上游生产流程或多个 Flow 操作符切换线程，用 flowOn() 会比较方便，因为不需要对每个操作符都用 withContext() 切换线程


flowOn() 只管上游的线程切换，不做其他处理的情况下，下游会在启动协程所在的线程运行；
如果我们想下游不在启动协程所在的线程运行，有两种官方推荐的实现方案：
* 在 onEach() 实现具体收集数据的逻辑，flowOn() 切换线程，collect() 空实现
* 在 onEach() 实现具体收集数据的逻辑，launchIn() 切换线程

 */