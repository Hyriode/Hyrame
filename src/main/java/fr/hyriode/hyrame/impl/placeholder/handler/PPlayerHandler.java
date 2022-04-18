package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:29
 */
public class PPlayerHandler extends PlaceholderPrefixHandler {

    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public PPlayerHandler() {
        super("player");
    }

    @Override
    public String handle(Player player, String placeholder) {
        if (player == null) {
            return null;
        }
        final String[] splitted = placeholder.split("_");

        UUID playerId = player.getUniqueId();
        if (splitted.length > 0 && UUID_REGEX_PATTERN.matcher(splitted[splitted.length - 1]).matches()) {
            playerId = UUID.fromString(splitted[splitted.length - 1]);
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);

        switch (placeholder) {
            case "name":
                return account.getName();
            case "display_name":
                return account.getCustomName();
            case "proxy":
                return account.getCurrentProxy();
            case "hyris":
                return String.valueOf(account.getHyris().getAmount());
            case "uuid":
                return playerId.toString();
            case "prefix":
                return account.getRank().getPrefix();
            case "full_prefix":
                return player.getDisplayName();
        }
        return null;
    }

}
