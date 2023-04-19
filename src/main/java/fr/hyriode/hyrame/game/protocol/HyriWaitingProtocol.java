package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.host.HostData;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.game.util.gui.TeamChooserGUI;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.scoreboard.HyriScoreboard;
import fr.hyriode.hyrame.scoreboard.IScoreboardManager;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/02/2022 at 19:53
 */
public class HyriWaitingProtocol extends HyriGameProtocol implements Listener {

    private static final Class<HyriWaitingScoreboard> SCOREBOARD_CLASS = HyriWaitingScoreboard.class;

    private boolean teamSelector;

    private final JavaPlugin plugin;

    public HyriWaitingProtocol(IHyrame hyrame, JavaPlugin plugin, boolean teamSelector) {
        super(hyrame, "waiting");
        this.plugin = plugin;
        this.teamSelector = teamSelector;

        HyriAPI.get().getEventBus().register(this);
    }

    public HyriWaitingProtocol(IHyrame hyrame, JavaPlugin plugin) {
        this(hyrame, plugin, true);
    }

    public boolean isTeamSelector() {
        return this.teamSelector;
    }

    public HyriWaitingProtocol withTeamSelector(boolean teamSelector) {
        this.teamSelector = teamSelector;
        return this;
    }

    @Override
    void enable() {
        final HyriGame<?> game = this.getGame();

        game.getStartingTimer().setOnTimeChanged(time -> game.getPlayers().forEach(gamePlayer -> this.runActionOnScoreboard(gamePlayer.getPlayer(), scoreboard -> {
            scoreboard.setTime(time);
            scoreboard.update();
        })));
    }

    @Override
    void disable() {
        final HyriGame<?> game = this.getGame();

        game.getPlayers().forEach(player -> player.getPlayer().setCanPickupItems(true));
        game.getStartingTimer().setOnTimeChanged(null);

        this.hyrame.getScoreboardManager().getScoreboards(SCOREBOARD_CLASS).forEach(HyriScoreboard::hide);
    }

    @HyriEventHandler
    public void onJoin(HyriGameJoinEvent event) {
        final Player player = event.getGamePlayer().getPlayer();
        final UUID playerId = player.getUniqueId();
        final HyriGame<?> game = this.getGame();
        final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);
        final IHyriServer server = HyriAPI.get().getServer();

        PlayerUtil.resetPlayer(player, true);

        player.setCanPickupItems(false);
        player.setGameMode(GameMode.ADVENTURE);

        if (this.teamSelector) {
            HyriGameItems.TEAM_SELECTOR.give(this.hyrame, player, 0);
        }

        HyriGameItems.LEAVE.give(this.hyrame, player, 8);

        // Check if the server is a host to give special items
        if (server.getAccessibility() == HyggServer.Accessibility.HOST) {
            final HostData hostData = server.getHostData();
            final UUID hostOwner = hostData.getOwner();

            if (hostOwner.equals(playerId) || hostData.getSecondaryHosts().contains(playerId)) {
                this.hyrame.getItemManager().giveItem(player, 7, "host_settings");
            }
        }

        new HyriWaitingScoreboard(game, this.plugin, player).show();

        this.updateScoreboards();

        BroadcastUtil.broadcast(target -> HyrameMessage.GAME_JOIN.asString(target)
                .replace("%player%", session.getNameWithRank())
                .replace("%counter_color%", (game.canStart() ? ChatColor.GREEN : ChatColor.RED).toString())
                .replace("%current_players%", String.valueOf(game.getPlayers().size()))
                .replace("%max_players%", String.valueOf(server.getSlots())));

        // Auto-add player in a team
        if (game.isUsingTeams() && game.getTeams().get(0).getTeamSize() > 1) {
            final IHyriParty party = IHyriParty.get(session.getParty());

            if (!session.hasParty() || !party.isLeader(playerId)) {
                return;
            }

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                if (!game.getState().isAccessible()) { // Game might have started
                    return;
                }

                final HyriGamePlayer leader = game.getPlayer(playerId);

                if (leader == null) { // Player might have disconnected
                    return;
                }

                List<HyriGamePlayer> members = new ArrayList<>();

                members.add(leader);

                // Check if there are other party members in this game
                for (HyriGamePlayer gamePlayer : game.getPlayers()) {
                    if (gamePlayer.equals(leader)) {
                        continue;
                    }

                    if (party.hasMember(gamePlayer.getUniqueId())) {
                        members.add(gamePlayer);
                    }
                }

                if (members.size() == 1) {
                    return;
                }

                // Get the smallest team and try to add the maximum of party members to it
                final HyriGameTeam smallestTeam = game.getSmallestTeam();
                final int space = smallestTeam.getTeamSize() - smallestTeam.getPlayers().size();

                if (space >= 2) { // The team must have a gap of 2 players
                    members = members.subList(0, Math.min(members.size(), space));

                    for (HyriGamePlayer gamePlayer : game.getPlayers()) {
                        smallestTeam.addPlayer(gamePlayer);

                        this.sendAutomaticAddedMessage(smallestTeam, gamePlayer.getPlayer(), members);
                    }

                    TeamChooserGUI.refresh();
                }
            }, 20L);
        }
    }

    private void sendAutomaticAddedMessage(HyriGameTeam team, Player player, List<HyriGamePlayer> members) {
        final StringBuilder players = new StringBuilder();

        for (int i = 0; i < members.size(); i++) {
            final HyriGamePlayer member = members.get(i);

            if (member.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            players.append(ChatColor.AQUA)
                    .append(member.getPlayer().getName());

            if (i != members.size() - 1) {
                players.append(ChatColor.GRAY)
                        .append(", ");
            }
        }

        player.sendMessage(HyrameMessage.TEAM_AUTOMATICALLY_ADDED_MESSAGE.asString(player)
                        .replace("%team%", team.getFormattedDisplayName(player))
                        .replace("%players%", players.toString()));
    }

    @HyriEventHandler
    public void onLeave(HyriGameLeaveEvent event) {
        final Player player = event.getGamePlayer().getPlayer();
        final HyriGame<?> game = this.getGame();
        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());
        final IHyriServer server = HyriAPI.get().getServer();

        this.updateScoreboards();

        BroadcastUtil.broadcast(target -> HyrameMessage.GAME_LEFT.asString(target)
                .replace("%player%", session == null ? event.getGamePlayer().asHyriPlayer().getNameWithRank() : session.getNameWithRank())
                .replace("%counter_color%", (game.canStart() ? ChatColor.GREEN : ChatColor.RED).toString())
                .replace("%current_players%", String.valueOf(game.getPlayers().size()))
                .replace("%max_players%", String.valueOf(server.getSlots())));
    }

    private void updateScoreboards() {
        this.hyrame.getScoreboardManager().getScoreboards(SCOREBOARD_CLASS).forEach(HyriWaitingScoreboard::update);
    }

    private void runActionOnScoreboard(Player player, Consumer<HyriWaitingScoreboard> action) {
        final IScoreboardManager scoreboardManager = this.hyrame.getScoreboardManager();

        if (scoreboardManager.isPlayerScoreboard(player, SCOREBOARD_CLASS)) {
            action.accept((HyriWaitingScoreboard) scoreboardManager.getPlayerScoreboard(player));
        }
    }

}
