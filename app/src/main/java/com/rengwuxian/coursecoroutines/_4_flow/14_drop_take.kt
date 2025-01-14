package com.rengwuxian.coursecoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val flow1 = flowOf(1, 2, 3, 4, 5)
  scope.launch {
    flow1.drop(2).collect { println("1: $it") }
    flow1.dropWhile { it != 3 }.collect { println("2: $it") }
    flow1.take(2).collect { println("3: $it") }
    flow1.takeWhile { it != 3 }.collect { println("4: $it") }
  }
  delay(10000)
}

/*

drop()、take() 系列操作符
drop() 和 take() 系列操作符也是过滤功能，但它们特殊性在于是属于截断型过滤，持续丢弃数据直到某条数据之后才开始放行向下转发，或者持续向下转发到达某条数值之后把数据流掐断直接结束数据流，后面的数据全都不要了。

drop()、dropWhile()
drop() 会把你提供的前 n 条数据过滤掉，再往后的数据就开始正常向下转发。
dropWhile() 会让你提供一个判断条件，然后对每个数据都检查，凡事符合这个条件的就把数据丢弃；遇到不符合条件的就把这条数据留下，并且从这条数据开始再往下就不再检查都往下转发。

take()、takeWhile()
take() 操作符和 drop() 相反，会让你提供一个数值，然后把数据流的前 n 条数据往下转发，一旦达到提供的数值就掐断直接结束 Flow，后面的数据全都不要。
takeWhile() 和 dropWhile() 相反，会让你提供一个判断条件，在数据符合条件的时候就保持发送，一旦遇到一条不符合的就掐断直接结束 Flow，后面的数据全都不要。
 */