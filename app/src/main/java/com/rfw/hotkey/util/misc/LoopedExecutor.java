package com.rfw.hotkey.util.misc;

/**
 * Repeatedly executes task at a fixed delay
 */
public abstract class LoopedExecutor extends Thread {
    public static final long DEFAULT_DELAY = 100; // milliseconds

    private volatile boolean stop = true;
    private volatile long delay = DEFAULT_DELAY; // milliseconds

    public LoopedExecutor() {}

    public LoopedExecutor(long delay) {
        this.delay = delay;
    }

    public static LoopedExecutor getInstance(Runnable taskToRun, long delay) {
        return new LoopedExecutor(delay) {
            @Override
            public void task() {
                taskToRun.run();
            }
        };
    }

    @Override
    public synchronized void start() {
        super.start();
        stop = false;
    }

    public void end() {
        stop = true;
    }

    public boolean isRunning() {
        return !stop;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * The task to executed
     * (Abstract method meant to be overloaded)
     */
    public abstract void task();

    @Override
    public void run() {
        while (!stop) {
            task();
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        stop = true;
    }
}