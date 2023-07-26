package fr.hyriode.hyrame.impl.game.util.spectator;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/11/2021 at 19:20
 */
public class SpectatorTeleporterItem extends HyriItem<HyramePlugin> {

    public SpectatorTeleporterItem(HyramePlugin plugin) {
        super(plugin, HyriGameItems.SPECTATOR_TELEPORTER_NAME, () -> HyriLanguageMessage.get("item.spectator.teleporter"), null, Material.COMPASS);
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        new Gui(hyrame, event.getPlayer()).open();
    }

    private static class Gui extends HyriInventory {

        public Gui(IHyrame hyrame, Player owner) {
            super(owner, HyriInventory.name(owner, "gui.spectator.teleporter.title"), dynamicSize((int) hyrame.getGameManager().getCurrentGame().getPlayers().stream().filter(player -> !player.isDead() || !player.isSpectator()).count()));

            final List<HyriGamePlayer> players = hyrame.getGameManager().getCurrentGame().getPlayers().stream().filter(player -> player.getUniqueId() == owner.getUniqueId()).filter(player -> !player.isDead() && !player.isSpectator()).collect(Collectors.toList());

            int currentSlot = 0;
            for (HyriGamePlayer gamePlayer : players) {
                if (!gamePlayer.isOnline()) {
                    continue;
                }

                final Player player = gamePlayer.getPlayer();

                final ItemStack itemStack = new ItemBuilder(Material.SKULL_ITEM, 1, 3)
                        .withPlayerHead(player.getUniqueId())
                        .withName(gamePlayer.formatNameWithTeam())
                        .withLore(HyriLanguageMessage.get("gui.spectator.teleporter.lore").getValue(this.owner))
                        .build();

                this.setItem(currentSlot, itemStack, event -> {
                    this.owner.closeInventory();
                    this.owner.teleport(player.getLocation());
                });

                currentSlot++;
            }
        }
    }

}
