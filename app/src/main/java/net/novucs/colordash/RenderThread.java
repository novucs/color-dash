package net.novucs.colordash;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.util.BlockingReference;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderThread extends Thread implements GameService {

    private final ColorDash game;
    private final BlockingReference<GameSnapshot> snapshot = new BlockingReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Paint paint = new Paint();

    public RenderThread(ColorDash game) {
        super("render-thread");
        this.game = game;
    }

    @Override
    public void initialize() {
        running.set(true);
        start();
    }

    @Override
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
        SurfaceHolder surfaceHolder = game.getPanel().getHolder();
        Canvas canvas = surfaceHolder.lockCanvas();

        // Wipe with white color.
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        renderAll(canvas, snapshot.getEntities());

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void renderAll(Canvas canvas, ImmutableMultimap<EntityType, Entity.Snapshot> entities) {
        for (Map.Entry<EntityType, Collection<Entity.Snapshot>> entry : entities.asMap().entrySet()) {
            switch (entry.getKey()) {
                case OBSTACLE:
                    renderObstacles(canvas, entry.getValue());
                    break;
            }
        }
    }

    private void renderObstacles(Canvas canvas, Collection<Entity.Snapshot> obstacles) {
        for (Entity.Snapshot entity : obstacles) {
            render(canvas, (Obstacle.Snapshot) entity);
        }
    }

    private void render(Canvas canvas, Obstacle.Snapshot obstacle) {
        float left = obstacle.getLocation().getX();
        float top = obstacle.getLocation().getY();
        float right = left + obstacle.getWidth();
        float bottom = top + obstacle.getHeight();

        paint.setColor(obstacle.getColor());

        if (left != 0 || right != 0) {
            if (obstacle.isLeft()) {
                canvas.drawCircle(right, (bottom + top) / 2, obstacle.getHeight() / 2, paint);
                right -= game.getPanel().getWidth() * 0.003f;
            } else {
                canvas.drawCircle(left, (bottom + top) / 2, obstacle.getHeight() / 2, paint);
                left -= game.getPanel().getWidth() * 0.003f;
            }
        }

        paint.setColor(obstacle.getColor());
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
