package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

// runBlocking不需要CoroutineScope，不需要Context和取消；runBlocking会阻塞当前线程，定位作用是把挂起函数代码转换成阻塞性代码，方便用传统的线程写法去调用挂起函数
// CoroutineScope提供Context和提供协程的取消

// 对于服务器程序或者测试代码，套在main里面，提供协程环境整个main里面都可以调用挂起函数
fun main() = runBlocking<Unit> {
  val contributors = gitHub.contributors("square", "retrofit")
  launch {

  }
}

class RunBlockingActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)

    // launch和async普遍情况下启动的协程跟当前线程无关系，大部分情况下不会阻塞当前线程，Main.immediate除外
    lifecycleScope.launch { }
    lifecycleScope.async { }

    // Dispatchers.Main.immediate和Dispatchers.Main的区别是前者会判断当前线程是不是主线程，是的话直接run，不是才post；后者是直接post
    lifecycleScope.launch(Dispatchers.Main.immediate) {

    }

    // runBlocking会阻塞当前线程，协程世界回到阻塞世界
    blockingContributors()
    println("runBlocking end")
  }

  private fun blockingContributors() = runBlocking {
    gitHub.contributors("square", "retrofit")
  }

}