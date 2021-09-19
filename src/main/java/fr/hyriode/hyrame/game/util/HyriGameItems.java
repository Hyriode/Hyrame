package fr.hyriode.hyrame.game.util;

import fr.hyriode.common.item.ItemBuilder;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.gui.HyriGameTeamChooseGui;
import fr.hyriode.hyrame.util.References;
import fr.hyriode.hyriapi.HyriAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 19/09/2021 at 12:47
 */
public class HyriGameItems {

    public static final TriFunction<Hyrame, HyriGamePlayer, Integer, ItemStack> CHOOSE_TEAM = (hyrame, gamePlayer, slot) -> new ItemBuilder(Material.WOOL, 1, gamePlayer.hasTeam() ? gamePlayer.getTeam().getColor().getData() : (byte) 0)
            .withName(ChatColor.DARK_AQUA + "Les équipes")
            .withEvent(PlayerInteractEvent.class, e -> {
                final PlayerInteractEvent event = (PlayerInteractEvent) e.get();

                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    new HyriGameTeamChooseGui(hyrame, hyrame.getGameManager().getCurrentGame(), gamePlayer.getPlayer().getPlayer(), slot).open();
                }
            })
            .build();

    public static final Function<Player, ItemStack> LEAVE = player ->  new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
            .withName(ChatColor.RED + References.EXIT_MESSAGE.getForPlayer(player))
            .withEvent(PlayerInteractEvent.class, e -> {
                final PlayerInteractEvent event = (PlayerInteractEvent) e.get();

                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUniqueId());
                }
            })
            .build();

    @FunctionalInterface
    public interface TriFunction<T, T1, T2, R> {

        R apply(T t, T1 t1, T2 t2);

    }

}
