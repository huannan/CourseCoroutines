package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

// 讨论父子协程的时候应该把Job看作一个协程，
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {

    // 父协程和子协程的父子关系的确定本质：真正决定父子协程关系的是启动协程时所使用的 Job 对象。内部协程用外部协程的CoroutineScope的来启动，CoroutineScope内部的Job和子协程的Job的绑定
    // CoroutineScope创建的时候内部会创建一个空的Job，用于启动协程的时候进行绑定
    // launch可以传入一个Job，用于父子协程关系的确定
    // val scope = CoroutineScope(EmptyCoroutineContext)
    // var innerJob: Job? = null
    // val job = scope.launch {
    //     // 也可以写成 this.launch {}
    //     // 用scope.launch，启动的是兄弟协程
    //     // scope.launch {  }
    //     innerJob = launch {
    //         delay(100) // 子协程做个延时，避免协程执行结束自动解绑父子协程关系
    //     }
    // }
    // val children = job.children
    // println("children count: ${children.count()}")
    // println("innerJob === children.first(): ${innerJob === children.first()}")
    // println("innerJob.parent === job: ${innerJob?.parent === job}")

    // 所有协程之间（包括父子协程、兄弟协程）在执行上都是并行关系，但是父协程会等待所有子协程执行完毕才结束（结构化结束）
    val scope = CoroutineScope(EmptyCoroutineContext)
    val initJob = scope.launch {
        launch { }
        launch { }
    }
    scope.launch {
        initJob.join()
        // 需要依赖initJob完成的业务逻辑
    }

    var innerJob: Job? = null
    val job = scope.launch {
        launch(Job()) {
            delay(100)
        }

        // val customJob = Job()
        // innerJob = launch(customJob) {
        //     delay(100)
        // }
    }

    val startTime = System.currentTimeMillis()
    job.join()
    val duration = System.currentTimeMillis() - startTime
    println("duration: $duration")
}

// 父协程和子协程的父子关系的确定本质：真正决定父子协程关系的是启动协程时所使用的 Job 对象

// 父子协程关系的定制（慎用）
// 可以传入自定义Job()
// 可以用不同的Scope启动

// 结构化结束：所有协程都是并行关系，但是父子协程的关系会导致父协程会等待子协程执行完成之后结束，哪怕父协程的代码已经执行完成了
// 实用场景：通过job.join()等待初始化协程（包括所有子协程）结束
