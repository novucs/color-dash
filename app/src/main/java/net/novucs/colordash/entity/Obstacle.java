package net.novucs.colordash.entity;

import android.graphics.Color;
import android.graphics.Paint;

import net.novucs.colordash.MechanicsThread;
import net.novucs.colordash.math.Vector2f;

public class Obstacle extends Entity {

    private float width;
    private float height;
    private final int color;

    public Obstacle(MechanicsThread mechanicsThread, Vector2f location, float width, float height, int color) {
        super(mechanicsThread, location);
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

    @Override
    public void tick() {
        float distance = getMechanicsThread().getHeight() * getMechanicsThread().getGameSpeed() * 0.002f;
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

}
