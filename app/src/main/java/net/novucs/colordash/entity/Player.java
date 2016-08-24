package net.novucs.colordash.entity;

import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.math.Vector2f;

import java.util.Set;

public final class Player extends Entity {

    // Default percentage radius of the player.
    private static final float RADIUS = 0.03f;

    // Acceleration of the player.
    private static final Vector2f ACCELERATION = new Vector2f(0.001f, 0.001f);

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
        checkInputs();
        checkCollisions();
        setLocation(getNextLocation());
    }

    private void checkInputs() {
        float xSpeed = ACCELERATION.getX() * getGame().getMechanicsThread().getGameSpeed() * getGame().getPanel().getWidth();
        float ySpeed = ACCELERATION.getY() * getGame().getMechanicsThread().getGameSpeed() * getGame().getPanel().getHeight();

        switch (getGame().getPanel().getLastClickType()) {
            case LEFT:
                setVelocity(getVelocity().add(-xSpeed, ySpeed));
                break;
            case RIGHT:
                setVelocity(getVelocity().add(xSpeed, ySpeed));
                break;
        }
    }

    private Vector2f checkCollisions() {
        Set<Obstacle> obstacles = ((Obstacle.Manager) getGame().getMechanicsThread().getEntityManagers().get(EntityType.OBSTACLE)).getObstacles();
        Vector2f nextLocation = getNextLocation();

        for (Obstacle obstacle : obstacles) {
            if (!intersectsX(nextLocation, obstacle) || !intersectsY(nextLocation, obstacle)) {
                continue;
            }

            float ySpeed = Obstacle.getMoveSpeed() * getGame().getMechanicsThread().getGameSpeed() * getGame().getPanel().getHeight();
            setLocation(new Vector2f(getLocation().getX(), obstacle.getLocation().getY() - getRadius()));
            setVelocity(new Vector2f(getVelocity().getX(), ySpeed));
            return new Vector2f(nextLocation.getX(), obstacle.getLocation().getY() + ySpeed);
        }

        return nextLocation;
    }

    private Vector2f getNextLocation() {
        float x = clamp(getVelocity().getX() + getLocation().getX(), 0, getGame().getPanel().getWidth());
        float y = clamp(getVelocity().getY() + getLocation().getY(), 0, getGame().getPanel().getHeight());
        return new Vector2f(x, y);
    }

    private boolean intersectsX(Vector2f location, Obstacle obstacle) {
        float aLeft = location.getX();
        float aRight = getRadius() + aLeft;
        float bLeft = obstacle.getLocation().getX();
        float bRight = obstacle.getWidth() + bLeft;
        return aLeft < bRight && aRight > bLeft;
    }

    private boolean intersectsY(Vector2f location, Obstacle obstacle) {
        float aTop = location.getY();
        float aBottom = getRadius() + aTop;
        float bTop = obstacle.getLocation().getY();
        float bBottom = obstacle.getHeight() + bTop;
        return aTop < bBottom && aBottom > bTop;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
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
