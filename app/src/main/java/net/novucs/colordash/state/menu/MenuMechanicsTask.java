package net.novucs.colordash.state.menu;

import android.graphics.Color;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.MechanicsThread;
import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.Snapshot;

import java.util.Arrays;
import java.util.List;

public class MenuMechanicsTask implements MechanicsTask {

    private final ColorDash game;

    //Int that keeps track of the color to render the play button.
    private int activeColor = 0;
    private int colorChangeCounter = 0;
    private int tropheyColor = 2;

    //How many ticks we should change the color of our items on the screen.
    private static final int COLOR_CHANGE = 15;

    private List<Integer> colors = Arrays.asList(
            Color.parseColor("#ff4000"),
            Color.parseColor("#ff8000"),
            Color.parseColor("#ffff00"),
            Color.parseColor("#40ff00"),
            Color.parseColor("#00ff80"),
            Color.parseColor("#0040ff"),
            Color.parseColor("#8000ff"),
            Color.parseColor("#bf00ff"),
            Color.parseColor("#ff0080")
    );

    public MenuMechanicsTask(ColorDash game) {
        this.game = game;
    }

    @Override
    public void tick() {
        colorChangeCounter++;
        if (colorChangeCounter == COLOR_CHANGE) {
            activeColor = MechanicsThread.getRandom().nextInt(colors.size() - 1);
            tropheyColor = MechanicsThread.getRandom().nextInt(colors.size() - 1);
            colorChangeCounter = 0;
        }
    }

    @Override
    public Snapshot snapshot() {
        return MenuSnapshot.builder()
                .state(ApplicationState.MENU)
                .playColor(colors.get(activeColor))
                .tropheyColor(colors.get(tropheyColor))
                .build();
    }
}
