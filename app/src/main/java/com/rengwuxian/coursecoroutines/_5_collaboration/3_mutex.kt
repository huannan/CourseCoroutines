package com.rengwuxian.coursecoroutines._5_collaboration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

//@Synchronized
fun main() = runBlocking {
    var number = 0
    val lock = Any()
    val thread1 = thread {
        repeat(100_0000) {
            synchronized(lock) {
                // number++ 和 number-- 并不是原子操作
                number++
            }
        }
    }
    val thread2 = thread {
        repeat(100_0000) {
            synchronized(lock) {
                number--
            }
        }
    }
    thread1.join()
    thread2.join()
    println("number: $number")

    val scope = CoroutineScope(EmptyCoroutineContext)
    val mutex = Mutex() // mutual exclusion
    // Semaphore，与Java的一样
    // 被多个线程同时持有的锁，可以设置上限，当获取到锁的线程数超过上限时，其他线程会阻塞，直到有锁释放为止。一般用来实现性能控制，比如 数据库连接池，当数据库连接池的连接数超过上限时，其他线程会阻塞，直到有连接释放为止。
    // val semaphore = Semaphore(3)
    // AtomicInteger()
    // CopyOnWriteArrayList<String>()
    val job1 = scope.launch {
        // semaphore.acquire()
        // semaphore.release()
        repeat(100_0000) {
            try {
                mutex.lock()
                number++
            } finally {
                mutex.unlock()
            }
        }
    }
    val job2 = scope.launch {
        repeat(100_0000) {
            // 简便写法
            mutex.withLock {
                number--
            }
        }
    }
    job1.join()
    job2.join()
    println("number: $number")

    delay(10000)
}

@Volatile
var v = 0

@Transient
var t = 0

/*

竞态条件：指的是在多个线程访问共享资源时由于缺乏并发控制，导致资源的访问顺序不受控，从而可能会出现错误的结果的条件。多线程同时操作同一个对象，不是原子操作时交叉访问就会导致结果可能不符合预期。我们在写代码的时候我们是一定要避免竞态条件的出现。

原子操作：指的比如运算操作不会有中间过程，要么开始要么结束，是最小的操作单元。
    例如number++ 和 number-- 实际上是可以拆分成两个操作：
    1. 把 number 的值和 1 相加得到结果
    2. 把计算结果赋值给 number

排查解决竞态条件主要有两个步骤：
1. 找到每一个被多个线程共同访问相同变量的代码块，这类代码块也被称为临界区
2. 保证同一时间只有一个线程访问变量，即在临界区添加互斥锁。在 Java 添加互斥锁主要有两种方式：synchronized 和 Lock。

在 kotlin 使用 synchronized 会有点不同，需要注意的是操作单元依然是线程而不是协程：
* kotlin 是没有 synchronized 关键字，对一个函数上锁需要用 @Synchronized 注解
* synchronized 代码块在 kotlin 是使用 synchronized() 函数

synchronized() 函数也能在协程解决竞态条件的。synchronized() 函数虽然操作单元是线程，但 kotlin 的协程本质上还是基于 JVM 是运行在线程上的，synchronized() 函数掐住的也是正在运行协程代码的线程，协程也会暂停，那自然也能处理协程的竞态条件了。Lock 也同理能用在协程序。

Mutex
不过我们在协程一般不用它们，在协程引入了 Mutex，用法上和 Java 的 Lock 差不多一样，Mutex 可以在协程给代码加锁，调用 lock() 加锁，调用 unlock() 解锁：
    withLock()
    // 等同于
    try {
      mutex.lock()
    } finally {
         mutex.unlock()
    }

Mutex 和 synchronized()/Lock 的区别：
* Mutex 是挂起式的，即会把当前线程让出让线程能做别的事情不造成资源浪费
* synchronized()/Lock 是阻塞式的在上锁的时候会把线程阻塞

Mutex 比 synchronized()/Lock 性能更好，这是 Mutex 的优势。Mutex 与 synchronized()/Lock 使用场景的选择：
* 在协程我们应该优先使用 Mutex
* 如果涉及到线程之间或者线程与协程之间的并发，还是得用 synchronized()/Lock

--------------------------------

线程API和Kotlin/协程API对照：
互斥锁
Java：synchronized 和 Lock
Kotlin/协程：协程中使用Mutex，也可以使用@Synchronized注解的函数，或者synchronized{}方法

信号量
Java：Semaphore
Kotlin/协程：Semaphore协程版

volatile和transient
Java：volatile和transient
Kotlin/协程：@Volatile和@Transient

wait()和notify()
Java：wait()和notify()，既能实现互斥锁的功能又能实现线程直接互相协作等待的功能
Kotlin/协程：无

共享变量相关API
Java：AtomicInteger，CopyOnWriteArrayList，CopyOnWriteArraySet，ConcurrentHashMap，ConcurrentLinkedQueue，ConcurrentSkipListMap，ConcurrentSkipListSet，LinkedBlockingQueue，LinkedBlockingDeque，PriorityBlockingQueue，SynchronousQueue，LinkedTransferQueue，LinkedHashMap，LinkedHashSet，LinkedTreeMap，LinkedTreeSet，TreeMap
Kotlin/协程：与Java一样

 */