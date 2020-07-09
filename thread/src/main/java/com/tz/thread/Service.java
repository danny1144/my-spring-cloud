package com.tz.thread;

class Service {
    private boolean isFlag = true;

    public void runMethod() {
        String anString = new String();
        while (isFlag == true) {
            //各个线程之间数据没有可见性，使用synchronized保障可见性
            synchronized (anString) {

            }
        }
        System.out.println("任务停止了");
    }

    public void stopMethod() {
        this.isFlag = false;
    }
}