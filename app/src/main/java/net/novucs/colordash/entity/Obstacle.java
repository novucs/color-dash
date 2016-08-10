package net.novucs.colordash.entity;

import net.novucs.colordash.math.Vector2f;

public class Obstacle {

    private float width;
    private float height;
    private Vector2f location;

    public Obstacle(float width, float height, Vector2f location) {
        this.width = width;
        this.height = height;
        this.location = location;
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

    public Vector2f getLocation() {
        return location;
    }

    public void setLocation(Vector2f location) {
        this.location = location;
    }

    public void tick() {
        location = location.add(0, 10);
    }

    public Snapshot snapshot() {
        return new Snapshot(width, height, location.clone());
    }

    public final class Snapshot {

        private final float width;
        private final float height;
        private final Vector2f location;

        private Snapshot(float width, float height, Vector2f location) {
            this.width = width;
            this.height = height;
            this.location = location;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public Vector2f getLocation() {
            return location;
        }
    }
}
