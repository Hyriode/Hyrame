package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.configuration.HyrameConfiguration;
import fr.hyriode.hyrame.util.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.rank.EHyriRank;
import fr.hyriode.hyriapi.rank.HyriRank;
import fr.hyriode.tools.scoreboard.team.ScoreboardTeam;
import fr.hyriode.tools.scoreboard.team.ScoreboardTeamHandler;
import fr.hyriode.tools.tab.Tab;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HyriTabManager {

    private final Map<Player, Tab> defaultTabs;

    private final ScoreboardTeamHandler teamHandler;

    private final Hyrame hyrame;

    public HyriTabManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.teamHandler = new ScoreboardTeamHandler();
        this.defaultTabs = new HashMap<>();

        final List<EHyriRank> ranks = Arrays.asList(EHyriRank.values());

        Collections.reverse(ranks);

        for (EHyriRank rankEnum : ranks) {
            final HyriRank rank = HyriAPI.get().getRankManager().getRank(rankEnum);
            final String name = rank.getName();
            final String display = this.getDisplayName(rank.getDisplayName());
            final ScoreboardTeam team = new ScoreboardTeam(name, name, display, display, "");

            this.teamHandler.addTeam(team);
        }
    }

    private String getDisplayName(String displayName) {
        displayName += " ";

        return displayName.substring(0, Math.min(displayName.length(), 16));
    }

    public void onLogin(Player player) {
        ThreadPool.EXECUTOR.execute(() -> {
            final HyrameConfiguration configuration = this.hyrame.getConfiguration();

            if (configuration.isTabListRanks()) {
                this.teamHandler.addReceiver(player);

                final IHyriPlayer hyriPlayer = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
                final ScoreboardTeam team = this.teamHandler.getTeamByName(hyriPlayer.getRank().getName());

                this.teamHandler.addPlayerToTeam(player, team);

                player.setDisplayName(hyriPlayer.getRank().getDisplayName() + " " + player.getName());
            }

            if (configuration.isDefaultTabLines()) {
                final Tab defaultTab = new HyriDefaultTab(this.hyrame, player);

                this.defaultTabs.put(player, defaultTab);
            }
        });
    }

    public void onLogout(Player player) {
        ThreadPool.EXECUTOR.execute(() -> {
            this.teamHandler.removeReceiver(player);

            this.defaultTabs.remove(player);
        });
    }

    public void enable() {
        this.hyrame.getConfiguration().setTabListRanks(true);

        ThreadPool.EXECUTOR.execute(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.teamHandler.addReceiver(player);
            }
        });
    }

    public void disable() {
        this.hyrame.getConfiguration().setTabListRanks(false);

        ThreadPool.EXECUTOR.execute(() -> {
            for (Player player : this.defaultTabs.keySet()) {
                this.teamHandler.removeReceiver(player);
            }
        });
    }

}
