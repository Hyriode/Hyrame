package fr.hyriode.hyrame.item.spectator;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServerManager;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.impl.HyramePlugin;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpecReplayItem extends HyriSpectatorItem {

    public SpecReplayItem(HyramePlugin plugin) {
        super(plugin, "spec_replay_item", "item.spectator.replay.name", "item.spectator.replay.lore", Material.PAPER);
    }

    @Override
    protected void onClick(IHyrame hyrame, PlayerInteractEvent event) {
        final IHyriServerManager server = HyriAPI.get().getServerManager();
        final HyriGame<?> game = hyrame.getGameManager().getCurrentGame();

        //TODO With Queue
    }
}
