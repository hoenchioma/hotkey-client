package com.rfw.hotkey.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicReference;

public class LiveScreenView extends SurfaceView implements SurfaceHolder.Callback {
    public static final long DEFAULT_FPS = 60;

    private UpdateLoop updateLoop = new UpdateLoop();
    private AtomicReference<Bitmap> bitmap = new AtomicReference<>(null);
    private long targetFPS = DEFAULT_FPS;

    public long getTargetFPS() {
        return targetFPS;
    }

    public void setTargetFPS(long targetFPS) {
        this.targetFPS = targetFPS;
    }

    public LiveScreenView(Context context) {
        super(context);
        init();
    }

    public LiveScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void updateBitMap(Bitmap newBitmap) {
        this.bitmap.set(newBitmap);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        updateLoop.running = true;
        updateLoop.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                updateLoop.running = false;
                updateLoop.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null && bitmap.get() != null) {
            canvas.drawBitmap(bitmap.get(), 0, 0, null);
        }
    }

    private class UpdateLoop extends Thread {
        volatile boolean running = false;

        @Override
        public void run() {
            long startTime;
            long timeMillis;
            long waitTime;
            long totalTime = 0;
            int frameCount = 0;
            long targetTime = 1000 / targetFPS;

            while (running) {
                startTime = System.nanoTime();
                Canvas canvas = null;

                try {
                    canvas = getHolder().lockCanvas();
                    synchronized (getHolder()) {
                        draw(canvas);
                    }
                } catch (Exception e) {
                } finally {
                    if (canvas != null) {
                        try {
                            getHolder().unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                timeMillis = (System.nanoTime() - startTime) / 1000000;
                waitTime = targetTime - timeMillis;

                try {
                    sleep(waitTime);
                } catch (Exception e) {}

                totalTime += System.nanoTime() - startTime;
                frameCount++;
//                if (frameCount == targetFPS)        {
//                    averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
//                    frameCount = 0;
//                    totalTime = 0;
//                    System.out.println(averageFPS);
//                }
            }

        }
    }
}
