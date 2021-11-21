package fr.hyriode.tools;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/11/2021 at 11:02
 */
public class PlayerUtil {

    /**
     * Get an offline player by his name
     *
     * @param name - Player name
     * @return - Player object
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

}
