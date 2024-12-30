package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

// 协程的结构化并发，其实就是父子协程的关系管理，即管理父子协程的正常取消和异常的生命周期的管理
fun main() = runBlocking<Unit> {
    // 启动线程，可以拿到线程对象，可以完成对「单独业务线」的管理，比如获取元数据或结束线程等；但是协程里面不存在完全等价物
    val thread = thread { }
    // thread.start()
    // thread.name

    val scope = CoroutineScope(Dispatchers.IO)
    // job甚至能延迟启动协程
    val job = scope.launch(start = CoroutineStart.LAZY) { }
    // job只是控制协程的流程，但不是协程的全部
    // job.start()
    // job.cancel()
    // job.cancelChildren()
    // job.join()
    // job.parent
    // job.children
    // job.isActive
    // job.isCancelled
    // job.isCompleted

    var innerJob: Job? = null
    // CoroutineScope是协程的顶级管理器，可以拿到协程的所有属性和功能，子协程可以沿用Scope的Context
    var innerScope: CoroutineScope? = null
    // 协程launch返回Job是为了做责任的区分，而不是返回StandaloneCoroutine，StandaloneCoroutine实现了Job接口
    // Job可以取消、join、获取协程状态、获取父子协程的Job对象、取消子Job
    val outerJob = scope.launch(Dispatchers.Default) {
        innerJob = coroutineContext[Job]
        innerScope = this
        // 因此在在实践中，使用Scope启动新协程
        launch {

        }
    }
    // 因此在在实践中，使用Job取消子协程
    // outerJob.cancel()

    // Deferred也是Job的子类
    val deferred = scope.async {

    }

    println("outerJob: $outerJob")
    println("innerJob: $innerJob")
    println("outerJob === innerJob: ${outerJob === innerJob}")
    println("outerJob === innerScope: ${outerJob === innerScope}")
}

// 总结：Scope和Job是从属关系，而不是并列关系，Scope是大总管，所以也可以把Scope看错一个协程

// 一个协程的概念
// 技术角度：把Scope或Job看作一个协程
// 广义角度：以大括号看作一个协程