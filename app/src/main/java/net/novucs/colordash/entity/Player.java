package net.novucs.colordash.entity;

import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.math.Vector2f;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.game.GameMechanicsTask;

import java.util.Set;

public final class Player extends Entity {

    // Default percentage radius of the player.
    private static final float RADIUS = 0.02f;

    // Acceleration of the player.
    private static final float ACCELERATION_X = 0.001f;
    private static final float ACCELERATION_Y = 0.001f;

    private float radius;
    private Vector2f velocity;
    private Obstacle currentObstacle;

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
    public void tick(float gameSpeed) {
        checkInputs(gameSpeed);
        checkCollisions(gameSpeed);
        setLocation(getNextLocation());
    }

    private void checkInputs(float gameSpeed) {
        float xSpeed = ACCELERATION_X * gameSpeed * getGame().getPanel().getWidth();

        switch (getGame().getPanel().getLastClickType()) {
            case LEFT:
                velocity.addX(-xSpeed);
                break;
            case RIGHT:
                velocity.addX(xSpeed);
                break;
        }
    }

    private void checkCollisions(float gameSpeed) {
        checkObstacleCollisions(gameSpeed);
        checkWallCollisions();
    }

    private void checkWallCollisions() {
        Vector2f nextLocation = getNextLocation();
        float maxX = getGame().getPanel().getWidth() - getRadius();
        float minX = getRadius();
        float maxY = getGame().getPanel().getHeight() - getRadius();
        float minY = getRadius();

        if (nextLocation.getX() > maxX) {
            getVelocity().setX(maxX - getLocation().getX());
        }

        if (nextLocation.getX() < minX) {
            getVelocity().setX(minX - getLocation().getX());
        }

        if (nextLocation.getY() > maxY) {
            getVelocity().setY(maxY - getLocation().getY());
        }

        if (nextLocation.getY() < minY) {
            getVelocity().setY(minY - getLocation().getY());
        }
    }

    private void checkObstacleCollisions(float gameSpeed) {
        MechanicsTask task = getGame().getMechanicsThread().getTask();
        if (!(task instanceof GameMechanicsTask)) {
            return;
        }

        if (currentObstacle != null) {
            if (intersectsX(getNextLocation(), currentObstacle)) {
                getLocation().setY(currentObstacle.getLocation().getY() - getRadius());
                getVelocity().setY(getObstacleSpeed(gameSpeed));
            } else {
                currentObstacle = null;
                getVelocity().setY(getGravitySpeed(gameSpeed));
            }
            return;
        }

        currentObstacle = getIntersectingObstacle();

        if (currentObstacle != null) {
            ((GameMechanicsTask) task).incrementScore();
            getLocation().setY(currentObstacle.getLocation().getY() - getRadius());
            getVelocity().setY(getObstacleSpeed(gameSpeed));
        } else {
            getVelocity().addY(getGravitySpeed(gameSpeed));
        }
    }

    private Obstacle getIntersectingObstacle() {
        MechanicsTask task = getGame().getMechanicsThread().getTask();
        GameMechanicsTask gameTask = (GameMechanicsTask) task;
        Set<Obstacle> obstacles = ((Obstacle.Manager) gameTask.getEntityManagers().get(EntityType.OBSTACLE)).getObstacles();
        for (Obstacle obstacle : obstacles) {
            if (intersects(getNextLocation(), obstacle)) {
                return obstacle;
            }
        }
        return null;
    }

    private float getGravitySpeed(float gameSpeed) {
        return ACCELERATION_Y * gameSpeed * getGame().getPanel().getHeight();
    }

    private float getObstacleSpeed(float gameSpeed) {
        return Obstacle.getMoveSpeed() * gameSpeed * getGame().getPanel().getHeight();
    }

    private Vector2f getNextLocation() {
        float x = getVelocity().getX() + getLocation().getX();
        float y = getVelocity().getY() + getLocation().getY();
        return new Vector2f(x, y);
    }

    private boolean intersects(Vector2f location, Obstacle obstacle) {
        return intersectsX(location, obstacle) && intersectsY(location, obstacle);
    }

    private boolean intersectsX(Vector2f location, Obstacle obstacle) {
        float aLeft = location.getX();
        float aRight = getRadius() + aLeft;
        float bLeft = obstacle.getLocation().getX();
        float bRight = obstacle.getWidth() + bLeft;
        return aLeft <= bRight && aRight >= bLeft;
    }

    private boolean intersectsY(Vector2f location, Obstacle obstacle) {
        float aTop = location.getY();
        float aBottom = getRadius() + aTop;
        float bTop = obstacle.getLocation().getY();
        float bBottom = obstacle.getHeight() + bTop;
        return aTop <= bBottom && aBottom >= bTop;
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
            float radius = game.getPanel().getHeight() * RADIUS;
            player = new Player(game, new Vector2f(x, y), radius, new Vector2f(0, 0));
        }

        @Override
        public void terminate() {
            player = null;
        }

        @Override
        public void tick(float gameSpeed) {
            player.tick(gameSpeed);
        }
    }
}
