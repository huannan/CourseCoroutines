package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
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
  }.map { it * 2 }.retry(3) {
    it is RuntimeException
  }/*.retryWhen { cause, attempt ->  }*/
  scope.launch {
    try {
      flow1.collect {
        println("Data: $it")
      }
    } catch (e: TimeoutException) {
      println("Network error: $e")
    } catch (e: RuntimeException) {
      println("RuntimeException: $e")
    }
  }
  delay(10000)
}

/*

retry()、retryWhen() 和 catch() 操作符一样也是针对上游异常时会触发的操作符，但和 catch() 不同的是，当上游 Flow 异常时 retry()、retryWhen() 可以根据需要选择重启或不重启 Flow。
重启的是整个上游 Flow（包括 retry()、retryWhen() 之前的操作符）的流程，对下游是无感知的；不选择重启则会将异常继续往下抛到下游。

retry()
指定重启次数，超过就将异常往下抛
指定是否重启的条件，条件返回 true 会重启，返回 false 会直接将异常往下抛

retryWhen { cause, attempt ->  }
cause：异常的原因
attempt：已经重试的次数
 */