package com.rengwuxian.coursecoroutines._5_collaboration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

val kotlinLocalString = ThreadLocal<String>()

fun main() = runBlocking {
    val scope = CoroutineScope(EmptyCoroutineContext)
    scope.launch {
        kotlinLocalString.set("test")
        delay(2000)
        // 注意这里的线程不一定是原来的线程，所以 kotlinLocalString.get() 可能是 null或者其它值
        println("value1: ${kotlinLocalString.get()}")

        val stringContext = kotlinLocalString.asContextElement("rengwuxian")
        withContext(stringContext) {
            delay(2000)
            println("value2: ${kotlinLocalString.get()}")
        }
    }
    delay(10000)
}

/*

ThreadLocal：线程的局部变量，每个线程都有自己的变量副本，不会被其他线程所共享。
定位：跨方法/函数的，在方法/函数之间共享的，但只在线程中有效，处理就无效。用于线程的结构化管理，即在同一个线程执行的多个方法之间共享数据，只针对当前线程有效，通常ThreadLocal会定义成静态变量。

变量的类型：
局部变量：方法内部声明的变量，方法内有效
类的字段：类中声明的变量
静态变量：带static关键字的变量，全局有效
线程的局部变量：介于局部变量和静态变量之间的一种变量，范围比局部变量大；比静态变量小。

协程中的CoroutineContext
相当于线程中的ThreadLocal，CoroutineContext是协程的局部变量，由于协程是结构化管理的，一般不用ThreadLocal

协程中与老Java代码的ThreadLocal的交互：
1. asContextElement
2. withContext

 */