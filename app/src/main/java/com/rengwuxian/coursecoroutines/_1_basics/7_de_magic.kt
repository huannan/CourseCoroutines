package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class DeMagicActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)

    lifecycleScope.launch {
      // 协程代码编译时，会把挂起函数的前后插入回调，以实现协程代码的挂起，内部是通过状态机切换实现
      // 下面是简化版的协程工作流程示例：
      // switchToBackground {
      //   gitHub.contributors("square", "retrofit")
      //   switchToMain {
      //     showContributors(contributors)
      //   }
      // }
    }
  }

  // 不能往线程执行流程中插入代码块，只能基于回调做，例如：
  // 1. Android提供了无线循环的方式，可以往主线程post任务
  // 2. 使用线程池，可以往子线程post任务
  private fun coroutinesStyle() = lifecycleScope.launch {
    // 为什么不卡主线程？其实内部是回调，主线程并没有在等待网络请求
    val contributors = gitHub.contributors("square", "retrofit")
    showContributors(contributors)
  }

  private fun showContributors(contributors: List<Contributor>) = contributors
    .map { "${it.login} (${it.contributions})" }
    .reduce { acc, s -> "$acc\n$s" }
    .let { infoTextView.text = it }
}