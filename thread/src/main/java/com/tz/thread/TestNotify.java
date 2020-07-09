package com.tz.thread;

/**
 * @Description: 对象锁通知
 * @author: zhongxp
 * @Date: 7/9/2020 5:16 PM
 */
public class TestNotify extends Thread {
    private Object lock;
    public  TestNotify(Object lock){
        this.lock=lock;
    }
    @Override
    public void run() {
        synchronized (lock) {
            lock.notify();
        }
    }
}
