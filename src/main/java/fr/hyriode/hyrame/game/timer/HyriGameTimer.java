package fr.hyriode.hyrame.game.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 05/03/2022 at 08:56
 */
public class HyriGameTimer implements Runnable {

    /** The current time of the timer */
    private long currentTime = 0;

    /** The consumer to fire when the time is changed */
    private final List<Consumer<Long>> timeChangedActions = new ArrayList<>();

    @Override
    public void run() {
        this.currentTime++;

        this.timeChangedActions.forEach(consumer -> consumer.accept(this.currentTime));
    }

    /**
     * Set the consumer that will be fired when the time is changed
     *
     * @param timeChanged A {@link Consumer} of the current time
     */
    public void setOnTimeChanged(Consumer<Long> timeChanged) {
        this.timeChangedActions.add(timeChanged);
    }

    /**
     * Get all the actions that are running when the timing changes
     *
     * @return A list of consumer of long
     */
    public List<Consumer<Long>> getTimeChangedActions() {
        return this.timeChangedActions;
    }

    /**
     * Get the current time of the timer
     *
     * @return A time in seconds
     */
    public long getCurrentTime() {
        return this.currentTime;
    }

}
