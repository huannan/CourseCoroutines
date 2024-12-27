package com.rengwuxian.coursecoroutines._1_basics

import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub

// 如果用到了其它挂起函数，那么就需要写一个挂起函数
suspend fun getRetrofitContributors(): List<Contributor> {
  return gitHub.contributors("square", "retrofit")
}

// 挂起函数只能在协程或其它挂起函数里面使用
// 如果不调用其它挂起函数，则不需要定义挂起函数
suspend fun customSuspendFun() {

}