package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StructuredConcurrencyActivity : ComponentActivity() {
    private lateinit var infoTextView: TextView
    private var disposable: Disposable? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_1)
        infoTextView = findViewById(R.id.infoTextView)

        // Structured concurrency 结构化并发，面向并发任务的管理，主要说的是协程生命周期（正常取消、异常）等父子协程的管理关系，CoroutineScope的cancel取消所有协程，一对多、一层对多层的取消
        // 关闭页面时立即取消任务，可以有效防止内存泄漏
        // 内存泄露（泄漏） memory leak
        // GC（垃圾回收器） garbage collector 扫描有没有被以下类型的对象引用，具有传递性，没有则就回收:static、活跃线程、JNI
        // Android 内存泄露 弱引用
        // RxJava

        // 有返回值
        // disposable = rxStyle()
        job = coroutinesStyle()
        coroutinesStyle()
    }

    override fun onDestroy() {
        super.onDestroy()
        // disposable?.dispose()
        // 协程的取消
        // 区别：job的cancel仅取消对应协程；CoroutineScope的cancel取消所有协程
        // job?.cancel()
        // 而实际中lifecycleScope.cancel()不用手动写
        lifecycleScope.cancel()
    }

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

    private fun coroutinesStyle() = lifecycleScope.launch {
        // 通过隐式Receiver this在协程内部再去启动新的协程
        // 内部的CoroutineScope收到外部的CoroutineScope管理
        launch {

        }
        val contributors = gitHub.contributors("square", "retrofit")
        val filtered = contributors.filter { it.contributions > 10 }
        showContributors(filtered)
    }

    private fun rxStyle() = gitHub.contributorsRx("square", "retrofit")
        .map { list -> list.filter { it.contributions > 10 } }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(::showContributors)

    private fun showContributors(contributors: List<Contributor>) = contributors
        .map { "${it.login} (${it.contributions})" }
        .reduce { acc, s -> "$acc\n$s" }
        .let { infoTextView.text = it }
}