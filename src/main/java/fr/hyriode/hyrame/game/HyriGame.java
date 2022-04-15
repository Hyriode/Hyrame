package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.game.event.HyriGameStateChangedEvent;
import fr.hyriode.hyrame.game.event.HyriGameWinEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamRegisteredEvent;
import fr.hyriode.hyrame.game.event.team.HyriGameTeamUnregisteredEvent;
import fr.hyriode.hyrame.game.protocol.HyriGameProtocolManager;
import fr.hyriode.hyrame.game.protocol.HyriSpectatorProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWaitingProtocol;
import fr.hyriode.hyrame.game.protocol.HyriWinProtocol;
import fr.hyriode.hyrame.game.tab.HyriGameTabListManager;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.timer.HyriGameStartingTimer;
import fr.hyriode.hyrame.game.timer.HyriGameTimer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
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

    /** Default game type */
    public static final HyriGameType DEFAULT_TYPE = () -> "default";

    /** Redis constants */
    private static final String CURRENT_GAME_KEY = "currentGame:";
    private static final String LAST_GAME_KEY = "lastGame:";

    /** Minimum of players to start */
    protected int minPlayers;
    /** Maximum of players */
    protected int maxPlayers;

    /** Tab list manager object */
    protected final HyriGameTabListManager tabListManager;

    /** The manager of all {@link fr.hyriode.hyrame.game.protocol.HyriGameProtocol} */
    protected final HyriGameProtocolManager protocolManager;

    /** Starting timer instance */
    protected HyriGameStartingTimer startingTimer;
    /** Starting timer task */
    protected BukkitTask startingTimerTask;
    /** Is default starting */
    protected boolean defaultStarting = true;

    /** Game timer task */
    protected BukkitTask timerTask;
    /** The game timer instance */
    protected HyriGameTimer timer;

    /** All game teams */
    protected final List<HyriGameTeam> teams;
    /** All game players */
    protected final List<P> players;
    /** Game player class */
    private final Class<P> playerClass;

    /** Game state */
    private HyriGameState state;

    /** Game name */
    protected final String name;
    /** Game display name */
    protected final String displayName;
    /** Game type */
    protected final HyriGameType type;

    /** Hyrame object */
    protected final IHyrame hyrame;
    /** Plugin object */
    protected final JavaPlugin plugin;

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param name Game name
     * @param displayName Game display name
     * @param playerClass Game player class
     * @param type Game type (for example 1v1, 2v2 etc.)
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, String displayName, Class<P> playerClass, HyriGameType type) {
        this.hyrame = hyrame;
        this.plugin = plugin;
        this.name = name;
        this.displayName = displayName;
        this.playerClass = playerClass;
        this.type = type;
        this.setState(HyriGameState.WAITING);
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.protocolManager = new HyriGameProtocolManager(this.plugin, this);
        this.minPlayers = 4;
        this.maxPlayers = this.minPlayers;
        this.tabListManager = new HyriGameTabListManager(this);
    }

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param name Game name
     * @param displayName Game display name
     * @param playerClass Game player class
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, String displayName, Class<P> playerClass) {
        this(hyrame, plugin, name, displayName, playerClass, DEFAULT_TYPE);
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
        this.protocolManager.enableProtocol(new HyriSpectatorProtocol(this.hyrame, this.plugin));
        this.protocolManager.enableProtocol(new HyriWinProtocol(this.hyrame, this));
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

        this.timer = new HyriGameTimer();
        this.timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this.timer, 20L, 20L);

        this.giveRandomTeams();

        this.setState(HyriGameState.PLAYING);

        this.players.forEach(player -> PlayerUtil.resetPlayer(player.getPlayer(), true));
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

                    HyriAPI.get().getRedisProcessor().process(jedis -> jedis.set(CURRENT_GAME_KEY + p.getUniqueId().toString(), this.getName()));

                    this.tabListManager.handleLogin(p);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            p.sendMessage(ChatColor.RED + "An error occurred while joining game! Sending you back to lobby...");
            HyriAPI.get().getServerManager().sendPlayerToLobby(p.getUniqueId());
            e.printStackTrace();
        }
    }

    /**
     * Called on player logout<br>
     * Override this method to make actions on logout
     *
     * @param p Logged out player
     */
    public void handleLogout(Player p) {
        final P player = this.getPlayer(p.getUniqueId());

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            jedis.set(LAST_GAME_KEY + p.getUniqueId().toString(), this.getName());
            jedis.del(CURRENT_GAME_KEY + p.getUniqueId().toString());
        });

        this.players.remove(player);

        if (player.hasTeam()) {
            player.getTeam().removePlayer(player);
        }

        this.tabListManager.handleLogout(p);
    }

    /**
     * To call when the game has a winner<br>
     * Override this method to make actions on game win
     */
    public void win(HyriGameTeam winner) {
        if (winner == null) {
            return;
        }

        HyriAPI.get().getEventBus().publish(new HyriGameWinEvent(this, winner));

        this.end();
    }

    /**
     * To call when the game is over<br>
     * Override this method to make actions on game end
     */
    public void end() {
        this.setState(HyriGameState.ENDED);

        this.timerTask.cancel();

        this.hyrame.setChatHandler(new HyriDefaultChatHandler());

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (HyriGamePlayer player : this.players) {
                HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUUID());
            }
        }, 20 * 30);

        Bukkit.getScheduler().runTaskLater(this.plugin, Bukkit::shutdown, 20 * 40);
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

            this.tabListManager.addTeam(team);

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

            this.tabListManager.removeTeam(team);

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

                this.updateTabList();
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
        this.tabListManager.updateTabList();
    }

    /**
     * Send a message to all players
     *
     * @param message A simple function used to change the message in terms of a target
     * @param condition The condition to send the message
     */
    public void sendMessageToAll(Function<Player, String> message, Predicate<HyriGamePlayer> condition) {
        for (HyriGamePlayer gamePlayer : this.players) {
            final Player player = gamePlayer.getPlayer();

            if (condition == null || condition.test(gamePlayer)) {
                player.sendMessage(message.apply(player));
            }
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
        for (HyriGamePlayer player : this.getSpectators()) {
            final Player target = player.getPlayer();

            target.sendMessage(ChatColor.GRAY + (withPrefix ? SPECTATORS_CHAT_PREFIX.getForSender(target) : "") + message.apply(target));
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
        return this.name;
    }

    /**
     * Get game display name
     *
     * @return Game display name
     */
    public String getDisplayName() {
        return this.displayName;
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
     * Get all game teams
     *
     * @return A list of game teams
     */
    public List<HyriGameTeam> getTeams() {
        return this.teams;
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
     * Set maximum of game players
     *
     * @param maxPlayers New maximum of game players
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
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
     * Set minimum of game players
     *
     * @param minPlayers New minimum of game players
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
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
