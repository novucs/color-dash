package net.novucs.colordash;

import android.graphics.Color;

import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.math.Vector2f;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MechanicsThread extends Thread {

    private static final int TPS = 30;
    private static final int NORMAL_TICK_DURATION = (int) TimeUnit.SECONDS.toMillis(1) / TPS;

    private final ColorDash colorDash;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Set<Obstacle> obstacles = new HashSet<>();
    private float gameSpeed = 1.0f;
    private int width;
    private int height;

    private int[] colors = {Color.parseColor("#FF0000"), Color.parseColor("#FF00EF"), Color.parseColor("#3FE0FF"), Color.parseColor("#3FFF72"), Color.parseColor("#FFDE00")};
    private int nextColor;
    private int colorTracker;

    public MechanicsThread(ColorDash colorDash) {
        super("mechanics-thread");
        this.colorDash = colorDash;
    }

    public float getGameSpeed() {
        return gameSpeed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void initialize() {
        width = colorDash.getGamePanel().getWidth();
        height = colorDash.getGamePanel().getHeight();
        running.set(true);
        start();
        nextColor = 0;
        colorTracker = 0;
    }

    public void terminate() {
        running.set(false);
    }

    @Override
    public void run() {
        long tickStart;
        long tickDuration;

        obstacles.add(createObstacle());

        while (running.get()) {
            tickStart = System.currentTimeMillis();

            gameSpeed *= 1.00001f;

            tick();

            // Pass current tick snapshot to render thread.
            colorDash.getRenderThread().setSnapshot(snapshot());

            tickDuration = System.currentTimeMillis() - tickStart;

            if (NORMAL_TICK_DURATION > tickDuration) {
                try {
                    sleep(NORMAL_TICK_DURATION - tickDuration);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void tick() {
        for (Obstacle obstacle : obstacles) {
            obstacle.tick();
        }
    }

    private Obstacle createObstacle() {
        float x = 0;
        float y = this.height * 0.10f;
        float width = this.width * 0.25f;
        float height = this.height * 0.01f;
        colorTracker++;
        if(colorTracker == 20) {
            if(nextColor == 4) {
                nextColor = 0;
            } else {
                nextColor++;
            }
            colorTracker = 0;
        }
        int color = colors[nextColor];
        return new Obstacle(this, new Vector2f(x, y), width, height, color);
    }

    private GameSnapshot snapshot() {
        Set<Obstacle.Snapshot> obstacles = new HashSet<>(this.obstacles.size());
        for (Obstacle obstacle : this.obstacles) {
            obstacles.add(obstacle.snapshot());
        }
        return new GameSnapshot(obstacles);
    }
}
