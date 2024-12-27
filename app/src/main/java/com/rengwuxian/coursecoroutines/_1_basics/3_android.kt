package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)

    // KTX
    // 协程启动在主线程
    // 1. 绑定所在组件的生命周期，当组件生命周期结束时，协程也会被取消
    // 2. 有内置的ContinuationInterceptor，直接Launch就是主线程，内部是Dispatchers.Main.immediate
    // Dispatchers.Main和Dispatchers.Main.immediate的区别：
    // 1. Dispatchers.Main会使用handler.post
    // 2. Dispatchers.Main.immediate经过性能优化，会先判断是否为主线程，不是才会使用handler.post
    lifecycleScope.launch {

    }
    Dispatchers.Default
    // Handler.post()
  }

  class MyViewModel : ViewModel() {
    fun someFun() {
      // 同lifecycleScope类似，有内置的ContinuationInterceptor，直接Launch就是主线程，内部是Dispatchers.Main.immediate
      viewModelScope.launch {

      }
    }
  }

  private fun coroutinesStyle() = lifecycleScope.launch {
    val contributors = gitHub.contributors("square", "retrofit")
    showContributors(contributors)
  }

  private fun showContributors(contributors: List<Contributor>) = contributors
    .map { "${it.login} (${it.contributions})" }
    .reduce { acc, s -> "$acc\n$s" }
    .let { infoTextView.text = it }
}