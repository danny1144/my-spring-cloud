package com.tz.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 无锁同步
 * @author: zhongxp
 * @Date: 7/9/2020 3:56 PM
 */
public class AddCountThread implements Runnable {

    private volatile AtomicInteger count = new AtomicInteger(0);


    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(count.incrementAndGet());
        }
    }
}

class main {

    public static void main(String[] args) {
        AddCountThread addCountThread1 = new AddCountThread();

        new Thread(addCountThread1).start();
        new Thread(addCountThread1).start();
        new Thread(addCountThread1).start();
        new Thread(addCountThread1).start();
        new Thread(addCountThread1).start();
    }
}