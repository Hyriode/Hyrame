package fr.hyriode.hyrame.impl.game.util.spectator;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.item.HyriItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class RestartGameItem extends HyriItem<HyramePlugin> {

    public RestartGameItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.RESTART_GAME_NAME, displayName("item.spectator.play-again"), Material.NETHER_STAR);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            final Player player = event.getPlayer();

            if (!HyriAPI.get().getQueueManager().addPlayerInQueue(player.getUniqueId(), game.getName(), game.getType().getName(), null, true)) {
                player.sendMessage(HyriLanguageMessage.get("message.queue.not-leader").getValue(player));
            }
        }

    }

}
