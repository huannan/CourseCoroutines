package com.rengwuxian.coursecoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        withContext(coroutineContext) {
            launch { }
        }
        withContext(Dispatchers.IO) {
            launch { }
        }
        coroutineScope {

        }
    }
    delay(10000)
}

/*

withContext()和coroutineScope()功能几乎一致，内部也是开启一个子协程，也是串行，只不过withContext()可以临时切换CoroutineContext

 */