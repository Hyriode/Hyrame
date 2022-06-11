package fr.hyriode.hyrame.impl.tablist;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeamHandler;
import fr.hyriode.hyrame.tablist.ITabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AstFaster
 * on 30/05/2022 at 19:51
 */
public class HyriTabListManager implements ITabListManager {

    private boolean enabled;

    private final HyriRanksHandler ranksHandler;

    private final Map<String, TeamHandler> teamHandlers;
    private final HyriScoreboardTeamHandler teamHandler;

    private final JavaPlugin plugin;
    private final Handler handler;

    public HyriTabListManager(IHyrame hyrame, JavaPlugin plugin) {
        this.plugin = plugin;
        this.handler = new Handler();
        this.teamHandlers = new HashMap<>();
        this.teamHandler = new HyriScoreboardTeamHandler();
        this.ranksHandler = new HyriRanksHandler(hyrame, this);
    }

    @Override
    public void enable() {
        if (this.enabled) {
            throw new IllegalStateException("TabList manager is already enabled!");
        }

        this.enabled = true;

        this.plugin.getServer().getPluginManager().registerEvents(this.handler, this.plugin);
    }

    @Override
    public void disable() {
        if (!this.enabled) {
            throw new IllegalStateException("TabList manager is not enabled!");
        }

        this.teamHandlers.clear();
        this.teamHandler.destroy(false);

        HandlerList.unregisterAll(this.handler);

        this.enabled = false;
    }

    @Override
    public void registerTeam(HyriScoreboardTeam team) {
        final String teamName = team.getName();

        if (this.teamHandlers.containsKey(teamName)) {
            this.unregisterTeam(teamName);
        }

        final HyriScoreboardTeam hiddenTeam = team.clone();

        hiddenTeam.setNameTagVisibility(HyriScoreboardTeam.NameTagVisibility.NEVER);
        hiddenTeam.getPlayers().clear();
        hiddenTeam.setName(teamName + "-h");
        hiddenTeam.setRealName((teamName.length() >= 14 ? teamName.substring(0, 14) : teamName) + "-h");

        this.teamHandler.addTeam(team);
        this.teamHandler.addTeam(hiddenTeam);

        this.teamHandlers.put(teamName, new TeamHandler(team, hiddenTeam));
    }

    @Override
    public void unregisterTeam(String teamName) {
        final TeamHandler handler = this.teamHandlers.remove(teamName);

        if (handler == null) {
            return;
        }

        this.teamHandler.removeTeam(handler.getDefaultTeam().getName());
        this.teamHandler.removeTeam(handler.getHiddenTeam().getName());
    }

    @Override
    public void updateTeam(String teamName) {
        this.teamHandler.updateTeam(teamName);
    }

    @Override
    public HyriScoreboardTeam getTeam(String teamName) {
        return this.teamHandler.getTeamByName(teamName);
    }

    @Override
    public boolean containsTeam(String teamName) {
        return this.teamHandlers.containsKey(teamName);
    }

    @Override
    public void addPlayerInTeam(Player player, String teamName) {
        this.teamHandler.addPlayerToTeam(player, this.getTeam(teamName));
    }

    @Override
    public void removePlayerFromTeam(Player player) {
        this.teamHandler.removePlayerFromTeam(player, this.getPlayerTeam(player));
    }

    @Override
    public HyriScoreboardTeam getPlayerTeam(Player player) {
        return this.teamHandler.getPlayerTeam(player);
    }

    @Override
    public void hideNameTag(Player player) {
        final TeamHandler teamHandler = this.getTeamHandler(player);

        if (teamHandler == null) {
            return;
        }

        this.teamHandler.addPlayerToTeam(player, teamHandler.getHiddenTeam());
    }

    @Override
    public void showNameTag(Player player) {
        final TeamHandler teamHandler = this.getTeamHandler(player);

        if (teamHandler == null) {
            return;
        }

        this.teamHandler.addPlayerToTeam(player, teamHandler.getDefaultTeam());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    private TeamHandler getTeamHandler(Player player) {
        final HyriScoreboardTeam team = this.getPlayerTeam(player);

        return team == null ? null : this.teamHandlers.get(team.getName());
    }

    public HyriRanksHandler getRanksHandler() {
        return this.ranksHandler;
    }

    private static class TeamHandler {

        private final HyriScoreboardTeam defaultTeam;
        private final HyriScoreboardTeam hiddenTeam;

        public TeamHandler(HyriScoreboardTeam defaultTeam, HyriScoreboardTeam hiddenTeam) {
            this.defaultTeam = defaultTeam;
            this.hiddenTeam = hiddenTeam;
        }

        public HyriScoreboardTeam getDefaultTeam() {
            return this.defaultTeam;
        }

        public HyriScoreboardTeam getHiddenTeam() {
            return this.hiddenTeam;
        }

    }

    private class Handler implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onJoin(PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            teamHandler.addReceiver(player);
            ranksHandler.onLogin(player);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onQuit(PlayerQuitEvent event) {
            final Player player = event.getPlayer();

            teamHandler.removeReceiver(event.getPlayer());
            ranksHandler.onLogout(player);
        }

    }

}
