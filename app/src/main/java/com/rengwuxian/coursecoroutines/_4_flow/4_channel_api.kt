package com.rengwuxian.coursecoroutines._4_flow

import com.rengwuxian.coursecoroutines.common.Contributor
import com.rengwuxian.coursecoroutines.common.gitHub
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileWriter
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking<Unit> {
  val scope = CoroutineScope(EmptyCoroutineContext)
  val fileChannel = Channel<FileWriter>() { it.close() }
  fileChannel.send(FileWriter("test.txt"))
//  val channel = Channel<List<Contributor>>(8, BufferOverflow.DROP_OLDEST)
//  val channel = Channel<List<Contributor>>(1, BufferOverflow.DROP_OLDEST)
  val channel = Channel<List<Contributor>>(CONFLATED)
  scope.launch {
    channel.send(gitHub.contributors("square", "retrofit"))
    channel.close()
    channel.close(IllegalStateException("Data error!"))
    channel.receive()
    channel.receive()
    channel.send(gitHub.contributors("square", "retrofit"))
    channel.trySend(gitHub.contributors("square", "retrofit"))
    channel.tryReceive()
  }
  launch {
    // val channelResult = channel.receiveCatching()

    for (data in channel) {
      println("Contributors: $data")
    }
    /*while (isActive) {
      val contributors = channel.receive()
      println("Contributors: $contributors")
    }*/
  }
  delay(1000)
  channel.cancel()
  delay(10000)
}

/*

可以直接对 Channel 对象进行循环遍历，能过通过 for 循环遍历实际上也是使用了 kotlin 重载操作符的能力，Channel 重载了 iterator()
一般 for循环我们是遍历一个 List 或者一个 Map，而 Channel 的遍历比较特殊，Channel 的遍历过程是挂起式的，在没有元素的时候也会把协程挂起，等待下一个元素的出现，或者 Channel 被关闭了循环才结束
    for (data in channel) {
      println("Contributors: $data")
    }


设置 Channel 缓冲区大小：capacity
* RENDEZVOUS：取的 0
* UNLIMITED：取的 Int.MAX_VALUE，不限制
* BUFFERED：默认值是 64
* CONFLATED：队列长度为1，并且总是丢弃旧元素的 Channel，等同于 capacity = 1 并且 onBufferOverflow = DROP_OLDEST（但实际上它要求 onBufferOverflow = SUSPEND，因为默认值就是所以可以不填）
按上面的例子我们把缓冲区调整为 8，那么第一次 send() 时数据就会直接被放进队列的头部，放完后 send() 也不挂起协程直接就返回了，直到队列长度放不下时才挂起协程。
大多数时候我们根据应用的情况填一个手动的值是最合适的。


设置缓冲溢出策略：onBufferOverflow
当队列满的时候默认是挂起协程，实际上还可以调整策略，比如丢弃数据
协程提供的策略：
* SUSPEND：默认值，队列满了就挂起协程
* DROP_OLDEST：有新数据塞到队列，就把队列头部的元素丢弃
* DROP_LATEST：丢弃新元素


Channel 的关闭：close() 和 cancel()
Channel 的关闭有两个函数：close() 和 cancel()。两个都是关闭 Channel，close() 是从发送端关闭，cancel() 是从接收端关闭。
Channel 的 close() 和 cancel() 分别归属于不同的接口，close() 是 SendChannel 接口，cancel() 是 ReceiveChannel 接口。

close()
Channel 调用了 close() 后，isClosedForSend 会被修改为 true，后续再调用 send() 就会抛出 ClosedSendChannelException，在使用这个 Channel 所在的协程都不能再调用 send()。如果数据都已经接收完了，isClosedForReceive 会被修改为 true，后续再调用 receive() 就会抛出 ClosedReceiveChannelException。
Channel 调用了 close() 不会发生异常的情况：
* 允许 Channel 调用 receive()：Channel 有可能还有数据没接收完缓冲区的数据
* 调用 close() 之前在挂起等待的 send()：它们的数据没进缓冲区在队列外排着队，也允许让 receive() 接收
close() 也允许自定义异常，当后续再发送 send() 和 receive() 就不会抛 ClosedSendChannelException 和 ClosedReceiveChannelException，而是会抛自定义的异常：
channel.close(IllegalStateException("Data error!"))

cancel()
当我们的界面被销毁了不再需要新数据了，就可以调用 cancel() 不再接收数据，它会将发送和接收两端都关闭，并且会同时修改 isClosedForSend 和 isClosedForReceive 为 true。
调用了 cancel() 后续再调用 send() 和 receive() 会抛出 CancellationException。可以通过这个异常区分是 close() 的异常还是 cancel() 的异常。
或许会有疑惑为什么要把发送也关闭？我们发送数据就是为了接收，我们都不再接收数据了，发送也就没有意义，自然的就可以将发送端也关闭。


Channel 的关闭及时释放资源：onUndeliveredElement
当调用 Channel 的 cancel() 后会同时关闭发送端和接收端，但这种关闭可能会带来问题：一些已经调用 send() 但没被接收的数据因为被丢弃没释放，可能导致资源泄漏。
比如写文件的操作，希望在接收到使用后就把文件流关闭，但文件没接收就调用了 cancel()，文件流没关闭导致了资源泄漏。
通过设置 Channel 的 onUndeliveredElement，它是针对那些已发送但最终被丢弃没被接收的数据怎么处理的函数。
  // 通过 onUndeliveredElement 如果发送了没被接收，就把文件流关闭
  val fileChannel = Channel<FileWriter>() { it.close() }


Channel 的 trySend() 和 tryReceive()
trySend() 和 tryReceive() 两个函数和命名的意思一样：能发送就发或者能接收就收，不行就算了。
trySend() 和 tryReceive() 和 send() 及 receive() 的区别：
* send() 和 receive() 都是挂起函数，trySend() 和 tryReceive() 是非挂起函数
* 不仅不挂起，也不阻塞线程，调用后瞬时返回：如果因为缓冲满了而发送不了，或者因为缓冲是空的而取不到数据，也并不会等待，而是直接返回，只不过返回的是失败的结果


receiveCatching()
receiveCatching() 也是一个挂起函数，相比 receive() 它在遇到异常的时候不会抛异常，而是会把异常包进 ChannelResult 里面，将异常返回到你手里让你自己处理，相当于是 receive() 和 tryReceive() 的结合。
  val channelResult = channel.receiveCatching()

 */