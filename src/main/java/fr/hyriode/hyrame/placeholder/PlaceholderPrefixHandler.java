package fr.hyriode.hyrame.placeholder;

import org.bukkit.OfflinePlayer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:44
 */
public abstract class PlaceholderPrefixHandler extends PlaceholderHandler {

    /** The prefix to check before handling a placeholder */
    protected final String prefix;

    /**
     * Constructor of {@link PlaceholderPrefixHandler}
     *
     * @param prefix The prefix to check
     */
    public PlaceholderPrefixHandler(String prefix) {
        this.prefix = prefix + "_";
    }

    @Override
    public String preHandle(OfflinePlayer player, String placeholder) {
        if (placeholder.startsWith(prefix)) {
            return super.preHandle(player, placeholder.substring(prefix.length()));
        }
        return null;
    }

}
