package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val start = System.currentTimeMillis()
    val flow1 = flow {
        for (i in 1..5) {
            emit(i)
            println("Emitted: $i - ${System.currentTimeMillis() - start}ms")
        }
    }
        .buffer(1)
        .flowOn(Dispatchers.IO)
        .buffer(2)
        // .conflate()
        .map { it + 1 }
        .map { it * 2 }
    scope.launch {
        // flow1.collect {
        //     delay(1000)
        //     println("Data: $it")
        // }

        // 只保留第一条和最后一条，mapLatest内部是瞬间完成的，第一条还是会保留
        flow1.mapLatest { it }.buffer(0).collect {
            delay(1000)
            println("Data: $it")
        }

        // 只保留最后一条，因为collectLatest的代码块实际上是mapLatest的代码块，所以上游下一条数据的生产会打断mapLatest的数据生产
        flow1.collectLatest {
            delay(1000)
            println("Data: $it")
        }
    }
    delay(10000)
}

/*

flow的线性特性，flow是线性的，包括数据生产和处理的线性顺序。
* 对于每条数据：每条数据从上游生产到下游处理，遵循线性流程。
* 对于整个数据流：多条数据之间也是线性的，一条数据处理完毕后下一条数据才开始处理。

数据缓冲的原理
对于整个数据流，在某一条数据生产之后但是处理之前，就开始并行生产下一条数据，可以通过使数据的生产和发送不在同一个线程实现，比如flowOn()
上游生产快，下游处理慢，如果没有数据缓冲功能下游处理慢还是会卡住上游的生产；flowOn()是通过Channel实现的，自带数据缓冲功能

数据缓冲的配置buffer()/conflate()
虽然flowOn()可以配置数据生产的线程，但是不能配置数据缓冲，数据缓冲是通过buffer()配置，底层也是Channel（buffer和flowOn也会进行融合）
* capacity
* onBufferOverflow
buffer()可以单独使用，内部还是会通过切协程的方式实现缓冲效果
conflate()相当于配置了缓冲大小为1而且只缓冲最新一条数据

多个buffer()操作符的融合
缓冲溢出策略遵循右边优先的原则
* 如果右边buffer()不是BufferOverflow.SUSPEND，会右边buffer()会完全覆盖左边buffer()，相当于忽略左边buffer()
* 如果右边buffer()是BufferOverflow.SUSPEND，那么缓冲溢出策略会沿用左边buffer()的，缓冲大小的规则是：
    都不填：默认64
    其中一个填了：用其中一个
    两个都填了：两个相加

collectLatest()
相当于mapLatest().buffer(0).collect()
使用场景：下游数据处理完成之前上游不再转换新数据的场景
原理分析：
* mapLatest()的隐藏需求：mapLatest()是可以打断当前的数据生产的map()，当前数据的处理过程中下一条数据就可以生产，不然没法打断当前数据的处理，即需要多协程的支持，实际上mapLatest()的底层实现也是Channel，mapLatest()默认情况下也是有缓冲的
* mapLatest()的缓冲实际上跟「新数据打断当前数据的转换生产」这个核心特性无关，与「并发执行」附带特性有关，默认情况下mapLatest()是带缓冲的，即跟map()不一样的是mapLatest()不会因为下游慢而卡住上游的数据生产，mapLatest()会将数据缓冲下来
* 而mapLatest().buffer(0)的缓冲关闭会导致mapLatest()挂起，即在上一条数据还没处理完的时候再来新数据，新数据就不会被mapLatest()转换，而是等待上一条数据的collect()完成，即等待的是下游，而不会卡住上游。
* 因此上述代码运行结果是只有第一条和最后一条数据被下游处理打印，其它的都没来得及被mapLatest()转换，根本就没有进入缓冲里面。
注意：

 */