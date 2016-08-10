package net.novucs.colordash;

import net.novucs.colordash.entity.Obstacle;

import java.util.Set;

public final class GameSnapshot {

    private final Set<Obstacle.Snapshot> obstacles;

    public GameSnapshot(Set<Obstacle.Snapshot> obstacles) {
        this.obstacles = obstacles;
    }

    public Set<Obstacle.Snapshot> getObstacles() {
        return obstacles;
    }
}
