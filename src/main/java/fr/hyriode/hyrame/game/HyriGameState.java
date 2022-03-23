package fr.hyriode.hyrame.game;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:26
 */
public enum HyriGameState {

    /** Game is waiting for players */
    WAITING(true),
    /** Game is ready to start */
    READY(true),
    /** Game is playing */
    PLAYING(false),
    /** Game is ended */
    ENDED(false);

    /** The boolean that represents the accessibility of the server */
    private final boolean accessible;

    /**
     * Constructor of {@link HyriGameState}
     *
     * @param accessible Access value
     */
    HyriGameState(boolean accessible) {
        this.accessible = accessible;
    }

    /**
     * Check if the server is accessible
     *
     * @return <code>true</code> if yes
     */
    public boolean isAccessible() {
        return this.accessible;
    }

}
