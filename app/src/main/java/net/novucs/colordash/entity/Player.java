package net.novucs.colordash.entity;

import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.math.Vector2f;

public class Player extends Entity {

    // Default percentage radius of the player.
    private static final float RADIUS = 0.03f;

    private float radius;
    private Vector2f velocity;

    public Player(ColorDash game, Vector2f location, float radius, Vector2f velocity) {
        super(game, location);
        this.radius = radius;
        this.velocity = velocity;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    @Override
    public void tick() {
    }

    @Override
    public Snapshot snapshot() {
        return new Snapshot(getLocation().clone(), getRadius());
    }

    public final class Snapshot extends Entity.Snapshot {

        private final float radius;

        public Snapshot(Vector2f location, float radius) {
            super(location);
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }
    }

    public static class Manager implements Entity.Manager {

        private final ColorDash game;
        private Player player;

        public Manager(ColorDash game) {
            this.game = game;
        }

        @Override
        public ImmutableSet<Entity.Snapshot> snapshot() {
            return ImmutableSet.of((Entity.Snapshot) player.snapshot());
        }

        @Override
        public void initialize() {
            float x = game.getPanel().getWidth() / 2;
            float y = game.getPanel().getHeight() / 4;
            float radius = game.getPanel().getWidth() * RADIUS;
            player = new Player(game, new Vector2f(x, y), radius, new Vector2f(0, 0));
        }

        @Override
        public void terminate() {
            player = null;
        }

        @Override
        public void tick() {
            player.tick();
        }
    }
}
