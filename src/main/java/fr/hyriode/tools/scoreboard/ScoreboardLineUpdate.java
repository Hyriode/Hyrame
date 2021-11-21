package fr.hyriode.tools.scoreboard;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ScoreboardLineUpdate {

    /** Update runnable */
    private final BukkitRunnable runnable;

    /** Number of ticks to wait before updating */
    private final int ticks;

    /**
     * Constructor of {@link ScoreboardLineUpdate}
     *
     * @param runnable - Update runnable
     * @param ticks - Ticks to wait
     */
    public ScoreboardLineUpdate(BukkitRunnable runnable, int ticks) {
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
