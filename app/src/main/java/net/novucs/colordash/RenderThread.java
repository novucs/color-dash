package net.novucs.colordash;

import java.util.concurrent.atomic.AtomicBoolean;

public class RenderThread extends Thread {

    private final ColorDash colorDash;
    private final BlockingReference<GameSnapshot> snapshot = new BlockingReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public RenderThread(ColorDash colorDash) {
        super("render-thread");
        this.colorDash = colorDash;
    }

    public void initialize() {
        running.set(true);
        start();
    }

    public void terminate() {
        running.set(false);
    }

    public void setSnapshot(GameSnapshot snapshot) {
        this.snapshot.update(snapshot);
    }

    @Override
    public void run() {
        while (running.get()) {
            GameSnapshot snapshot;

            try {
                snapshot = this.snapshot.retrieve();
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to retrieve a snapshot", e);
            }

            // TODO: Render the game snapshot here...
        }
    }
}
