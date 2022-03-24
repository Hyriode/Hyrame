package fr.hyriode.hyrame.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 17:54
 */
public abstract class PlaceholderHandler {

    /**
     * This method is called before handling placeholder
     *
     * @param player The player
     * @param placeholder The placeholder to handle
     * @return A replaced placeholder
     */
    public String preHandle(OfflinePlayer player, String placeholder) {
        if (player != null && player.isOnline()) {
            return this.handle((Player) player, placeholder);
        }
        return this.handle(null, placeholder);
    }

    /**
     * This method called is used to handler placeholder
     *
     * @param player The player
     * @param placeholder The placeholder to handle
     * @return A replaced placeholder
     */
    public String handle(Player player, String placeholder) {
        return null;
    }

}
