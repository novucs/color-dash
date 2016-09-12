package net.novucs.colordash.entity;

import android.graphics.Color;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.MechanicsThread;
import net.novucs.colordash.math.Vector2f;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Obstacle extends Entity {

    // Obstacle dimensions.
    private static final float HEIGHT = 0.03f;
    private static final int SEGMENT_COUNT = 5;

    // Speed modifier for only obstacles.
    private static final float MOVE_SPEED = -0.003f;

    // Duration in ticks between obstacle spawns.
    private static final int SPAWN_TICKS = 60;

    // Number of obstacles pushed before their color changes.
    private static final int COLOR_COUNT = 4;

    // All available colors obstacles can be.
    public static final ImmutableList<Integer> COLORS = ImmutableList.of(
            Color.parseColor("#FF0000"),
            Color.parseColor("#FF00EF"),
            Color.parseColor("#3FE0FF"),
            Color.parseColor("#3FFF72"),
            Color.parseColor("#FFDE00")
    );

    private float width;
    private float height;
    private int color;
    private boolean left;

    public Obstacle(ColorDash game, Vector2f location, float width, float height, int color, boolean left) {
        super(game, location);
        this.width = width;
        this.height = height;
        this.color = color;
        this.left = left;
    }

    public static float getMoveSpeed() {
        return MOVE_SPEED;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isLeft() {
        return left;
    }

    @Override
    public void tick(float gameSpeed) {
        float distance = getGame().getPanel().getHeight() * gameSpeed * MOVE_SPEED;
        getLocation().addY(distance);
    }

    @Override
    public Snapshot snapshot() {
        return new Snapshot(getLocation().clone(), width, height, color);
    }

    public final class Snapshot extends Entity.Snapshot {

        private final float width;
        private final float height;
        private final int color;

        Snapshot(Vector2f location, float width, float height, int color) {
            super(location);
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public int getColor() {
            return color;
        }

        public boolean isLeft() {
            return left;
        }
    }

    public static final class Manager implements Entity.Manager {

        private final ColorDash game;
        private final Set<Obstacle> obstacles = new HashSet<>();

        private int spawnTickCounter;
        private int colorCounter;

        public Manager(ColorDash game) {
            this.game = game;
        }

        public Set<Obstacle> getObstacles() {
            return obstacles;
        }

        @Override
        public void initialize() {
            colorCounter = 0;
            spawnTickCounter = 0;
        }

        @Override
        public void terminate() {
        }

        @Override
        public void tick(float gameSpeed) {
            if (++spawnTickCounter * gameSpeed >= SPAWN_TICKS) {
                spawnObstaclePair();
                spawnTickCounter = 0;
            }

            // Get the obstacles iterator.
            Iterator<Obstacle> it = obstacles.iterator();

            // Iterate through each obstacle.
            while (it.hasNext()) {
                tickObstacle(gameSpeed, it);
            }
        }

        private void tickObstacle(float gameSpeed, Iterator<Obstacle> it) {
            Obstacle obstacle;
            obstacle = it.next();

            // Tick the obstacle.
            obstacle.tick(gameSpeed);

            // Remove the obstacle if it is out of the screen range.
            if (obstacle.getLocation().getY() < -obstacle.getHeight()) {
                it.remove();
            }
        }

        @Override
        public ImmutableSet<Entity.Snapshot> snapshot() {
            ImmutableSet.Builder<Entity.Snapshot> target = ImmutableSet.builder();
            for (Obstacle obstacle : obstacles) {
                target.add(obstacle.snapshot());
            }
            return target.build();
        }

        private void spawnObstaclePair() {
            int color = tickColor();

            float segmentSize = game.getPanel().getWidth() / SEGMENT_COUNT;
            float width = MechanicsThread.getRandom().nextFloat() * segmentSize * (SEGMENT_COUNT - 1);
            width = Math.round(width / segmentSize) * segmentSize;

            float height = game.getPanel().getHeight() * HEIGHT;
            float x = 0;
            float y = game.getPanel().getHeight();
            obstacles.add(new Obstacle(game, new Vector2f(x, y), width, height, color, true));

            x = width + segmentSize;
            width = game.getPanel().getWidth() - width - segmentSize;
            obstacles.add(new Obstacle(game, new Vector2f(x, y), width, height, color, false));
        }

        private int tickColor() {
            int current = colorCounter++ / COLOR_COUNT;
            if (current >= COLORS.size()) {
                colorCounter = 0;
                current = 0;
            }
            return COLORS.get(current);
        }
    }
}
