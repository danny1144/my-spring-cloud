package com.tz.thread;

class Thread1 extends Thread {
    Service service;

    public Thread1(Service service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.runMethod();
    }
}