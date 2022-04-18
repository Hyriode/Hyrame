package fr.hyriode.hyrame.item.spectator;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.stream.Collectors;

public class SpecTeleportItem extends HyriSpectatorItem {

    public SpecTeleportItem(HyramePlugin plugin) {
        super(plugin, "spec_teleport_item","item.spectator.teleport.name","item.spectator.teleport.lore", Material.COMPASS);
    }

    @Override
    protected void onClick(IHyrame hyrame, PlayerInteractEvent event) {
        new Gui(hyrame, event.getPlayer()).open();
    }

    public static class Gui extends HyriInventory {

        public Gui(IHyrame hyrame, Player owner) {
            super(owner, HyriInventory.name(hyrame, owner, "gui.spectator.teleport.title"),
                    dynamicSize((int) hyrame.getGameManager().getCurrentGame().getPlayers().stream().filter(player -> !player.isDead() || !player.isSpectator()).count()));

            final List<HyriGamePlayer> players = hyrame.getGameManager().getCurrentGame().getPlayers().stream().filter(player -> !player.isDead() && !player.isSpectator()).collect(Collectors.toList());
            final IHyriLanguageManager lang = hyrame.getLanguageManager();

            final int[] slot = {0};
            players.forEach(player -> {
                this.setItem(slot[0], new ItemBuilder(Material.SKULL_ITEM, 1, 3)
                                .withSkullOwner(player.getUUID()).withName(player.getPlayer().getName())
                                .withLore(lang.getValue(player.getUUID(), "gui.spectator.teleport.player_lore")).build(),
                        e -> this.owner.teleport(player.getPlayer()));
            });
        }
    }
}
