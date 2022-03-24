package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.placeholder.PlaceholderHandler;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:29
 */
public class PServerHandler extends PlaceholderPrefixHandler {

    public PServerHandler() {
        super("server");
    }

    @Override
    public String handle(Player player, String placeholder) {
        final IHyriServer server = HyriAPI.get().getServer();

        switch (placeholder) {
            case "name":
                return server.getName();
            case "type":
                return server.getType();
            case "player_count":
                return String.valueOf(server.getPlayers());
        }
        return null;
    }

}
