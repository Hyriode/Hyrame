package fr.hyriode.hyrame.utils;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:16
 */
public class Cancellable {

    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
