package com.rengwuxian.coursecoroutines._5_collaboration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    val job1 = launch {
        delay(1000)
        println("job1 done")
    }
    val job2 = launch {
        delay(2000)
        println("job2 done")
    }
    val deferred = async {
        delay(500)
        "rengwuxian"
    }
    val channel = Channel<String>()
    launch {
        delay(200)
        channel.receive()
    }

    scope.launch {
        val result = select {
            // onJoin() 是在 select() 才能使用的函数，onJoin() 会监听 Job 的结束（Job 的正常结束或取消），并执行 onJoin() 大括号的代码
            job1.onJoin {
                // onJoin() 也是可以调用挂起函数，但不影响它最终作为 select() 返回值
                delay(2000)
                1
            }

            job2.onJoin {
                2
            }

            deferred.onAwait {
                "3$it"
            }

            channel.onSend("haha") {
                4
            }
            // 需要注意的是，在 select() 函数同一个对象只能使用一次 onXxx() 监听。
            // channel 的 onSend()、onReceive()、onReceiveCatching() 只能选其一，不能同时应用
            // channel.onReceive {
            //
            // }
            // channel.onReceiveCatching {
            //
            // }

            // select() 还可以使用 onTimeout() 设置超时时间，如果在设定超时时间内所有协程都还没有返回结束，就执行 onTimeout() 的返回值
            onTimeout(1.seconds) {
                5
            }
        }
        println("Result: $result")
    }
    delay(10000)
}

/*

select()：先到先得
select() 函数是一个挂起函数，它也是用于协程之间的互相等待可以监听多个 Job，最快返回结果的 Job 会执行 onJoin() 回调，onJoin() 的返回值会作为 select() 最终的返回值。

 */