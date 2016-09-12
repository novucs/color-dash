package net.novucs.colordash.state.menu;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;
import net.novucs.colordash.state.ApplicationState;
import net.novucs.colordash.state.Snapshot;
import net.novucs.colordash.state.game.GameSnapshot;

public class MenuSnapshot implements Snapshot {
    private ApplicationState state;

    //Represents the current color of the play button
    private int playColor;
    //Represents the current color of the trophy
    private int tropheyColor;

    public MenuSnapshot(ApplicationState state, int playColor, int tropheyColor) {
        this.state = state;
        this.playColor = playColor;
        this.tropheyColor = tropheyColor;
    }

    @Override
    public ApplicationState getState() {
        return ApplicationState.MENU;
    }

    public int getPlayColor() {
        return playColor;
    }

    public int getTropheyColor() {
        return tropheyColor;
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new {@link MenuSnapshot.Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ApplicationState state;
        private int playColor;
        private int tropheyColor;

        public Builder state(ApplicationState state) {
            this.state = state;
            return this;
        }

        public Builder playColor(int playColor) {
            this.playColor = playColor;
            return this;
        }

        public Builder tropheyColor(int tropheyColor) {
            this.tropheyColor = tropheyColor;
            return this;
        }

        /**
         * Checks if the current builder is safe to build.
         *
         * @return <code>true</code> if this can be built.
         */
        public boolean isValid() {
            return state != null && playColor != 0;
        }

        /**
         * Builds the game snapshot with current input.
         *
         * @return a new {@link MenuSnapshot} from the builder.
         * @throws IllegalStateException if the builder is invalid.
         */
        public MenuSnapshot build() {
            // Throw an exception if the builder is invalid.
            if (!isValid()) {
                throw new IllegalStateException();
            }

            return new MenuSnapshot(state, playColor, tropheyColor);
        }
    }
}
