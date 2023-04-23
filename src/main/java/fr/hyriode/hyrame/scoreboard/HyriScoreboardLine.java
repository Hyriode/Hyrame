package fr.hyriode.hyrame.scoreboard;

import java.util.function.Consumer;

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

        private int tickIndex = 0;

        private final Consumer<HyriScoreboardLine> lineConsumer;
        /** Number of ticks to wait before updating */
        private final int ticks;

        public Update(Consumer<HyriScoreboardLine> lineConsumer, int ticks) {
            this.lineConsumer = lineConsumer;
            this.ticks = ticks;
        }

        /**
         * Get number of ticks to wait
         *
         * @return - A number
         */
        public int getTicks() {
            return this.ticks;
        }

        public boolean onTick(HyriScoreboardLine line) {
            this.tickIndex++;

            if (this.ticks == this.tickIndex) {
                this.lineConsumer.accept(line);
                this.tickIndex = 0;
                return true;
            }
            return false;
        }
    }

}
