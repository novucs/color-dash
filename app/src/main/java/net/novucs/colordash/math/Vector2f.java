package net.novucs.colordash.math;

public class Vector2f implements Cloneable {

    private final float x;
    private final float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }

    public Vector2f add(Vector2f other) {
        return add(other.getX(), other.getY());
    }

    public Vector2f sub(float x, float y) {
        return new Vector2f(this.x - x, this.y - y);
    }

    public Vector2f sub(Vector2f other) {
        return sub(other.getX(), other.getY());
    }

    @Override
    public Vector2f clone() {
        try {
            return (Vector2f) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
