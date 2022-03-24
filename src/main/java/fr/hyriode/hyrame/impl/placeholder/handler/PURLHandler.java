package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:29
 */
public class PURLHandler extends PlaceholderPrefixHandler {

    public PURLHandler() {
        super("url");
    }

    @Override
    public String handle(Player player, String placeholder) {
        switch (placeholder) {
            case "discord":
                return HyriConstants.DISCORD_URL;
            case "website":
                return HyriConstants.WEBSITE_URL;
            case "store":
                return HyriConstants.STORE_WEBSITE_URL;
        }
        return null;
    }

}
