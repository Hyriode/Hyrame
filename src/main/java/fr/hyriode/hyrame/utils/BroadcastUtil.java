package fr.hyriode.hyrame.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/01/2022 at 17:15
 */
public class BroadcastUtil {

    /**
     * Broadcast a message
     *
     * @param message The message function
     */
    public static void broadcast(Function<Player, String> message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message.apply(player));
        }
    }

}
