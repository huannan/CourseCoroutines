package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

// NonCancellable本质上是一个单例Job，切断了父子协程的关系，一般配合withContext使用
// 使用场景：
// 1. 取消前的收尾清理工作：协程被调用 cancel() 后真正退出执行之前的工作，在处理清理工作时不希望其他挂起函数中断收尾清理工作
// 2. 不好收尾的业务工作：比如写文件、数据库等不想被中途停止，写入的东西还要撤销就是很麻烦的事情
// 3. 跟当前业务无关的其他工作：比如写日志，协程的任务被取消了，日志还是会需要记录的，这种场景就是用 launch(NonCancellable) 来处理
fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  var childJob: Job? = null
  var childJob2: Job? = null
  val newParent = Job()
  val parentJob = scope.launch {
    childJob = launch(NonCancellable) {
      println("Child started")
      delay(1000)
      println("Child stopped")
    }
    println("childJob parent: ${childJob?.parent}")
    childJob2 = launch(newParent) {
      println("Child started")
      // 不好收尾的业务工作
      writeInfo()
      // 跟当前业务无关的其他工作
      launch(NonCancellable) {
        // Log
      }
      if (!isActive) {
        // 取消前的收尾清理工作
        withContext(NonCancellable) {
          // Write to database (Room)
          delay(1000)
        }
        throw CancellationException()
      }
      try {
        delay(3000)
      } catch (e: CancellationException) {

        throw e
      }
      println("Child 2 started")
      delay(3000)
      println("Child 2 stopped")
    }
    println("Parent started")
    delay(3000)
    println("Parent stopped")
  }
  delay(1500)
  newParent.cancel()
  delay(10000)
}

suspend fun writeInfo() = withContext(Dispatchers.IO + NonCancellable) {
  // write to file
  // read from database (Room)
  // write data to file
}

suspend fun uselessSuspendFun() {
  Thread.sleep(1000)
}