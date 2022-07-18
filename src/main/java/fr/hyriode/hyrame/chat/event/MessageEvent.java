package fr.hyriode.hyrame.chat.event;

import fr.hyriode.api.event.HyriCancellableEvent;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 30/04/2022 at 18:28
 */
public class MessageEvent extends HyriCancellableEvent {

    private final UUID player;
    private final String message;

    public MessageEvent(UUID player, String message) {
        this.player = player;
        this.message = message;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

}
