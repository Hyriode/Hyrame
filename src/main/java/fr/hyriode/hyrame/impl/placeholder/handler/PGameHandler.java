package fr.hyriode.hyrame.impl.placeholder.handler;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.placeholder.PlaceholderPrefixHandler;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/03/2022 at 10:28
 */
public class PGameHandler extends PlaceholderPrefixHandler {

    private final IHyrame hyrame;

    public PGameHandler(IHyrame hyrame) {
        super("game");
        this.hyrame = hyrame;
    }

    @Override
    public String handle(Player player, String placeholder) {
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            switch (placeholder) {
                case "name":
                    return game.getName();
                case "display_name":
                    return game.getDisplayName();
                case "players":
                    return String.valueOf(game.getPlayers().size());
                case "max_players":
                    return String.valueOf(game.getMaxPlayers());
                case "min_players":
                    return String.valueOf(game.getMinPlayers());
                case "spectators":
                    return String.valueOf(game.getSpectators().size());
            }
        }
        return null;
    }
}
