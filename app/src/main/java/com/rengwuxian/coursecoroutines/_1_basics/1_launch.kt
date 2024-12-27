package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rengwuxian.coursecoroutines.ui.theme.CourseCoroutinesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

class LaunchCoroutineActivity : ComponentActivity() {

  @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CourseCoroutinesTheme {}
    }

    // 切到后台/子线程，不常用
    thread {

    }

    // 使用线程池，可以复用线程，性能更好
    println("Main thread: ${Thread.currentThread().name}")
    val executor = Executors.newCachedThreadPool()
    executor.execute {
      println("Executor thread: ${Thread.currentThread().name}")
    }

    // 客户端独有的形式，切到主线程
    val handler = Handler(Looper.getMainLooper())
    handler.post {  }

    // 客户端独有的形式，切到主线程
    val view = View(this)
    view.post {  }

    // 协程写法，切线程，通过CoroutineScope启动协程
    // 协程也是把代码装进来，丢给线程池处理
    // EmptyCoroutineContext:默认在后台线程
    // CoroutineScope包含Executor，CoroutineContext包含协程用到的所有上下文
    // ContinuationInterceptor:协程调度器，管理线程，管理任务执行的工具，拦截代码执行，先切线程，再往下执行

    // Dispatchers.Main 主线程，服务器程序无法使用
    // Dispatchers.Default 处理CPU计算密集型，一般线程池大小与CPU的可用核心数相同，线程数超过CPU的可用核心数的时候效率反而会降低
    // Dispatchers.IO CPU空闲但是IO密集型，64线程的线程池，充分利用CPU (input / output，与内存之外进行数据交互，一般是磁盘/网络，磁盘或者网卡在工作，CPU处于闲置状态)
    // Dispatchers.Unconfined 不限制，不切线程，在挂起函数执行完之后继续在挂起函数所在的线程继续执行，不推荐使用

    // 自定义线程池
    val context = newFixedThreadPoolContext(20, "MyThread")
    val context1 = newSingleThreadContext("MyThread")

    val scope = CoroutineScope(context)
    // 不使用的时候最好及时关闭
    context1.close()
    // 不复用CoroutineScope指定的ContinuationInterceptor
    scope.launch(Dispatchers.IO) {
      println("Coroutine thread: ${Thread.currentThread().name}")
    }
    scope.launch {
      println("Coroutine thread: ${Thread.currentThread().name}")
    }
  }
}