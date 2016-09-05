package net.novucs.colordash.state.menu;

import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.Snapshot;

public class MenuSnapshot implements Snapshot {
    @Override
    public ApplicationState getState() {
        return ApplicationState.MENU;
    }
}
