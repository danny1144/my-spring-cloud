package com.tz.thread;

/**
 * 线程优先级测试
 */
public class ThreadPriority {

    /**
     * 现代操作系统基本采用时分的形式调度运行的线程: 操作系统会分出一个个时间片, 线程会分配到若干时间, 当线程的时间片用完就会发生线程调度,
     * 并等待下次分配, 线程分配到的时间片也决定了线程使用处理器资源的多少.
     * 线程优先级: 就是决定线程需要多或者少分配一些处理器资源的线程属性.
     * 需要注意的是: 线程优先级不能作为程序正确性的依赖, 因为操作系统可以不用理会 Java 线程对于优先级的设定
     */
    public static void main(String[] args) {
        /*
         * 线程优先级: 1~10 默认是 5
         */
        Thread thread1 = new Thread(() -> System.out.println("线程1, 优先级10"));
        thread1.setPriority(10);
        thread1.start();

        Thread thread3 = new Thread(() -> System.out.println("线程2, 默认优先级5"));
        thread3.start();

        Thread thread2 = new Thread(() -> System.out.println("线程3, 优先级1"));
        thread2.setPriority(1);
        thread2.start();
    }


}


