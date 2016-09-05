package net.novucs.colordash.state.menu;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.Snapshot;

public class MenuMechanicsTask implements MechanicsTask {

    private final ColorDash game;

    public MenuMechanicsTask(ColorDash game) {
        this.game = game;
    }

    @Override
    public void tick() {

    }

    @Override
    public Snapshot snapshot() {
        return null;
    }
}
