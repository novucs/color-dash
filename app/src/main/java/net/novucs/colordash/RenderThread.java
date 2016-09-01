package net.novucs.colordash;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.entity.Player;
import net.novucs.colordash.util.BlockingReference;

import java.util.Collection;
import java.util.Map;

public class RenderThread extends Thread implements GameService {

    private final ColorDash game;
    private final BlockingReference<GameSnapshot> snapshot = new BlockingReference<>();
    private final Paint paint = new Paint();

    public RenderThread(ColorDash game) {
        super("render-thread");
        this.game = game;
    }

    @Override
    public void initialize() {
        start();
    }

    @Override
    public void terminate() {
        interrupt();
    }

    public void setSnapshot(GameSnapshot snapshot) {
        this.snapshot.set(snapshot);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            GameSnapshot snapshot;

            try {
                snapshot = this.snapshot.take();
            } catch (InterruptedException e) {
                break;
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
                case PLAYER:
                    renderPlayers(canvas, entry.getValue());
                    break;
            }
        }
        renderUI(canvas);
    }

    private void renderObstacles(Canvas canvas, Collection<Entity.Snapshot> obstacles) {
        for (Entity.Snapshot entity : obstacles) {
            renderObstacle(canvas, (Obstacle.Snapshot) entity);
        }
    }

    private void renderObstacle(Canvas canvas, Obstacle.Snapshot obstacle) {
        float left = obstacle.getLocation().getX();
        float top = obstacle.getLocation().getY();
        float right = left + obstacle.getWidth();
        float bottom = top + obstacle.getHeight();

        paint.setColor(obstacle.getColor());

        if (obstacle.isLeft() && right != 0) {
            float radius = obstacle.getHeight() / 2;
            right -= radius;
            canvas.drawCircle(right, (bottom + top) / 2, radius, paint);
        } else if (!obstacle.isLeft() && left != game.getPanel().getWidth()) {
            float radius = obstacle.getHeight() / 2;
            left += radius;
            canvas.drawCircle(left, (bottom + top) / 2, radius, paint);
        }

        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void renderPlayers(Canvas canvas, Collection<Entity.Snapshot> players) {
        for (Entity.Snapshot entity : players) {
            renderPlayer(canvas, (Player.Snapshot) entity);
        }
    }

    //Function that will render the bottom bar, score, and pause / unpause buttons.
    private void renderUI(Canvas canvas) {
        if (game.getApplicationState() == ApplicationState.PAUSED || game.getApplicationState() == ApplicationState.PLAYING) {

            float left = 0;
            float top = canvas.getHeight() * 0.95f;
            float right = left + canvas.getWidth();
            float bottom = canvas.getHeight();

            paint.setColor(Color.parseColor("#FFF2F2F2"));
            paint.setAlpha(200);
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    private void renderPlayer(Canvas canvas, Player.Snapshot player) {
        float cx = player.getLocation().getX();
        float cy = player.getLocation().getY();
        float radius = player.getRadius();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
