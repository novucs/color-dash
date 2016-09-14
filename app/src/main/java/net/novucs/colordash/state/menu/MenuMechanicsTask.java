package net.novucs.colordash.state.menu;

import android.graphics.Color;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.ColorDash;
import net.novucs.colordash.MechanicsThread;
import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.entity.Obstacle;
import net.novucs.colordash.entity.Player;
import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.MechanicsTask;
import net.novucs.colordash.state.Snapshot;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MenuMechanicsTask implements MechanicsTask {

    private final ColorDash game;

    //Int that keeps track of the color to render the play button.
    private int activeColor = 0;
    private int colorChangeCounter = 0;
    private int trophyColor = 0;

    //How many ticks we should change the color of our items on the screen.
    private static final int COLOR_CHANGE = 15;


    //Double the speed for rendering in the bars.
    private static final int RENDER_SPEED = 2;

    private final Map<EntityType, Entity.Manager> entityManagers = new EnumMap<>(EntityType.class);

    public static final ImmutableList<Integer> colors = ImmutableList.of(
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
            trophyColor = MechanicsThread.getRandom().nextInt(colors.size() - 1);
            colorChangeCounter = 0;
        }
        for (Entity.Manager manager : entityManagers.values()) {
            manager.tick(2);
        }
    }

    @Override
    public Snapshot snapshot() {
        return MenuSnapshot.builder()
                .state(ApplicationState.MENU)
                .playColor(colors.get(activeColor))
                .trophyColor(colors.get(trophyColor))
                .entities(snapshotEntities())
                .build();
    }

    public void setup() {
        entityManagers.put(EntityType.OBSTACLE, new Obstacle.Manager(game));
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
