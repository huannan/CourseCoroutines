package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow {
        delay(500)
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
    }
    val flow2 = flow {
        delay(250)
        emit(4)
        delay(500)
        emit(5)
        delay(500)
        emit(6)
    }
    val mergedFlow = merge(flow1, flow2)
    val flowList = listOf(flow1, flow2)
    val mergedFlowFromList = flowList.merge()
    val flowFlow = flowOf(flow1, flow2) // flatten
    val concattedFlowFlow = flowFlow.flattenConcat() // concatenate
    val mergedFlowFlow = flowFlow.flattenMerge()
    // flow1.map { from -> (1..from).asFlow().map { "$from - $it" } }.flattenConcat()
    val concattedMappedFlow = flow1.flatMapConcat { from -> (1..from).asFlow().map { "$from - $it" } }
    val mergedMappedFlow = flow1.flatMapMerge { from -> (1..from).asFlow().map { "$from - $it" } }
    val latestMappedFlow = flow1.flatMapLatest { from -> (1..from).asFlow().map { "$from - $it" } }
    val combinedFlow = flow1.combine(flow2) { a, b -> "$a - $b" }
    val combinedFlow2 = combine(flow1, flow2, flow1) { a, b, c -> "$a - $b - $c" }
    flow1.combineTransform(flow2) { a, b -> emit("$a - $b") }
    val zippedFlow = flow1.zip(flow2) { a, b -> "$a - $b" }
    scope.launch {
        zippedFlow.collect { println(it) }
    }
    delay(10000)
}

/*

多个Flow的合并：
多个Flow的合并是将多个Flow的数据合并成一个Flow。包括：直接展开成一个Flow；把多个Flow的数据进行结合计算之后再生成一个新的Flow

多个Flow的直接合并
merge()
merge函数可用于合并两个Flow，将它们的数据统一发送。merge函数可以处理穿插发送的数据，按发送时间顺序转发

Flow的Flow的铺平/展开/合并
由于本身不是每个Flow都是现成的，而是一个一个生产出来的，所以展开分为两种：顺序展开、穿插式展开
flattenConcat()
顺序展开，在某一个Flow元素发送之后通过挂起协程的形式暂停生产Flow的Flow，去收集当前Flow并把数据转发出去，然后生产下一条Flow，如此类推
flattenMerge()
穿插式展开，在收集当前Flow并把数据转发过程中，不暂停生产Flow的Flow，继续生产并收集下一条Flow

如何将一个Flow转换成一个生产Flow的Flow，然后铺平/展开/合并
flatMapConcat()
相当于用map()去转换成一个生产Flow的Flow，然后再通过flattenConcat()顺序展开铺平
flatMapMerge()
相当于用map()去转换成一个生产Flow的Flow，然后再通过flattenMerge()穿插式铺平
flatMapLatest()
与flatMapConcat()类似，也是顺序展开，但是不卡住Flow的Flow的生产，一旦下一个Flow生产出来就终止当前Flow的收集而去收集下一个Flow

多个Flow的数据合并
combine()
可以将两个Flow的数据进行结合，用过的元素会重复使用
zip()
可以将两个Flow的数据进行结合，但是用过的元素不会重复使用，像「拉链」一样
combineTransform()
类似map()和transform()的区别，可以自定义combine()的发送规则

 */