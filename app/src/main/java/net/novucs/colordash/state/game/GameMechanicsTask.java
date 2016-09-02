package net.novucs.colordash.state.game;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.entity.Player;
import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.Snapshot;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameMechanicsTask implements MechanicsTask {

    // How much the game speed should be multiplied by each tick.
    private static final float GAME_SPEED_MULTIPLIER = 1.0001f;

    // The maximum speed the game can hit.
    private static final float MAX_GAME_SPEED = 5;

    // The default game speed.
    private static final float DEFAULT_GAME_SPEED = 1;

    private final Map<EntityType, Entity.Manager> entityManagers = new EnumMap<>(EntityType.class);
    private final ColorDash game;
    private float gameSpeed;
    private int score;
    private ApplicationState state;

    public ColorDash getGame() {
        return game;
    }

    /**
     * Returns the game speed, this should affect all entity movement speeds.
     *
     * @return the game speed.
     */
    public float getGameSpeed() {
        return gameSpeed;
    }

    /**
     * Returns all the entity managers.
     *
     * @return the entity managers.
     */
    public Map<EntityType, Entity.Manager> getEntityManagers() {
        return entityManagers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public GameMechanicsTask(ColorDash game) {
        this.game = game;
    }

    public void setup() {
        gameSpeed = DEFAULT_GAME_SPEED;

        entityManagers.put(EntityType.OBSTACLE, new Obstacle.Manager(game));
        entityManagers.put(EntityType.PLAYER, new Player.Manager(game));
        this.state = ApplicationState.GAME;
        for (Entity.Manager manager : entityManagers.values()) {
            manager.initialize();
        }
    }

    public void finish() {
        for (Entity.Manager manager : entityManagers.values()) {
            manager.terminate();
        }

        entityManagers.clear();
    }

    @Override
    public void tick() {
        // Update the game speed.
        gameSpeed = Math.min(MAX_GAME_SPEED, gameSpeed * GAME_SPEED_MULTIPLIER);

        // Tick all entity managers.
        for (Entity.Manager manager : entityManagers.values()) {
            manager.tick();
        }
    }

    @Override
    public Snapshot snapshot() {
        return GameSnapshot.builder()
                .entities(snapshotEntities())
                .score(score)
                .state(state)
                .build();
    }

    /**
     * Creates a new snapshot view of all the entities from the current game
     * state for rendering. Should only be executed at the end of a tick, once
     * all entity calculations have been executed.
     *
     * @return the entity snapshot multimap.
     */
    private ImmutableMultimap<EntityType, Entity.Snapshot> snapshotEntities() {
        // Create the entity snapshot multimap builder.
        ImmutableMultimap.Builder<EntityType, Entity.Snapshot> target = ImmutableMultimap.builder();

        // Add all entity snapshots from each entity manager to the builder.
        for (Map.Entry<EntityType, Entity.Manager> entry : entityManagers.entrySet()) {
            target.putAll(entry.getKey(), entry.getValue().snapshot());
        }

        // Build and return the entity snapshot multimap.
        return target.build();
    }
}
