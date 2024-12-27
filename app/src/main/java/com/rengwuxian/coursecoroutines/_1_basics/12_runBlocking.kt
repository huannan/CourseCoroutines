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

// runBlocking不需要CoroutineScope
// CoroutineScope提供Context，提供协程的取消
// runBlocking会阻塞当前线程，左右是把挂起函数代码转换成阻塞性代码，去传统的线程写法去调用
// 对于服务器程序或者测试代码，套在main里面，整个main里面都可以调用挂起函数
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

    // launch和async普遍情况下启动的协程跟当前线程无关系，Main.immediate除外
    lifecycleScope.launch(Dispatchers.Main.immediate) {

    }
    println()
    lifecycleScope.async { }

    // runBlocking会阻塞当前线程，协程世界回到阻塞世界
    blockingContributors()
    println()
  }

  private fun blockingContributors() = runBlocking {
    gitHub.contributors("square", "retrofit")
  }

}