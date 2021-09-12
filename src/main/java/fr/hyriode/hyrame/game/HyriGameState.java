package fr.hyriode.hyrame.game;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:26
 */
public enum HyriGameState {

    WAITING(true),
    READY(false),
    PLAYING(false),
    ENDED(false);

    private final boolean reachable;

    HyriGameState(boolean reachable) {
        this.reachable = reachable;
    }

    public boolean isReachable() {
        return this.reachable;
    }

}
