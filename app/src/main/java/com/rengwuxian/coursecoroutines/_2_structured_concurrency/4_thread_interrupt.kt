package com.rengwuxian.coursecoroutines._2_structured_concurrency

import kotlinx.coroutines.runBlocking

// 线程的取消：
// 交互式取消：interrupt()，让线程自杀，只是设置一个中断状态的标记，线程中需要插入检查点（建议在循环的开始或者耗时工作开始前插入），节约资源，提前结束耗时操作，越早结束越好，注意做好清理工作
//   特例：sleep()中的线程被调用interrupt()，sleep()会抛出异常，可以被打断，但是需要try，相当于一个订阅中断事件，但是会重置中断状态
// 强行结束：stop()，直接杀，程序不可控，不可靠
fun main() = runBlocking<Unit> {
    val thread = object : Thread() {
        override fun run() {
            println("Thread: I'm running!")

            var count = 0
            while (true) {
                // interrupted()和isInterrupted()的区别是，interrupted()会重置中断状态
                // 一般使用isInterrupted()
                if (isInterrupted) {
                    // 收尾清理工作
                    println("Clearing ...")
                    return
                }
                count++
                if (count % 100_000_000 == 0) {
                    println(count)
                }
                if (count % 1_000_000_000 == 0) {
                    break
                }
            }

            // try {
            //   Thread.sleep(2000)
            // } catch (e: InterruptedException) {
            //   println("isInterrupted: $isInterrupted")
            //   println("Clearing ...")
            //   return
            // }

            println("Thread: I'm done!")

            // // 所有的等待相关的方法都需要进行InterruptedException的捕获
            // val lock = Object()
            // try {
            //   lock.wait()
            // } catch (e: InterruptedException) {
            //   println("isInterrupted: $isInterrupted")
            //   println("Clearing ...")
            //   return
            // }
            //
            // val newThread = thread {
            //
            // }
            // // 所有的等待相关的方法都需要进行InterruptedException的捕获
            // newThread.join()
            //
            // val latch = CountDownLatch(3)
            // // 所有的等待相关的方法都需要进行InterruptedException的捕获
            // latch.await()

        }
    }.apply { start() }
    Thread.sleep(1000)
    thread.interrupt()
}