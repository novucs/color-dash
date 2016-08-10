package net.novucs.colordash;

import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.math.Vector2f;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MechanicsThread extends Thread {

    private static final int TPS = 30;

    private final ColorDash colorDash;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Set<Obstacle> obstacles = new HashSet<>();
    private int width;
    private int height;

    public MechanicsThread(ColorDash colorDash) {
        super("mechanics-thread");
        this.colorDash = colorDash;
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

            tick();

            // Pass current tick snapshot to render thread.
            colorDash.getRenderThread().setSnapshot(snapshot());

            tickDuration = System.currentTimeMillis() - tickStart;

            if (TPS > tickDuration) {
                try {
                    sleep(TPS - tickDuration);
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
        float width = this.width * 0.25f;
        float height = this.height * 0.05f;
        float x = 0;
        float y = this.height * 0.10f;
        return new Obstacle(width, height, new Vector2f(x, y));
    }

    private GameSnapshot snapshot() {
        Set<Obstacle.Snapshot> obstacles = new HashSet<>(this.obstacles.size());
        for (Obstacle obstacle : this.obstacles) {
            obstacles.add(obstacle.snapshot());
        }
        return new GameSnapshot(obstacles);
    }
}
