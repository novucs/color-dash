package net.novucs.colordash.state.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.entity.Player;
import net.novucs.colordash.state.RenderTask;
import net.novucs.colordash.state.Snapshot;

import java.util.Collection;
import java.util.Map;

public class GameRenderTask implements RenderTask {

    private final Paint paint = new Paint();
    private final ColorDash game;

    public GameRenderTask(ColorDash game) {
        this.game = game;
    }

    @Override
    public void render(Snapshot s) {
        GameSnapshot snapshot = (GameSnapshot) s;
        SurfaceHolder surfaceHolder = game.getPanel().getHolder();
        Canvas canvas = surfaceHolder.lockCanvas();

        // Wipe with white color.
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        renderAll(canvas, snapshot.getEntities(), snapshot.getScore());

        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void renderAll(Canvas canvas, ImmutableMultimap<EntityType, Entity.Snapshot> entities, int score) {
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
        renderUI(canvas, score);
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
    private void renderUI(Canvas canvas, int score) {
        float left = 0;
        float top = canvas.getHeight() * 0.95f;
        float right = left + canvas.getWidth();
        float bottom = canvas.getHeight();
        paint.setColor(Color.parseColor("#FFF2F2F2"));
        paint.setAlpha(200);
        canvas.drawRect(left, top, right, bottom, paint);

        //Draw in the score
        paint.setColor(Color.WHITE);
        paint.setTextSize(100.0f);
        canvas.drawText("Score: " + score, left, canvas.getHeight() * 0.987f, paint);
    }

    private void renderPlayer(Canvas canvas, Player.Snapshot player) {
        float cx = player.getLocation().getX();
        float cy = player.getLocation().getY();
        float radius = player.getRadius();
        paint.setColor(Color.GREEN);
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
