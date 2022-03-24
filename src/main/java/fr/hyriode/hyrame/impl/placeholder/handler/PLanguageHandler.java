package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 09:16
 */
class PLanguageHandler extends PlaceholderPrefixHandler {

    public PLanguageHandler() {
        super("lang");
    }

    @Override
    public String handle(Player player, String placeholder) {
        if (player == null) {
            return null;
        }
        return HyriLanguageMessage.get(placeholder).getForPlayer(player);
    }

}
