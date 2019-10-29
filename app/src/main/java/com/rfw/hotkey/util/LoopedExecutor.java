package com.rfw.hotkey.util;

public abstract class LoopedExecutor extends Thread {
    private volatile boolean stop = true;
    private volatile long delay = 100; // milliseconds

    public LoopedExecutor() {}

    public LoopedExecutor(long delay) {
        this.delay = delay;
    }

    @Override
    public synchronized void start() {
        super.start();
        stop = false;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public abstract void task();

    public void end() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            task();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stop = true;
    }
}