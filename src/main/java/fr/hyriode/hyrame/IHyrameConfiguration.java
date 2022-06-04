package fr.hyriode.hyrame;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 20:32
 */
public interface IHyrameConfiguration {

    /**
     * Are ranks showed in the tab list
     *
     * @return <code>true</code> if yes
     */
    boolean areRanksInTabList();

    /**
     * Set if ranks are showed in the tab list
     *
     * @param value New value
     */
    void setRanksInTabList(boolean value);

    /**
     * Check if Hyrame is in build mode.<br>
     * If it's in build mode, no more game could be registered.
     *
     * @return <code>true</code> if yes
     */
    boolean isBuildMode();

    /**
     * Set whether Hyrame is in build mode or not.<br>
     * If yes, Hyrame will assume that you will start a configuration creation process and will disable many features like games.
     *
     * @param buildMode <code>true</code> to enable build mode
     */
    void setBuildMode(boolean buildMode);

}
