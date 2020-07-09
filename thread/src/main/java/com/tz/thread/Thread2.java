package com.tz.thread;


class Thread2 extends Thread {
    Service service;

    public Thread2(Service service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.stopMethod();
    }
}
