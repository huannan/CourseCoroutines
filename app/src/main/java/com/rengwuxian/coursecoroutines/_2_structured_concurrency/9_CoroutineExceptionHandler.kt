package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    scope.launch(handler) {
        launch {
            launch {
                println("Grand child started")
                delay(3000)
                println("Grand child done")
            }
            delay(1000)
            throw RuntimeException("Error!")
        }
        launch {

        }
        println("Parent started")
        delay(3000)
        println("Parent done")
    }
    delay(10000)
}

/*
如果我们想捕获协程的异常，可以用 CoroutineExceptionHandler 传给最外面的父协程。
需要注意的是，CoroutineExceptionHandler 只能设置到最外面的父协程，设置到内层协程是没用的。
 */