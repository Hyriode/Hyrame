package fr.hyriode.hyrame.chat.event;

import fr.hyriode.api.event.HyriCancellableEvent;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 18/07/2022 at 18:21
 *
 * This event is triggered when a player tries to send a private message to another player
 */
public class PrivateMessageEvent extends HyriCancellableEvent {

    /** The unique id of the player sending a message */
    private final UUID senderId;
    /** The unique id of the player that will receive the message */
    private final UUID targetId;

    /**
     * The constructor of a {@link PrivateMessageEvent}
     *
     * @param senderId The unique id of the sender
     * @param targetId The unique id of the target
     */
    public PrivateMessageEvent(UUID senderId, UUID targetId) {
        this.senderId = senderId;
        this.targetId = targetId;
    }

    /**
     * Get the unique identifier of the sender
     *
     * @return A player {@link UUID}
     */
    public UUID getSenderId() {
        return this.senderId;
    }

    /**
     * Get the unique identifier of the target
     *
     * @return A player {@link UUID}
     */
    public UUID getTargetId() {
        return this.targetId;
    }

}
