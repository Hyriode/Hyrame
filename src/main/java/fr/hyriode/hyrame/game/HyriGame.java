package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.network.HyriNetworkCount;
import fr.hyriode.api.network.HyriPlayerCount;
import fr.hyriode.api.network.IHyriNetwork;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.event.HyriGameStateChangedEvent;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamRegisteredEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamUnregisteredEvent;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocolManager;
import fr.hyriode.hyrame.game.protocol.HyriSpectatorProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWinProtocol;
import fr.hyriode.hyrame.game.tablist.HyriGameTabListManager;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.timer.HyriGameStartingTimer;
import fr.hyriode.hyrame.game.timer.HyriGameTimer;
import fr.hyriode.hyrame.game.util.HyriGameMessages;
import fr.hyriode.hyrame.game.waitingroom.HyriWaitingRoom;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.BroadcastUtil;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:16
 */
public abstract class HyriGame<P extends HyriGamePlayer> {

    /** The prefix to show when a message is sent to spectators */
    private static final HyriLanguageMessage SPECTATORS_CHAT_PREFIX = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "[Spectators] ")
            .addValue(HyriLanguage.FR, "[Spectateurs] ");

    /** Minimum of players to start */
    protected final int minPlayers;
    /** Maximum of players */
    protected final int maxPlayers;

    /** Tab list manager object */
    protected HyriGameTabListManager tabListManager;

    /** The manager of all {@link fr.hyriode.hyrame.game.protocol.HyriGameProtocol} */
    protected final HyriGameProtocolManager protocolManager;

    /** Starting timer instance */
    protected HyriGameStartingTimer startingTimer;
    /** Starting timer task */
    protected BukkitTask startingTimerTask;
    /** Is default starting */
    protected boolean defaultStarting = true;
    /** Is game tab list used */
    protected boolean usingGameTabList = true;

    /** Max reconnection time for a player (if -1, reconnection will not be enabled) */
    protected int reconnectionTime;

    /** Game timer task */
    protected BukkitTask timerTask;
    /** The game timer instance */
    protected HyriGameTimer timer;

    /** The waiting room object of the game */
    protected HyriWaitingRoom waitingRoom;

    /** All game teams */
    protected final List<HyriGameTeam> teams;
    /** All game players */
    protected final List<P> players;
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
        this.teams = new ArrayList<>();
        this.protocolManager = new HyriGameProtocolManager(this.plugin, this);
        this.minPlayers = type.getMinPlayers();
        this.maxPlayers = type.getMaxPlayers();

        if (this.usingGameTabList) {
            this.tabListManager = new HyriGameTabListManager(this, this.hyrame.getTabListManager());
        }

        HyriAPI.get().getServer().setSlots(this.maxPlayers);
    }

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param name Game name
     * @param playerClass Game player class
     * @param type Game type (for example 1v1, 2v2 etc.)
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, Class<P> playerClass, HyriGameType type) {
        this(hyrame, plugin, HyriAPI.get().getGameManager().getGameInfo(name), playerClass, type);
    }

    /**
     * Game post registration. Used after calling {@link IHyriGameManager#registerGame}
     */
    public void postRegistration() {
        if (this.defaultStarting) {
            this.startingTimer = new HyriGameStartingTimer(this.plugin, this);
            this.startingTimerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this.startingTimer, 20L, 20L);

            this.protocolManager.enableProtocol(new HyriWaitingProtocol(this.hyrame, this.plugin));
        }
        this.protocolManager.enableProtocol(new HyriSpectatorProtocol(this.hyrame));
        this.protocolManager.enableProtocol(new HyriWinProtocol(this.hyrame, this));

        if (this.waitingRoom != null) {
            this.waitingRoom.setup();
        }
    }

    /**
     * Start the game
     */
    public void start() {
        if (this.defaultStarting) {
            this.protocolManager.disableProtocol(HyriWaitingProtocol.class);

            this.startingTimerTask.cancel();
            this.startingTimer = null;
        }

        if (this.waitingRoom != null) {
            this.waitingRoom.remove();
        }

        HyriAPI.get().getServer().setState(IHyriServer.State.PLAYING);

        this.setState(HyriGameState.PLAYING);

        this.timer = new HyriGameTimer();
        this.timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this.timer, 20L, 20L);

        this.giveRandomTeams();

        for (HyriGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();

            gamePlayer.initConnectionTime();

            if (this.description != null) {
                player.spigot().sendMessage(HyriGameMessages.createDescription(this, player));
            }

            PlayerUtil.resetPlayer(player, true);
        }
    }

    /**
     * Called on player login
     * Override this method to make actions on login
     *
     * @param p Logged player
     */
    public void handleLogin(Player p) {
        try {
            if (this.state == HyriGameState.WAITING || this.state == HyriGameState.READY) {
                if (!this.isFull()) {
                    final P player = this.playerClass.getConstructor(HyriGame.class, Player.class).newInstance(this, p);

                    this.players.add(player);

                    HyriAPI.get().getServer().addPlayerPlaying(p.getUniqueId());
                    HyriAPI.get().getServerManager().getReconnectionHandler().remove(p.getUniqueId());

                    this.updatePlayerCount(false);

                    HyriAPI.get().getEventBus().publish(new HyriGameJoinEvent(this, player));

                    if (this.usingGameTabList) {
                        this.tabListManager.handleLogin(p);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            p.sendMessage(ChatColor.RED + "An error occurred while joining game! Sending you back to lobby...");
            HyriAPI.get().getServerManager().sendPlayerToLobby(p.getUniqueId());
            e.printStackTrace();
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

        server.removePlayerPlaying(uuid);

        gamePlayer.setOnline(false);

        if (!this.isReconnectionAllowed() || this.state != HyriGameState.PLAYING) {
            this.players.remove(gamePlayer);

            if (gamePlayer.hasTeam()) {
                gamePlayer.getTeam().removePlayer(gamePlayer);
            }
        } else {
            HyriAPI.get().getServerManager().getReconnectionHandler().set(uuid, server.getName(), this.reconnectionTime);
        }

        if (this.state == HyriGameState.PLAYING) {
            if (!gamePlayer.isSpectator()) {
                BroadcastUtil.broadcast(target ->  hyrame.getLanguageManager().getValue(target, this.isReconnectionAllowed() ? "message.game.disconnected" : "message.game.left").replace("%player%", gamePlayer.formatNameWithTeam()));
            }
        }

        this.updatePlayerCount(true);

        HyriAPI.get().getEventBus().publish(new HyriGameLeaveEvent(this, gamePlayer));
    }

    /**
     * Called when a player reconnects on the game.<br>
     * Override this method to make actions on reconnection
     *
     * @param player The player that reconnected
     */
    public void handleReconnection(Player player) {
        final P gamePlayer = this.getPlayer(player);

        HyriAPI.get().getServer().addPlayerPlaying(player.getUniqueId());

        gamePlayer.setOnline(true);

        this.updatePlayerCount(false);

        if (this.usingGameTabList) {
            this.tabListManager.handleReconnection(gamePlayer);
        }

        HyriAPI.get().getEventBus().publish(new HyriGameReconnectedEvent(this, gamePlayer));

        if (this.state == HyriGameState.PLAYING) {
            if (!gamePlayer.isSpectator()) {
                BroadcastUtil.broadcast(target -> hyrame.getLanguageManager().getValue(target, "message.game.reconnected").replace("%player%", gamePlayer.formatNameWithTeam()));
            }
        }
    }

    /**
     * Update the player counter
     *
     * @param remove Is the method when removing
     */
    private void updatePlayerCount(boolean remove) {
        final IHyriNetwork network = HyriAPI.get().getNetworkManager().getNetwork();
        final HyriNetworkCount networkCount = network.getPlayerCount();
        final HyriPlayerCount count = networkCount.getCategory(this.getName());
        final int currentPlayers = count.getType(this.type.getName());

        if (currentPlayers <= 0 && remove) {
            return;
        }

        count.setType(this.type.getName(), currentPlayers + (remove ? -1 : 1));
        network.update();
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

        HyriAPI.get().getEventBus().publish(new HyriGameWinEvent(this, winner));

        this.end();
    }

    /**
     * To call when the game is over.<br>
     * Override this method to make actions on game end
     */
    public void end() {
        this.setState(HyriGameState.ENDED);

        this.timerTask.cancel();

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (HyriGamePlayer gamePlayer : this.players) {
                final UUID playerId = gamePlayer.getUUID();
                final IHyriPlayer account = gamePlayer.asHyriPlayer();
                final boolean autoQueue = account.getSettings().isAutoQueueEnabled();

                if (autoQueue) {
                    HyriAPI.get().getQueueManager().addPlayerInQueueWithPartyCheck(playerId, this.info.getName(), this.type.getName());
                }
            }
        }, 10 * 30);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUniqueId());
            }
        }, 20 * 30);

        HyriAPI.get().getServer().setState(IHyriServer.State.SHUTDOWN);

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
            final HyriGameTeam team = this.getTeamWithFewestPlayers();

            if (team != null) {
                team.addPlayer(player);

                player.setTeam(team);

                this.tabListManager.updatePlayer(player);
            } else {
                HyriAPI.get().getPlayerManager().sendMessage(player.getUUID(), ChatColor.RED + "An error occurred while giving your team! Sending you back to lobby...");
                HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUUID());
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
     * Send a message to all players
     *
     * @param message A simple function used to change the message in terms of a target
     * @param condition The condition to send the message
     */
    public void sendMessageToAll(Function<Player, String> message, Predicate<HyriGamePlayer> condition) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final HyriGamePlayer gamePlayer = this.getPlayer(player);

            if (gamePlayer != null) {
                if (condition == null || condition.test(gamePlayer)) {
                    player.sendMessage(message.apply(player));
                }
                continue;
            }

            player.sendMessage(message.apply(player));
        }
    }

    /**
     * Send a message to all players
     *
     * @param message A simple function used to change the message in terms of a target
     */
    public void sendMessageToAll(Function<Player, String> message) {
        this.sendMessageToAll(message, null);
    }

    /**
     * Send a message to all players
     *
     * @param message The {@link HyriLanguageMessage} to send to players
     * @param condition The condition to send the message
     */
    public void sendMessageToAll(HyriLanguageMessage message, Predicate<HyriGamePlayer> condition) {
        this.sendMessageToAll(message.toFunction(), condition);
    }

    /**
     * Send a message to all players
     *
     * @param message The {@link HyriLanguageMessage} to send to players
     */
    public void sendMessageToAll(HyriLanguageMessage message) {
        this.sendMessageToAll(message, null);
    }

    /**
     * Send a message to all spectators
     *
     * @param message Message to send
     * @param withPrefix If <code>true</code>, a prefix will be added before the message
     */
    public void sendMessageToSpectators(Function<Player, String> message, boolean withPrefix) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final HyriGamePlayer gamePlayer = this.getPlayer(player.getUniqueId());

            if (gamePlayer != null && !gamePlayer.isSpectator()) {
                return;
            }

            final String formattedMessage = ChatColor.GRAY + (withPrefix ? SPECTATORS_CHAT_PREFIX.getForSender(player) : "") + message.apply(player);

            if (gamePlayer == null) {
                player.sendMessage(formattedMessage);
            } else if (gamePlayer.isSpectator()) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    /**
     * Send a message to all spectators
     *
     * @param message Message to send
     * @param withPrefix If <code>true</code>, a prefix will be added before the message
     */
    public void sendMessageToSpectators(HyriLanguageMessage message, boolean withPrefix) {
        this.sendMessageToSpectators(message::getForPlayer, withPrefix);
    }

    /**
     * Send a title to all the game players
     *
     * @param title The title to send
     * @param condition The condition to send the title to a player
     */
    public void sendTitleToAll(Function<Player, Title> title, Predicate<HyriGamePlayer> condition) {
        for (HyriGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();

            if (condition == null || condition.test(gamePlayer)) {
                Title.sendTitle(player, title.apply(player));
            }
        }
    }

    /**
     * Send a title to all the game players
     *
     * @param title The title to send
     */
    public void sendTitleToAll(Function<Player, Title> title) {
        this.sendTitleToAll(title, null);
    }

    /**
     * Get the Hyrame instance
     *
     * @return The {@link IHyrame} instance
     */
    public IHyrame getHyrame() {
        return this.hyrame;
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
        return this.players.stream().filter(player -> player.getUUID().equals(uuid)).findFirst().orElse(null);
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
     * Get all players in spectator mode
     *
     * @return A list of game player
     */
    public List<P> getSpectators() {
        return this.players.stream().filter(HyriGamePlayer::isSpectator).collect(Collectors.toList());
    }

    /**
     * Check if a given player is a spectator by giving its unique id
     *
     * @param playerUUID The {@link UUID} of the player to check
     * @return <code>true</code> if the player is a spectator
     */
    public boolean isSpectator(UUID playerUUID) {
        final HyriGamePlayer gamePlayer = this.getPlayer(playerUUID);

        return gamePlayer != null && gamePlayer.isSpectator();

    }

    /**
     * Check if a given player is a spectator
     *
     * @param player The {@link Player} to check
     * @return <code>true</code> if the player is a spectator
     */
    public boolean isSpectator(Player player) {
        return this.isSpectator(player.getUniqueId());
    }

    /**
     * Get all dead players
     *
     * @return A list of game player
     */
    public List<P> getDeadPlayers() {
        return this.players.stream().filter(HyriGamePlayer::isDead).collect(Collectors.toList());
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
    public HyriGameTeam getTeamWithFewestPlayers() {
        HyriGameTeam result = null;
        for (HyriGameTeam team : this.teams) {
            if (!team.isFull()) {
                if (result == null) {
                    result = team;
                } else {
                    if (team.getPlayers().size() < result.getPlayers().size()) {
                        result = team;
                    }
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
        final List<P> players = new ArrayList<>();

        for (P player : this.players) {
            if (player.isOnline()) {
                players.add(player);
            }
        }
        return players;
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
     * Get the {@link fr.hyriode.hyrame.game.protocol.HyriGameProtocol} manager instance
     *
     * @return The {@link HyriGameProtocolManager} instance
     */
    public HyriGameProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    /**
     * Get maximum of game players
     *
     * @return Maximum of game players
     */
    public int getMaxPlayers() {
        return this.maxPlayers;
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
        return this.players.size() >= this.minPlayers;
    }

    /**
     * Check if the game is full
     *
     * @return <code>true</code> if yes
     */
    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

}
