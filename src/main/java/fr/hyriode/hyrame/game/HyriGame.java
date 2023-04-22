package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.event.HyriGameStateChangedEvent;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamRegisteredEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamUnregisteredEvent;
import fr.hyriode.hyrame.game.protocol.*;
import fr.hyriode.hyrame.game.tablist.HyriGameTabListManager;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.timer.HyriGameStartingTimer;
import fr.hyriode.hyrame.game.timer.HyriGameTimer;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.hyrame.tablist.ITabListManager;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.Cast;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:16
 */
public abstract class HyriGame<P extends HyriGamePlayer> implements Cast<HyriGame<?>> {

    private static final String SPECTATORS_TEAM = "zspectators";

    /** Minimum of players to start */
    protected final int minPlayers;

    /** Tab list manager object */
    protected HyriGameTabListManager tabListManager;

    /** The manager of all {@link HyriGameProtocol} */
    protected final HyriGameProtocolManager protocolManager;

    /** Starting timer instance */
    protected HyriGameStartingTimer startingTimer;
    /** Is default starting */
    protected boolean defaultStarting = true;
    /** Is game tab list used */
    protected boolean usingGameTabList = true;
    /** Are teams used */
    protected boolean usingTeams = true;
    /** Are spectators allowed to connect */
    protected boolean spectatorsAllowed = true;

    /** Max reconnection time for a player (if -1, reconnection will not be enabled) */
    protected int reconnectionTime = -1;

    /** The game timer instance */
    protected HyriGameTimer timer;

    /** The waiting room object of the game */
    protected HyriWaitingRoom waitingRoom;

    /** All game teams */
    protected final List<HyriGameTeam> teams;
    /** All game players */
    protected final List<P> players;
    /** The players spectating the server */
    protected final List<HyriGameSpectator> spectators;
    /** Game player class */
    private final Class<P> playerClass;

    /** Game state */
    private HyriGameState state;

    /** Game info */
    protected final IHyriGameInfo info;
    /** Game type */
    protected final HyriGameType type;
    /** The description of the game */
    protected HyriLanguageMessage description;

    /** Hyrame object */
    protected final IHyrame hyrame;
    /** Plugin object */
    protected final JavaPlugin plugin;

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param info Game information
     * @param playerClass Game player class
     * @param type Game type (for example 1v1, 2v2 etc.)
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, IHyriGameInfo info, Class<P> playerClass, HyriGameType type) {
        this.hyrame = hyrame;
        this.plugin = plugin;
        this.info = info;
        this.playerClass = playerClass;
        this.type = type;
        this.setState(HyriGameState.WAITING);
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.protocolManager = new HyriGameProtocolManager(this.plugin, this);
        this.minPlayers = type.getMinPlayers();

        if (this.usingGameTabList) {
            this.tabListManager = new HyriGameTabListManager(this, this.hyrame.getTabListManager());
        }

        HyriAPI.get().getServer().setSlots(type.getMaxPlayers());
    }

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param gameName Game name
     * @param playerClass Game player class
     * @param type Game type (for example 1v1, 2v2 etc.)
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String gameName, Class<P> playerClass, HyriGameType type) {
        this(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo(gameName), playerClass, type);
    }

    /**
     * Game post registration. Used after calling {@link IHyriGameManager#registerGame}
     */
    public void postRegistration() {
        if (this.defaultStarting) {
            this.startingTimer = new HyriGameStartingTimer(this.plugin, this);
            this.startingTimer.runTaskTimerAsynchronously(this.plugin, 20L, 20L);

            this.protocolManager.enableProtocol(new HyriWaitingProtocol(this.hyrame, this.plugin));
        }

        this.protocolManager.enableProtocol(new HyriSpectatorProtocol(this.hyrame));
        this.protocolManager.enableProtocol(new HyriWinProtocol(this.hyrame));

        if (this.waitingRoom != null) {
            this.waitingRoom.setup();
        }

        this.hyrame.getTabListManager().registerTeam(new HyriScoreboardTeam(SPECTATORS_TEAM, SPECTATORS_TEAM, "", "", "", HyriScoreboardTeam.NameTagVisibility.NEVER));
    }

    /**
     * Start the game
     */
    public void start() {
        if (this.defaultStarting) {
            this.protocolManager.disableProtocol(HyriWaitingProtocol.class);
            this.startingTimer.cancel();
        }

        if (this.waitingRoom != null) {
            this.waitingRoom.remove();
        }

        HyriAPI.get().getServer().setState(HyggServer.State.PLAYING);

        this.setState(HyriGameState.PLAYING);

        this.timer = new HyriGameTimer();
        this.timer.runTaskTimer(this.plugin, 20L, 20L);

        this.giveRandomTeams();

        for (HyriGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();
            final IHyriPlayerSession session = gamePlayer.getSession();

            session.setPlaying(true);
            session.update();

            gamePlayer.initConnectionTime();

            PlayerUtil.resetPlayer(player, true);
        }
    }

    /**
     * Called on player login.<br>
     * Override this method to make actions on login
     *
     * @param player Logged player
     */
    public void handleLogin(Player player) {
        try {
            final UUID playerId = player.getUniqueId();

            P gamePlayer = this.getPlayer(playerId);

            // The player already exists -> reconnection
            if (gamePlayer != null && this.isReconnectionAllowed()) {
                HyriAPI.get().getServer().addPlayerPlaying(playerId);

                gamePlayer.setOnline(true);

                if (this.usingGameTabList) {
                    this.tabListManager.handleReconnection(gamePlayer);
                }

                if (this.state == HyriGameState.PLAYING && !gamePlayer.isSpectator()) {
                    final String name = gamePlayer.formatNameWithTeam();
                    final IHyriPlayerSession session = gamePlayer.getSession();

                    session.setPlaying(true);
                    session.update();

                    BroadcastUtil.broadcast(target -> HyrameMessage.GAME_RECONNECTED.asString(target).replace("%player%", name));
                }

                gamePlayer.resetDisconnectionTime();

                HyriAPI.get().getEventBus().publish(new HyriGameReconnectedEvent(this, gamePlayer));
                return;
            }

            // The player doesn't exist and the game is not playing -> normal
            gamePlayer = this.playerClass.getConstructor(Player.class).newInstance(player);

            this.players.add(gamePlayer);

            // Register a fake team owned by the player
            if (!this.usingTeams) {
                final HyriGameTeam team = new HyriGameTeam(player.getName(), null, null, 1) {
                    @Override
                    public String getFormattedDisplayName(Player target) {
                        return this.getPlayer(playerId).formatNameWithTeam();
                    }
                };

                this.registerTeam(team);

                team.addPlayer(gamePlayer);
            }

            if (this.usingGameTabList) {
                this.tabListManager.handleLogin(player);
            }

            // Send the rules of the game
            if (this.description != null) {
                player.spigot().sendMessage(HyriGameMessages.createDescription(this, player));
            }

            HyriAPI.get().getServer().addPlayerPlaying(playerId);

            // Finally, trigger events
            HyriAPI.get().getServerManager().getReconnectionHandler().remove(playerId);
            HyriAPI.get().getEventBus().publish(new HyriGameJoinEvent(this, gamePlayer));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            player.sendMessage(ChatColor.RED + "An error occurred while joining game! Sending you back to lobby...");
            e.printStackTrace();

            HyriAPI.get().getLobbyAPI().sendPlayerToLobby(player.getUniqueId());
        }
    }

    /**
     * Handle the login of a spectator
     *
     * @param player The logged in player
     */
    public void handleSpectatorLogin(Player player) {
        final HyriGameSpectator spectator = new HyriGameSpectator(player);

        this.spectators.add(spectator);
        this.hyrame.getTabListManager().addPlayerInTeam(player, SPECTATORS_TEAM);

        spectator.setSpectator(true);
    }

    /**
     * Handler the logout of a spectator
     *
     * @param player The logged out spectator
     */
    public void handleSpectatorLogout(Player player) {
        final HyriGameSpectator spectator = this.getSpectator(player.getUniqueId());

        // The player is a spectator
        if (spectator != null) {
            this.spectators.remove(spectator);
        }
    }

    /**
     * Called on player logout.<br>
     * Override this method to make actions on logout
     *
     * @param player Logged out player
     */
    public void handleLogout(Player player) {
        final UUID uuid = player.getUniqueId();
        final P gamePlayer = this.getPlayer(uuid);
        final IHyriServer server = HyriAPI.get().getServer();
        final IHyriPlayerSession session = gamePlayer.getSession();

        if (session != null) {
            session.setPlaying(false);
            session.update();
        }

        server.removePlayerPlaying(uuid);
        gamePlayer.setOnline(false);

        if (!this.usingTeams) {
            this.teams.remove(gamePlayer.getTeam());
        }

        if (this.state.isAccessible()) { // State is waiting for more players to start, so the player is not important
            this.players.remove(gamePlayer);
        }

        // Player can reconnect
        if (this.isReconnectionAllowed() && this.state == HyriGameState.PLAYING) {
            HyriAPI.get().getServerManager().getReconnectionHandler().set(uuid, server.getName(), this.reconnectionTime);
        } else if (this.state != HyriGameState.PLAYING && gamePlayer.hasTeam()) { // Game is not playing, so we need to remove him from his team
            gamePlayer.getTeam().removePlayer(gamePlayer);
        }

        if (this.state == HyriGameState.PLAYING) {
            gamePlayer.initDisconnectionTime();
        }

        // Send disconnection message
        if (this.state == HyriGameState.PLAYING && !gamePlayer.isSpectator()) {
            BroadcastUtil.broadcast(target -> HyrameMessage.GAME_DISCONNECTED.asString(target).replace("%player%", gamePlayer.formatNameWithTeam()));
        }

        HyriAPI.get().getEventBus().publish(new HyriGameLeaveEvent(this, gamePlayer));
    }

    /**
     * To call when the game has a winner<br>
     * Override this method to make actions on game win
     *
     * @param winner The team that wins the game
     */
    public void win(HyriGameTeam winner) {
        if (winner == null) {
            return;
        }

        final ITabListManager tabListManager = this.hyrame.getTabListManager();
        final HyriScoreboardTeam team = tabListManager.getTeam(SPECTATORS_TEAM);

        team.setNameTagVisibility(HyriScoreboardTeam.NameTagVisibility.ALWAYS);

        tabListManager.updateTeam(team.getName());

        this.end();

        HyriAPI.get().getEventBus().publish(new HyriGameWinEvent(this, winner));
    }

    /**
     * To call when the game is over.<br>
     * Override this method to make actions on game end
     */
    public void end() {
        this.setState(HyriGameState.ENDED);

        this.timer.cancel();

        if (HyriAPI.get().getServer().getAccessibility() != HyggServer.Accessibility.HOST) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (HyriGamePlayer gamePlayer : this.players) {
                    if (!gamePlayer.isOnline()) {
                        continue;
                    }

                    final boolean autoQueue = gamePlayer.asHyriPlayer().getSettings().isAutoQueueEnabled();

                    if (autoQueue) {
                        HyriAPI.get().getQueueManager().addPlayerInQueue(gamePlayer.getUniqueId(), this.info.getName(), this.type.getName(), null);
                    }
                }
            }, 20 * 15);
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                HyriAPI.get().getLobbyAPI().sendPlayerToLobby(player.getUniqueId());
            }
        }, 20 * 30);

        HyriAPI.get().getServer().setState(HyggServer.State.SHUTDOWN);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> HyriAPI.get().getServerManager().removeServer(HyriAPI.get().getServer().getName(), () -> HyrameLogger.log("Hyggdrasil is stopping the server...")), 20 * 40);
    }

    /**
     * Register a game team
     *
     * @param team Team to register
     * @return The registered team
     */
    public HyriGameTeam registerTeam(HyriGameTeam team) {
        if (this.getTeam(team.getName()) == null) {
            this.teams.add(team);

            if (this.usingGameTabList) {
                this.tabListManager.addTeam(team);
            }

            HyriAPI.get().getEventBus().publish(new HyriGameTeamRegisteredEvent(this, team));

            return team;
        }
        throw new IllegalStateException("A team with the same name is already registered!");
    }

    /**
     * Unregister a game team
     *
     * @param team The team to unregister
     */
    public void unregisterTeam(HyriGameTeam team) {
        if (this.getTeam(team.getName()) != null) {
            this.teams.remove(team);

            if (this.usingGameTabList) {
                this.tabListManager.removeTeam(team);
            }

            HyriAPI.get().getEventBus().publish(new HyriGameTeamUnregisteredEvent(this, team));
            return;
        }
        throw new IllegalStateException("The provided team is not registered! Team name: " + team.getName());
    }

    /**
     * Give a random team to all players who have not a team
     */
    public void giveRandomTeams() {
        final List<P> withoutTeam = this.players.stream().filter(player -> !player.hasTeam()).collect(Collectors.toList());

        Collections.shuffle(withoutTeam);

        for (P player : withoutTeam) {
            final HyriGameTeam team = this.getSmallestTeam();

            if (team != null) {
                team.addPlayer(player);

                this.tabListManager.updatePlayer(player);
            } else {
                HyriAPI.get().getPlayerManager().sendMessage(player.getUniqueId(), ChatColor.RED + "An error occurred while giving your team! Sending you back to lobby...");
                HyriAPI.get().getLobbyAPI().sendPlayerToLobby(player.getUniqueId());
            }
        }
    }

    /**
     * Update tab list if its used
     */
    public void updateTabList() {
        if (this.usingGameTabList) {
            this.tabListManager.updateTabList();
        }
    }

    /**
     * Send a message to all spectators
     *
     * @param message Message to send
     */
    public void sendMessageToSpectators(Function<Player, String> message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final UUID playerId = player.getUniqueId();
            final HyriGamePlayer gamePlayer = this.getPlayer(playerId);
            final HyriGameSpectator spectator = this.getSpectator(playerId);

            // Check if the player is playing the game
            if (gamePlayer != null && !gamePlayer.isSpectator()) {
                continue;
            }

            if (gamePlayer == null || spectator != null || gamePlayer.isSpectator()) {
                player.sendMessage(HyrameMessage.GAME_SPECTATORS_CHAT.asString(player).replace("%message%", message.apply(player)));
            }
        }
    }

    /**
     * Send a message to all spectators
     *
     * @param message Message to send
     */
    public void sendMessageToSpectators(HyriLanguageMessage message) {
        this.sendMessageToSpectators(message::getValue);
    }

    /**
     * Get the plugin instance of the game
     *
     * @return A {@link JavaPlugin} instance
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Get a game player object by giving player's {@link UUID}
     *
     * @param uuid Player {@link UUID}
     * @return Game player object
     */
    public P getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Get a game player object
     *
     * @param player The {@link Player}
     * @return Game player object
     */
    public P getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    /**
     * Get all players in spectator mode.<br>
     * It's not taking in account players that were playing the game!
     *
     * @return A list of {@link HyriGameSpectator}
     */
    public List<HyriGameSpectator> getSpectators() {
        return this.spectators;
    }

    /**
     * Get a spectator object
     *
     * @param uniqueId The unique id of the spectator to get
     * @return A {@link HyriGameSpectator}; or <code>null</code> if nothing was found
     */
    public HyriGameSpectator getSpectator(UUID uniqueId) {
        for (HyriGameSpectator spectator : this.spectators) {
            if (spectator.getUniqueId().equals(uniqueId)) {
                return spectator;
            }
        }
        return null;
    }

    /**
     * Get all dead players
     *
     * @return A list of game player
     */
    public List<P> getDeadPlayers() {
        return this.players.stream().filter(gamePlayer -> gamePlayer.isDead() || gamePlayer.isSpectator()).collect(Collectors.toList());
    }

    /**
     * Get a team by its name
     *
     * @param name Team name
     * @return {@link HyriGameTeam} instance
     */
    public HyriGameTeam getTeam(String name) {
        for (HyriGameTeam team : this.teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Get the team of a player
     *
     * @param player The player
     * @return A {@link HyriGameTeam}
     */
   public HyriGameTeam getPlayerTeam(Player player) {
        return this.getPlayer(player.getUniqueId()).getTeam();
   }

    /**
     * Get the team with the fewest players
     *
     * @return A {@link HyriGameTeam} or <code>null</code> if every team is full
     */
    public HyriGameTeam getSmallestTeam() {
        HyriGameTeam result = null;
        for (HyriGameTeam team : this.teams) {
            if (!team.isFull()) {
                if (result == null) {
                    result = team;
                } else if (team.getPlayers().size() < result.getPlayers().size()) {
                    result = team;
                }
            }
        }
        return result;
    }

    /**
     * Check if two given players are in the same team
     *
     * @param first First player
     * @param second Second player
     * @return The result. <code>true</code> if they are in the same team
     */
    public boolean areInSameTeam(Player first, Player second) {
        final HyriGamePlayer firstPlayer = this.getPlayer(first.getUniqueId());
        final HyriGamePlayer secondPlayer = this.getPlayer(second.getUniqueId());

        if (firstPlayer != null && secondPlayer != null) {
            return firstPlayer.getTeam() == secondPlayer.getTeam();
        }
        return false;
    }

    /**
     * Get game name
     *
     * @return Game name
     */
    public String getName() {
        return this.info.getName();
    }

    /**
     * Get game display name
     *
     * @return Game display name
     */
    public String getDisplayName() {
        return this.info.getDisplayName();
    }

    /**
     * Get game type (for example 1v1, 2v2 etc.)
     *
     * @return Game type
     */
    public HyriGameType getType() {
        return this.type;
    }

    /**
     * Get the description of the game
     *
     * @return A {@link HyriLanguageMessage}
     */
    public HyriLanguageMessage getDescription() {
        return this.description;
    }

    /**
     * Get game state
     *
     * @return Game state
     */
    public HyriGameState getState() {
        return this.state;
    }

    /**
     * Set game state
     *
     * @param state New game state
     */
    public void setState(HyriGameState state) {
        final HyriGameState oldState = this.state;

        HyriAPI.get().getEventBus().publishAsync(new HyriGameStateChangedEvent(this, oldState, this.state = state));
    }

    /**
     * Get all game players
     *
     * @return A list of game players
     */
    public List<P> getPlayers() {
        return this.players;
    }

    /**
     * Get all the online game players
     *
     * @return A list of game players
     */
    public List<P> getOnlinePlayers() {
        return this.players.stream().filter(HyriGamePlayer::isOnline).collect(Collectors.toList());
    }

    /**
     * Get all game teams
     *
     * @return A list of game teams
     */
    public List<HyriGameTeam> getTeams() {
        return this.teams;
    }

    /**
     * Get the game waiting room object
     *
     * @return The game's {@linkplain HyriWaitingRoom waiting room}
     */
    public HyriWaitingRoom getWaitingRoom() {
        return this.waitingRoom;
    }

    /**
     * Get the game timer instance
     *
     * @return The {@link HyriGameTimer} instance
     */
    public HyriGameTimer getTimer() {
        return this.timer;
    }

    /**
     * Check if game is using default starting
     *
     * @return <code>true</code> if yes
     */
    public boolean isDefaultStarting() {
        return this.defaultStarting;
    }

    /**
     * Set if game is using default starting
     *
     * @param defaultStarting <code>true</code> if yes
     */
    public void setDefaultStarting(boolean defaultStarting) {
        this.defaultStarting = defaultStarting;
    }

    /**
     * Get the game tab list manager instance
     *
     * @return The {@link HyriGameTabListManager} instance
     */
    public HyriGameTabListManager getTabListManager() {
        return this.tabListManager;
    }

    /**
     * Check if the game is using the game tab list system (with teams)
     *
     * @return <code>true</code> if yes
     */
    public boolean isUsingGameTabList() {
        return this.usingGameTabList;
    }

    /**
     * Check if teams system is used or not
     *
     * @return <code>true</code> if teams are used
     */
    public boolean isUsingTeams() {
        return this.usingTeams;
    }

    /**
     * Check whether spectators are allowed on the game
     *
     * @return <code>true</code> if they are allowed
     */
    public boolean isSpectatorsAllowed() {
        return this.spectatorsAllowed;
    }

    /**
     * Set whether spectators are allowed on the game
     *
     * @param spectatorsAllowed <code>true</code> if yes
     */
    public void setSpectatorsAllowed(boolean spectatorsAllowed) {
        this.spectatorsAllowed = spectatorsAllowed;
    }

    /**
     * Get the reconnection time for a player
     *
     * @return The game reconnection time
     */
    public int getReconnectionTime() {
        return this.reconnectionTime;
    }

    /**
     * Check if reconnection is enabled for this game
     *
     * @return <code>true</code> if reconnection is enabled
     */
    public boolean isReconnectionAllowed() {
        return this.reconnectionTime > 0;
    }

    /**
     * Get the starting timer instance.
     * Warning: It might be null if the game is no longer in {@link HyriGameState#WAITING} or {@link HyriGameState#READY}
     *
     * @return The {@link HyriGameStartingTimer} instance
     */
    public HyriGameStartingTimer getStartingTimer() {
        return this.startingTimer;
    }

    /**
     * Get the {@link HyriGameProtocol} manager instance
     *
     * @return The {@link HyriGameProtocolManager} instance
     */
    public HyriGameProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    /**
     * Get minimum of game players
     *
     * @return Minimum of game players
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Check if the game can start
     *
     * @return <code>true</code> if yes
     */
    public boolean canStart() {
        final int slots = HyriAPI.get().getServer().getSlots();

        return slots < this.minPlayers ? this.players.size() >= slots : this.players.size() >= this.minPlayers;
    }

    /**
     * Check if the game is full
     *
     * @return <code>true</code> if yes
     */
    public boolean isFull() {
        return this.players.size() >= HyriAPI.get().getServer().getSlots();
    }

}
