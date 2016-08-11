package net.novucs.colordash.entity;

import com.google.common.collect.ImmutableSet;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.GameService;
import net.novucs.colordash.Tickable;
import net.novucs.colordash.math.Vector2f;

public abstract class Entity implements Tickable {

    private final ColorDash game;
    private Vector2f location;

    public Entity(ColorDash game, Vector2f location) {
        this.game = game;
        this.location = location;
    }

    public ColorDash getGame() {
        return game;
    }

    public Vector2f getLocation() {
        return location;
    }

    public void setLocation(Vector2f location) {
        this.location = location;
    }

    public abstract Snapshot snapshot();

    public abstract class Snapshot {

        private final Vector2f location;

        Snapshot(Vector2f location) {
            this.location = location;
        }

        public Vector2f getLocation() {
            return location;
        }
    }

    public interface Manager extends GameService, Tickable {
        ImmutableSet<Snapshot> snapshot();
    }
}
