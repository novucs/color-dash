package net.novucs.colordash;

import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.Snapshot;
import net.novucs.colordash.state.game.GameMechanicsTask;
import net.novucs.colordash.state.game.GameRenderTask;
import net.novucs.colordash.state.menu.MenuMechanicsTask;
import net.novucs.colordash.state.menu.MenuRenderTask;

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

    /**
     * Gets the current task.
     *
     * @return the task.
     */
    public MechanicsTask getTask() {
        return task;
    }

    @Override
    public void initialize() {
        start();
        System.out.println("Yes hello!!");
        task = new MenuMechanicsTask(game);
    }

    @Override
    public void terminate() {
        interrupt();
    }

    @Override
    public void run() {
        long tickStart;
        long tickDuration;

        while (!isInterrupted()) {
            tickStart = System.currentTimeMillis();

            //updateState(task.snapshot());

            // Perform the game mechanics tick.
            task.tick();

            //Snapshot snapshot = task.snapshot();

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
    }

    /**
     * Data cleanup on the mechanics thread after the game loop stops.
     */
    private void finish() {
        ((GameMechanicsTask) task).finish();
    }
}
