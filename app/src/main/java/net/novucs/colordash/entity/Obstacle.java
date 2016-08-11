package net.novucs.colordash.entity;

import android.graphics.Color;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.math.Vector2f;

import java.util.HashSet;
import java.util.Set;

public final class Obstacle extends Entity {

    // Default obstacle percentage dimensions.
    private static final float WIDTH = 0.25f;
    private static final float HEIGHT = 0.03f;

    // Speed modifier for only obstacles.
    private static final float MOVE_SPEED = 0.002f;

    // Duration in ticks between obstacle spawns.
    private static final int SPAWN_TICKS = 60;

    // Number of obstacles pushed before their color changes.
    private static final int COLOR_COUNT = 4;

    // All available colors obstacles can be.
    private static final ImmutableList<Integer> COLORS = ImmutableList.of(
            Color.parseColor("#FF0000"),
            Color.parseColor("#FF00EF"),
            Color.parseColor("#3FE0FF"),
            Color.parseColor("#3FFF72"),
            Color.parseColor("#FFDE00")
    );

    private float width;
    private float height;
    private int color;

    public Obstacle(ColorDash game, Vector2f location, float width, float height, int color) {
        super(game, location);
        this.width = width;
        this.height = height;
        this.color = color;
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

    @Override
    public void tick() {
        float distance = getGame().getPanel().getHeight() * getGame().getMechanicsThread().getGameSpeed() * MOVE_SPEED;
        setLocation(getLocation().add(0, distance));
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
    }

    public static final class Manager implements Entity.Manager {

        private final ColorDash game;
        private final Set<Obstacle> obstacles = new HashSet<>();

        private int spawnTickCounter;
        private int colorCounter;

        public Manager(ColorDash game) {
            this.game = game;
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
        public void tick() {
            if (++spawnTickCounter >= SPAWN_TICKS) {
                obstacles.add(createObstacle());
                spawnTickCounter = 0;
            }

            for (Obstacle obstacle : obstacles) {
                obstacle.tick();
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

        private Obstacle createObstacle() {
            int color = tickColor();
            float width = game.getPanel().getWidth() * WIDTH;
            float height = game.getPanel().getHeight() * HEIGHT;
            float x = 0;
            float y = 0 - height;
            return new Obstacle(game, new Vector2f(x, y), width, height, color);
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
