package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    // 启动线程，可以拿到线程对象，可以完成对「单独业务线」的管理，比如获取元数据或结束线程等；但是协程里面不存在等价物
    // val thread = thread {  }

    val scope = CoroutineScope(Dispatchers.IO)
    // 延迟启动协程
    val job = scope.launch(start = CoroutineStart.LAZY) { }
    // job只是控制协程的流程，但不是协程的全部
    job.start()
    job.cancel()
    job.cancelChildren()
    job.join()
    job.parent
    job.children
    job.isActive
    job.isCancelled
    job.isCompleted

    var innerJob: Job? = null
    // CoroutineScope是协程的顶级管理器，可以拿到协程的所有属性和功能
    var innerScope: CoroutineScope? = null
    // 协程launch返回Job是为了做责任的区分，而不是返回StandaloneCoroutine，StandaloneCoroutine实现了Job接口
    // Job可以取消、join、获取协程状态、获取父子协程的Job对象、取消子Job
    val outerJob = scope.launch(Dispatchers.Default) {
        innerJob = coroutineContext[Job]
        innerScope = this
        launch {

        }
    }
    outerJob.cancel()

    scope.async {

    }

    println("outerJob: $outerJob")
    println("innerJob: $innerJob")
    println("outerJob === innerJob: ${outerJob === innerJob}")
    println("outerJob === innerScope: ${outerJob === innerScope}")
}