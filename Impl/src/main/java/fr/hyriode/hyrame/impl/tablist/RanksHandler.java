package fr.hyriode.hyrame.impl.tablist;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.event.NicknameUpdatedEvent;
import fr.hyriode.api.player.event.RankUpdatedEvent;
import fr.hyriode.api.player.model.IHyriNickname;
import fr.hyriode.api.rank.IHyriRank;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.tablist.ITabListManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by AstFaster
 * on 30/05/2022 at 20:45
 */
public class RanksHandler {

    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final IHyrame hyrame;
    private final ITabListManager tabListManager;

    public RanksHandler(IHyrame hyrame, ITabListManager tabListManager) {
        this.hyrame = hyrame;
        this.tabListManager = tabListManager;

        HyriAPI.get().getEventBus().register(this);
    }

    public void onLogin(Player player) {
        if (this.hyrame.getConfiguration().areRanksInTabList()) {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer account = IHyriPlayer.get(playerId);
            final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);
            final IHyriNickname nickname = session.getNickname();
            final String display = this.getTeamDisplay(account, session);
            final HyriScoreboardTeam team = new HyriScoreboardTeam(playerId.toString(), this.generateTeamName(nickname.has() ? nickname.getRank().getTabListPriority() : account.getTabListPriority()), display, display, "");

            this.tabListManager.registerTeam(team);
            this.tabListManager.addPlayerInTeam(player, team.getName());
        }
    }

    public void onLogout(Player player) {
        this.tabListManager.unregisterTeam(player.getUniqueId().toString());
    }

    private String getTeamDisplay(IHyriPlayer player, IHyriPlayerSession session) {
        if (session.getNickname().has()) {
            return ChatColor.translateAlternateColorCodes('&', session.getNickname().getRank().getDefaultPrefix());
        } else if (player.getRank().withSeparator()) {
            return ChatColor.translateAlternateColorCodes('&', player.getPrefix()) + IHyriRank.SEPARATOR + player.getRank().getMainColor();
        } else {
            return ChatColor.translateAlternateColorCodes('&', player.getPrefix()) + player.getRank().getMainColor();
        }
    }

    private String generateTeamName(int priority) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            builder.append(CHARS[ThreadLocalRandom.current().nextInt(CHARS.length)]);
        }
        return String.valueOf(CHARS[priority % 24]) + builder;
    }

    @HyriEventHandler
    public void onNicknameUpdated(NicknameUpdatedEvent event) {
        final Player player = Bukkit.getPlayer(event.getPlayerId());

        this.onLogout(player);

        Bukkit.getScheduler().runTaskLater(IHyrame.get().getPlugin(), () -> this.onLogin(player), 1L);
    }

    @HyriEventHandler
    public void onRankUpdated(RankUpdatedEvent event) {
        final Player player = Bukkit.getPlayer(event.getPlayerId());

        this.onLogout(player);

        Bukkit.getScheduler().runTaskLater(IHyrame.get().getPlugin(), () -> this.onLogin(player), 1L);
    }

}
