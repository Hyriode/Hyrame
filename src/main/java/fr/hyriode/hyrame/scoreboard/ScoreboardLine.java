package fr.hyriode.hyrame.scoreboard;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ScoreboardLine {

    /** Line's update */
    private ScoreboardLineUpdate update;

    /** Line's value */
    private String value;

    /**
     * Constructor of {@link ScoreboardLine}
     *
     * @param value - Line's value
     */
    public ScoreboardLine(String value) {
        this.value = value;
    }

    /**
     * Constructor of {@link ScoreboardLine}
     *
     * @param value - Line's value
     * @param update - Line's update
     */
    public ScoreboardLine(String value, ScoreboardLineUpdate update) {
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
     * @return - A {@link ScoreboardLineUpdate} object
     */
    public ScoreboardLineUpdate getUpdate() {
        return this.update;
    }

}
