package fr.hyriode.hyrame.scoreboard;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriScoreboardLine {

    /** Line's update */
    private Update update;

    /** Line's value */
    private String value;

    /**
     * Constructor of {@link HyriScoreboardLine}
     *
     * @param value - Line's value
     */
    public HyriScoreboardLine(String value) {
        this.value = value;
    }

    /**
     * Constructor of {@link HyriScoreboardLine}
     *
     * @param value Line's value
     * @param update Line's update
     */
    public HyriScoreboardLine(String value, Update update) {
        this.value = value;
        this.update = update;
    }

    /**
     * Get line's value
     *
     * @return - Line's value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Set line's value
     *
     * @param value - New value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get line's update
     *
     * @return - A {@link Update} object
     */
    public Update getUpdate() {
        return this.update;
    }

    public static class Update {

        /** Update runnable */
        private final BukkitRunnable runnable;

        /** Number of ticks to wait before updating */
        private final int ticks;

        /**
         * Constructor of {@link Update}
         *
         * @param runnable Update runnable
         * @param ticks Ticks to wait
         */
        public Update(BukkitRunnable runnable, int ticks) {
            this.runnable = runnable;
            this.ticks = ticks;
        }

        /**
         * Get update runnable
         *
         * @return - A runnable
         */
        public BukkitRunnable getRunnable() {
            return this.runnable;
        }

        /**
         * Get number of ticks to wait
         *
         * @return - A number
         */
        public int getTicks() {
            return this.ticks;
        }

    }

}
