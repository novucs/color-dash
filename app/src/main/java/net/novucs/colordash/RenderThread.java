package net.novucs.colordash;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.util.BlockingReference;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderThread extends Thread {

    private final ColorDash colorDash;
    private final BlockingReference<GameSnapshot> snapshot = new BlockingReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Paint paint = new Paint();

    public RenderThread(ColorDash colorDash) {
        super("render-thread");
        this.colorDash = colorDash;
    }

    public void initialize() {
        running.set(true);
        start();
    }

    public void terminate() {
        running.set(false);
    }

    public void setSnapshot(GameSnapshot snapshot) {
        this.snapshot.set(snapshot);
    }

    @Override
    public void run() {
        while (running.get()) {
            GameSnapshot snapshot;

            try {
                snapshot = this.snapshot.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to take a snapshot", e);
            }

            render(snapshot);
        }
    }

    private void render(GameSnapshot snapshot) {
        SurfaceHolder surfaceHolder = colorDash.getGamePanel().getHolder();
        Canvas canvas = surfaceHolder.lockCanvas();

        // Wipe with white color.
        paint.reset();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        render(canvas, snapshot.getObstacles());

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void render(Canvas canvas, Set<Obstacle.Snapshot> obstacles) {
        paint.setColor(Color.RED);
        for (Obstacle.Snapshot obstacle : obstacles) {
            render(canvas, obstacle);
        }
    }

    private void render(Canvas canvas, Obstacle.Snapshot obstacle) {
        float left = obstacle.getLocation().getX();
        float top = obstacle.getLocation().getY();
        float right = left + obstacle.getWidth();
        float bottom = top + obstacle.getHeight();
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
