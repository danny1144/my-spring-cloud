package com.tz.thread;

/**
 * @Description: wait /notify. wai和notify在使用前都要获取对象的锁，如果notify在没有持有锁的情况下调用会抛出illegalMonitorStateException
 * 该方法用来通知那些可能等待该对象锁的其他线程。如果有多个线程在等待。则由线程规划器随机挑选其中一个呈现wait的状态线程通知其notify。并使它等待获取对象锁。
 * @author: zhongxp
 * @Date: 7/9/2020 5:05 PM
 */
public class TestWait extends Thread {

    private Object lock;

    public TestWait(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {

        synchronized (lock) {
            System.out.println("syn 第一行");
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("wait 之后的一行");
        }
        System.out.println("synchronized 之后的一行");
    }

    public static void main(String[] args) throws InterruptedException {
        String lock = new String("origin");
        TestWait testWait = new TestWait(lock);
        testWait.start();
        Thread.sleep(3000);
        TestNotify testNotify = new TestNotify(lock);
        testNotify.start();


    }
}
