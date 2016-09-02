package net.novucs.colordash;

import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.game.GameMechanicsTask;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MechanicsThread extends Thread implements GameService {

    private static final Random RANDOM = new Random();

    // Target ticks per second.
    private static final int TPS = 30;

    // Duration in millis a tick should last, calculated from the TPS.
    private static final int NORMAL_TICK_DURATION = (int) TimeUnit.SECONDS.toMillis(1) / TPS;

    private final ColorDash game;

    private MechanicsTask task;

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
            task.tick();

            // Pass current tick snapshot to render thread.
            game.getRenderThread().setSnapshot(task.snapshot());

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

    /**
     * Data initialization on the mechanics thread before the game loop begins.
     */
    private void setup() {
        task = new GameMechanicsTask(game);
        ((GameMechanicsTask) task).setup();
    }

    /**
     * Data cleanup on the mechanics thread after the game loop stops.
     */
    private void finish() {
        ((GameMechanicsTask) task).finish();
    }
}
