package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CallbackToSuspendActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)

    val job = lifecycleScope.launch {
      // 嵌套的协程异常无法通过try捕获
      try {
        launch {  }

        // 可以直接捕获挂起函数的异常
        try {
          val contributors = callbackToCancellableSuspend()
          showContributors(contributors)
        } catch (e: Exception) {

        }

      } catch (e: Exception) {
        infoTextView.text = e.message
      }
    }

    // 取消需要挂起函数的配合，例如sleep是不会配合取消的
    // val job = lifecycleScope.launch {
    //   println("Coroutine cancel: 1")
    //   Thread.sleep(500)
    //   println("Coroutine cancel: 2")
    // }

    lifecycleScope.launch {
      delay(200)
      job.cancel()
    }
  }

  // 回调函数转换为挂起函数，不支持取消，调用后就算协程取消了也会继续执行完成
  private suspend fun callbackToSuspend() = suspendCoroutine {
    gitHub.contributorsCall("square", "retrofit")
      .enqueue(object : Callback<List<Contributor>> {
        override fun onResponse(
          call: Call<List<Contributor>>, response: Response<List<Contributor>>,
        ) {
          // 结束挂起函数
          it.resume(response.body()!!)
        }

        override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
          // 异常处理
          it.resumeWithException(t)
        }
      })
  }

  // 回调函数转换为挂起函数，支持取消，一般都是希望取消，建议用suspendCancellableCoroutine
  private suspend fun callbackToCancellableSuspend() = suspendCancellableCoroutine {
    // 可以注册一个取消的回调，进行收尾工作
    it.invokeOnCancellation {
    }
    gitHub.contributorsCall("square", "retrofit")
      .enqueue(object : Callback<List<Contributor>> {
        override fun onResponse(
          call: Call<List<Contributor>>, response: Response<List<Contributor>>,
        ) {
          it.resume(response.body()!!)
        }

        override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
          it.resumeWithException(t)
        }
      })
  }

  private fun callbackStyle() {
    gitHub.contributorsCall("square", "retrofit")
      .enqueue(object : Callback<List<Contributor>> {
        override fun onResponse(
          call: Call<List<Contributor>>, response: Response<List<Contributor>>,
        ) {
          showContributors(response.body()!!)
        }

        override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
          t.printStackTrace()
        }
      })
  }

  private fun showContributors(contributors: List<Contributor>) = contributors
    .map { "${it.login} (${it.contributions})" }
    .reduce { acc, s -> "$acc\n$s" }
    .let { infoTextView.text = it }
}