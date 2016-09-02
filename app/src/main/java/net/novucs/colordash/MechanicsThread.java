package net.novucs.colordash;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.entity.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MechanicsThread extends Thread implements GameService, Tickable {

    private static final Random RANDOM = new Random();

    // Target ticks per second.
    private static final int TPS = 30;

    // Duration in millis a tick should last, calculated from the TPS.
    private static final int NORMAL_TICK_DURATION = (int) TimeUnit.SECONDS.toMillis(1) / TPS;

    // How much the game speed should be multiplied by each tick.
    private static final float GAME_SPEED_MULTIPLIER = 1.0001f;

    // The maximum speed the game can hit.
    private static final float MAX_GAME_SPEED = 5;

    // The default game speed.
    private static final float DEFAULT_GAME_SPEED = 1;

    private final ColorDash game;
    private final Map<EntityType, Entity.Manager> entityManagers = new EnumMap<>(EntityType.class);

    private float gameSpeed;

    private int score;

    public MechanicsThread(ColorDash game) {
        super("mechanics-thread");
        this.game = game;
    }

    /**
     * Gets the thread local random.
     *
     * @return the random.
     */
    public static Random getRandom() {
        return RANDOM;
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

    @Override
    public void initialize() {
        start();
    }

    @Override
    public void terminate() {
        interrupt();
    }

    @Override
    public void run() {
        long tickStart;
        long tickDuration;

        setup();

        while (!isInterrupted()) {
            tickStart = System.currentTimeMillis();

            // Perform the game mechanics tick.
            tick();

            // Pass current tick snapshot to render thread.
            game.getRenderThread().setSnapshot(snapshot());

            tickDuration = System.currentTimeMillis() - tickStart;

            if (NORMAL_TICK_DURATION > tickDuration) {
                try {
                    sleep(NORMAL_TICK_DURATION - tickDuration);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        finish();
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

    /**
     * Data initialization on the mechanics thread before the game loop begins.
     */
    private void setup() {
        gameSpeed = DEFAULT_GAME_SPEED;

        entityManagers.put(EntityType.OBSTACLE, new Obstacle.Manager(game));
        entityManagers.put(EntityType.PLAYER, new Player.Manager(game));
        game.setApplicationState(ApplicationState.PLAYING);
        for (Entity.Manager manager : entityManagers.values()) {
            manager.initialize();
        }
    }

    /**
     * Data cleanup on the mechanics thread after the game loop stops.
     */
    private void finish() {
        for (Entity.Manager manager : entityManagers.values()) {
            manager.terminate();
        }

        entityManagers.clear();
    }

    /**
     * Creates a new snapshot of the entire game, ready for rendering. This may
     * only be executed at the end of the tick, otherwise there will be data
     * loss.
     *
     * @return the game snapshot.
     */
    private GameSnapshot snapshot() {
        return GameSnapshot.builder()
                .snapshot(snapshotEntities(), score)
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
