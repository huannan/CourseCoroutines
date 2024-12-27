package com.rengwuxian.coursecoroutines._1_basics

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.rengwuxian.coursecoroutines.R
import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithContextActivity : ComponentActivity() {
  private lateinit var infoTextView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_1)
    infoTextView = findViewById(R.id.infoTextView)

    CoroutineScope(Dispatchers.Main).launch {
      // 并行的切线程，使用launch
      // 串行的切线程，使用挂起函数withContext，sleep比delay更准确
      val data = withContext(Dispatchers.IO) {
        // 网络代码
        "data"
      }
      // 实际项目中一般不会直接这样写，而是使用自定义挂起函数
      val processedData = withContext(Dispatchers.Default) {
        // 处理数据
        "processed $data"
      }
      println("Processed data: $processedData")
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