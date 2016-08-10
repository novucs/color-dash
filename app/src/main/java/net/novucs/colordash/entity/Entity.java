package net.novucs.colordash.entity;

import net.novucs.colordash.MechanicsThread;
import net.novucs.colordash.math.Vector2f;

public abstract class Entity implements Tickable {

    private final MechanicsThread mechanicsThread;
    private Vector2f location;

    public Entity(MechanicsThread mechanicsThread, Vector2f location) {
        this.mechanicsThread = mechanicsThread;
        this.location = location;
    }

    public MechanicsThread getMechanicsThread() {
        return mechanicsThread;
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
}
