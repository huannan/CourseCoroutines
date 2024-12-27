package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SuspendActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)


//    callbackStyle()
    coroutinesStyle()
  }

  // 回调形式的切线程
  private fun callbackStyle() {
    gitHub.contributorsCall("square", "retrofit")
      .enqueue(object : Callback<List<Contributor>> {
        override fun onResponse(
          call: Call<List<Contributor>>, response: Response<List<Contributor>>,
        ) {
          showContributors(response.body()!!)
          gitHub.contributorsCall("square", "okhttp")
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

        override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
          t.printStackTrace()
        }
      })
  }

  // 协程形式的切线程
  // 协程被挂起suspend：挂起函数把协程暂停了，协程和线程分离，不再占用协程所在的线程，线程被让出了，而是切到挂起函数指定的线程
  // 协程恢复resume：回到协程自己线程继续执行
  // 挂起函数需要在协程作用域中调用才有意义
  private fun coroutinesStyle() = CoroutineScope(Dispatchers.Main).launch {
    val contributors = gitHub.contributors("square", "retrofit")
    showContributors(contributors)
    val contributors2 = gitHub.contributors("square", "okhttp")
    showContributors(contributors2)
  }

  private fun showContributors(contributors: List<Contributor>) = contributors
    .map { "${it.login} (${it.contributions})" }
    .reduce { acc, s -> "$acc\n$s" }
    .let { infoTextView.text = it }
}