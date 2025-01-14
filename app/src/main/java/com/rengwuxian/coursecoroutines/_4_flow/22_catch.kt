package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.unstableGitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flow {
    for (i in 1..5) {
      // 数据库读数据
      // 网络请求
      if (i == 3) {
        throw RuntimeException("flow() error")
      } else {
        emit(i)
      }
    }
  }.catch {
    println("catch(): $it")
    emit(100)
    emit(200)
    emit(300)
//    throw RuntimeException("Exception from catch()")
  }/*.onEach { throw RuntimeException("Exception from onEach()") }
    .catch { println("catch() 2: $it") }*/
  scope.launch {
    try {
      flow1.collect {
        /*val contributors = unstableGitHub.contributors("square", "retrofit")
        println("Contributors: $contributors")*/
        println("Data: $it")
      }
    } catch (e: TimeoutException) {
      println("Network error: $e")
    }
  }
  delay(10000)
}

/*

总结下 catch() 操作符的特点：
* 能捕获 Flow 上游抛出的异常，只有一个 catch() 操作符时，catch() 操作符后面的操作符如果出现异常是不能捕获
* 效果类似于用 try-catch 包住 Flow 生产数据的代码块，但又不会捕获 emit() 产生的异常，让异常能到达下游正常走异常流程，符合 [不要用 try/catch 包住 emit()] 的原则
* 不会捕获 CancellationException 异常，保证协程取消流程正常
* 有多个 catch() 操作符时，最靠近下游的 catch() 操作符能捕获两个 catch() 之间抛出的异常

catch() 操作符的作用是：当上游发生异常时接管后续数据发送工作的操作符。catch() 操作符就类似于当上游的管道坏了，用一根侧管开始接管后续的数据生产，下游是无感知的。
在使用 Flow 时可能生产数据的代码是别人提供封装好的，我们没法用 try-catch 从内部源码去改动，那么此时可以使用catch() 操作符

catch() 操作符的适用场景：无法修改 Flow 代码从内部通过 try-catch 针对异常修复 Flow 流程的时候，可以用 catch() 操作符做接管。
需要注意的是，[catch() 操作符接管上游数据的发送] 并不是指的完全接管上游的数据发送，而是在上游发生异常时接管做一些收尾的工作告知下游处理。

try-catch 和 catch() 操作符的选择：
在能修改 Flow 代码的前提下，发生异常时能用 try-catch 就用 try-catch，但要遵循 try-catch 不能把 emit() 包住；catch() 操作符的接管是一种无法修复 Flow 的无奈之举。

 */