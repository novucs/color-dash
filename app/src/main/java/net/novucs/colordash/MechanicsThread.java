package net.novucs.colordash;

import java.util.concurrent.atomic.AtomicBoolean;

public class MechanicsThread extends Thread {

    private static final int TPS = 30;

    private final ColorDash colorDash;
    private final AtomicBoolean running = new AtomicBoolean();

    public MechanicsThread(ColorDash colorDash) {
        super("mechanics-thread");
        this.colorDash = colorDash;
    }

    public void initialize() {
        running.set(true);
        start();
    }

    public void terminate() {
        running.set(false);
    }

    @Override
    public void run() {
        long tickStart;
        long tickDuration;

        while (running.get()) {
            tickStart = System.currentTimeMillis();

            // Pass current tick snapshot to render thread.
            GameSnapshot snapshot = new GameSnapshot();
            colorDash.getRenderThread().setSnapshot(snapshot);

            tickDuration = System.currentTimeMillis() - tickStart;

            if (TPS > tickDuration) {
                try {
                    sleep(TPS - tickDuration);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
