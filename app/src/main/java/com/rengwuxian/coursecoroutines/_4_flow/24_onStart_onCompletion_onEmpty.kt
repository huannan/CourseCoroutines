package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val flow1 = flow<Int> {
        // 上游的try-catch无法捕获onStart里面的异常，因为onStart是发送数据之前的，所以无法捕获
        // 此处只是示例代码，实际不能用try-catch包住emit并且不抛出
        try {
            for (i in 1..5) {
                emit(i)
            }
        } catch (e: Exception) {
            println("try / catch: $e")
        }
    }.onStart {
        println("onStart 1")
        // throw RuntimeException("onStart error")
    }
        // 因为onStart是调用上游的collect之前就触发了回调，所以下面的onStart会先于上面的onStart回调
        .onStart { println("onStart 2") }
        .onCompletion {
            println("onCompletion: $it")
        }.onEmpty { println("onEmpty") }
        // 但是可以使用catch来捕获onStart里面的异常
        .catch { println("catch: $it") }
    scope.launch {
        flow1.collect {
            println("Data: $it")
        }
    }
    delay(10000)
}

/*

onStart()
onStart() 操作符是负责监听 Flow 收集流程的开始事件的，确切的说是 Flow 调用 collect() 后数据生产之前会触发。

onCompletion()
onCompletion() 操作符是负责监听 Flow 的结束，即所有数据都发送完结束或异常结束时会触发。异常结束能触发但不会拦截异常。

onEmpty()
onEmpty() 操作符负责监听 Flow 正常结束且没有发送过一条数据的时候被触发，异常结束不会触发。
 */