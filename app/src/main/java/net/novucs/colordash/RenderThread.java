package net.novucs.colordash;

import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.RenderTask;
import net.novucs.colordash.state.Snapshot;
import net.novucs.colordash.state.game.GameRenderTask;
import net.novucs.colordash.state.menu.MenuRenderTask;
import net.novucs.colordash.util.BlockingReference;

public class RenderThread extends Thread implements GameService {

    private final ColorDash game;
    private final BlockingReference<Snapshot> snapshot = new BlockingReference<>();
    private RenderTask task;
    private ApplicationState state;

    public RenderThread(ColorDash game) {
        super("render-thread");
        this.game = game;
    }

    @Override
    public void initialize() {
        start();
    }

    @Override
    public void terminate() {
        interrupt();
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot.set(snapshot);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Snapshot snapshot;

            try {
                snapshot = this.snapshot.take();
            } catch (InterruptedException e) {
                break;
            }

            updateState(snapshot.getState());
            task.render(snapshot);
        }
    }

    private void updateState(ApplicationState newState) {
        if (newState == state) {
            return;
        }

        switch (newState) {
            case GAME:
                task = new GameRenderTask(game);
                break;
            case MENU:
                task = new MenuRenderTask(game);
                break;
        }

        state = newState;
    }
}
