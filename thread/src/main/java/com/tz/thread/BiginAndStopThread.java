package com.tz.thread;

/**
 * @Description:
 * @author: zhongxp
 * @Date: 7/9/2020 4:20 PM
 */
public class BiginAndStopThread {


    public static void main(String[] args) {
        Service service = new Service();
        Thread1 thread1 = new Thread1(service);
        thread1.start();
        Thread2 thread2 = new Thread2(service);
        thread2.start();
        System.out.println("发起停止命令了");
    }

}
