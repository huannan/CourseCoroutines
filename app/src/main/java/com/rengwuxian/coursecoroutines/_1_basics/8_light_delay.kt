package com.rengwuxian.coursecoroutines._1_basics

// 官方文档说协程比线程更轻量级，存在夸大宣传的嫌疑，协程示例是单线程，而另外一个Demo是开启了5万个线程
// delay()和sleep()的区别：delay()是挂起函数，不会阻塞线程，会让出当前线程，是性能优势；sleep()是阻塞函数，会阻塞线程，但是sleep是真正占用所以更准确

// 协程用直观的代码结构实现了线程的复杂结构，而且把业务代码和线程绑定，就是一种性能优势，当成轻量级线程，好像也没什么问题