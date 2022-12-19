package fr.hyriode.hyrame.impl.host.config;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.HyriGameType;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.impl.host.category.HostMainCategory;
import fr.hyriode.hyrame.impl.host.category.team.HostTeamsCategory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.TimeUtil;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.List;

/**
 * Created by AstFaster
 * on 08/08/2022 at 15:23
 */
public class HostConfigFormatter {

    public static ItemStack createItem(Player player, IHostConfig config, boolean loaded, boolean compatible) {
        final IHyriGameInfo gameInfo = HyriAPI.get().getGameManager().getGameInfo(config.getGame());
        final HyriGameType gameType = gameInfo == null ? null : gameInfo.getType(config.getGameType());

        if (gameInfo == null || gameType == null) {
            config.delete();
            return null;
        }

        final String configId = config.getId();
        final HostConfigIcon icon = HostConfigIcon.from(config);
        final Object players = config.getValue(HostMainCategory.PLAYERS_KEY);
        final Object teamsSize = config.getValue(HostTeamsCategory.TEAMS_SIZE_KEY);
        final List<String> lore = ListReplacer.replace(HyrameMessage.HOST_CONFIG_ITEM_LORE.asList(player), "%id%", configId)
                .replace("%owner%", IHyriPlayer.get(config.getOwner()).getNameWithRank())
                .replace("%date%", TimeUtil.formatDate(new Date(config.getCreationDate())))
                .replace("%private%", config.isPrivate() ? ChatColor.GREEN + Symbols.TICK_BOLD : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD)
                .replace("%loadings%", String.valueOf(HyriAPI.get().getHostConfigManager().getConfigLoadings(configId)))
                .replace("%players%", String.valueOf(players == null ? "?" : players))
                .replace("%game%", gameInfo.getDisplayName())
                .replace("%game_type%", gameType.getDisplayName())
                .replace("%teams_size%", String.valueOf(teamsSize == null ? 1 : (int) teamsSize))
                .list();

        lore.add("");
        lore.add((compatible ? (loaded ? HyrameMessage.HOST_CONFIG_ALREADY_LOADED_LINE : HyrameMessage.HOST_CLICK_TO_LOAD) : HyrameMessage.HOST_CONFIG_INCOMPATIBLE_LINE).asString(player));

        return new ItemBuilder(icon == null ? HostConfigIcon.PAPER.asItem() : icon.asItem())
                .withName(ChatColor.AQUA + config.getName())
                .withLore(lore)
                .withAllItemFlags()
                .build();
    }

}
