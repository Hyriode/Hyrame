package fr.hyriode.hyrame.generator;

import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/01/2022 at 18:23
 */
public interface IHyriGeneratorTier {

    /**
     * The display name of the tier.<br>
     * It returns a function with a {@link Player} in parameter because the name can be different for a given player.
     *
     * @return A function returning a {@link String}
     */
    Function<Player, String> getName();

    /**
     * Get the maximum of items that can spawns.<br>
     * If -1 is set, there will be no maximum.
     *
     * @return A number
     */
    int getSpawnLimit();

    /**
     * Get the time to wait before spawning a new item.<br>
     * The value must be in the range: [0; {@link Long#MAX_VALUE}].<br>
     * Warning: the time is in Minecraft ticks: 1 seconds = 20 ticks.
     *
     * @return A time in ticks
     */
    long getTimeBetweenSpawns();

    /**
     * Check if the tier accept item splitting.<br>
     * It means that if two or more players are in the generator, the items will be duplicating for each player.
     *
     * @return <code>true</code> if the tier accept splitting
     */
    boolean isSplitting();

    /**
     * A builder class that can be used to create a {@link IHyriGeneratorTier} without implementing the interface.
     */
    class Builder {

        /** The display name of the tier */
        private Function<Player, String> name;
        /** The maximum of items that can spawn. By default, 12 */
        private int spawnLimit = 12;
        /** The time to wait before spawning items. By default, 8 seconds */
        private long timeBetweenSpawns = 8 * 20;
        /** <code>true</code> if items are splitting when two or more players are in the generator */
        private boolean splitting;

        /**
         * Set the display name of the {@link  IHyriGeneratorTier}
         *
         * @param name A function applied for each player
         * @return <code>this</code> instance
         */
        public Builder withName(Function<Player, String> name) {
            this.name = name;
            return this;
        }

        /**
         * Set the maximum of items that can spawn
         *
         * @param spawnLimit The items limit. In the range: {0, {@link Integer#MAX_VALUE}}
         * @return <code>this</code> instance
         */
        public Builder withSpawnLimit(int spawnLimit) {
            this.spawnLimit = spawnLimit;
            return this;
        }

        /**
         * Set the time to wait before each item spawn.<br>
         * Warning: The time is in ticks, so 1 seconds = 20 ticks
         *
         * @param timeBetweenSpawns The time to wait in ticks.
         * @return <code>this</code> instance
         */
        public Builder withTimeBetweenSpawns(long timeBetweenSpawns) {
            this.timeBetweenSpawns = timeBetweenSpawns;
            return this;
        }

        /**
         * Set that generator is now splitting items when two or more players are in the generator
         *
         * @return <code>this</code> instance
         */
        public Builder withSplitting() {
            this.splitting = true;
            return this;
        }

        /**
         * Build the builder to return a {@link  IHyriGeneratorTier} instance
         *
         * @return The built {@link  IHyriGeneratorTier} instance
         */
        public IHyriGeneratorTier build() {
            return new IHyriGeneratorTier() {
                @Override
                public Function<Player, String> getName() {
                    return name;
                }

                @Override
                public int getSpawnLimit() {
                    return spawnLimit;
                }

                @Override
                public long getTimeBetweenSpawns() {
                    return timeBetweenSpawns;
                }

                @Override
                public boolean isSplitting() {
                    return splitting;
                }
            };
        }

    }

}
