package net.novucs.colordash;

import com.google.common.collect.ImmutableMultimap;

import net.novucs.colordash.entity.Entity;
import net.novucs.colordash.entity.EntityType;

/**
 * A snapshot of a game tick for the render thread to process.
 */
public final class GameSnapshot {

    private final ImmutableMultimap<EntityType, Entity.Snapshot> entities;

    private GameSnapshot(ImmutableMultimap<EntityType, Entity.Snapshot> entities) {
        this.entities = entities;
    }

    /**
     * Returns a render-safe immutable multimap of entity snapshots.
     *
     * @return the entities.
     */
    public ImmutableMultimap<EntityType, Entity.Snapshot> getEntities() {
        return entities;
    }

    /**
     * Creates a new builder instance.
     *
     * @return a new {@link GameSnapshot.Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ImmutableMultimap<EntityType, Entity.Snapshot> entities;

        /**
         * Sets the entity snapshots to be rendered.
         *
         * @param entities the entity snapshot multimap.
         * @return this.
         */
        public Builder entities(ImmutableMultimap<EntityType, Entity.Snapshot> entities) {
            this.entities = entities;
            return this;
        }

        /**
         * Checks if the current builder is safe to build.
         *
         * @return <code>true</code> if this can be built.
         */
        public boolean isValid() {
            return entities != null;
        }

        /**
         * Builds the game snapshot with current input.
         *
         * @return a new {@link GameSnapshot} from the builder.
         * @throws IllegalStateException if the builder is invalid.
         */
        public GameSnapshot build() {
            // Throw an exception if the builder is invalid.
            if (!isValid()) {
                throw new IllegalStateException();
            }

            return new GameSnapshot(entities);
        }
    }
}
