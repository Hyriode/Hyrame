package fr.hyriode.hyrame.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/12/2021 at 19:06
 */
public interface IHyriChatHandler {

    /**
     * Method fired when a player chat
     *
     * @param event Event fired
     * @return <code>true</code> if handlers processing can continue
     */
    default boolean onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(this.isCancelled());
        return true;
    }

    /**
     * Get current chat format<br>
     * Example: %s: %s where fist %s is the player name and the second one is the message
     *
     * @return Chat format
     */
    String format();

    /**
     * Check if chat is cancelled
     *
     * @return <code>true</code> if yes
     */
    boolean isCancelled();

    /**
     * Set if chat is cancelled
     *
     * @param cancelled New value
     */
    void setCancelled(boolean cancelled);

}
