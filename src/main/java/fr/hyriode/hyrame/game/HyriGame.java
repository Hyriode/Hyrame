package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.scoreboard.HyriGameWaitingScoreboard;
import fr.hyriode.hyrame.game.tab.HyriGameTabListManager;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.utils.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyrame.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:16
 */
public abstract class HyriGame<P extends HyriGamePlayer> {

    /** Default game type */
    public static final HyriGameType DEFAULT_TYPE = () -> "default";

    /** Redis constants */
    private static final String CURRENT_GAME_KEY = "currentGame:";
    private static final String LAST_GAME_KEY = "lastGame:";

    /** Minimum of players to start */
    protected int minPlayers;
    /** Maximum of players */
    protected int maxPlayers;
    /** Is tab list used */
    protected boolean tabListUsed;

    /** Tab list manager object */
    protected HyriGameTabListManager tabListManager;
    /** All players scoreboard */
    private final List<HyriGameWaitingScoreboard> waitingScoreboards;

    /** Starting timer task */
    protected BukkitTask startingTimer;
    /** Is default starting */
    protected boolean defaultStarting = true;

    /** All game teams */
    protected final List<HyriGameTeam> teams;
    /** All game players */
    protected final List<P> players;

    /** Game state */
    protected HyriGameState state;
    /** Game name */
    protected final String name;
    /** Game display name */
    protected final String displayName;
    /** Game player class */
    private final Class<P> playerClass;
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
     * @param name Game name (ex: bedwars)
     * @param displayName Game display name (ex: BedWars)
     * @param playerClass Game player class
     * @param type Game type (for example 1v1, 2v2 etc.)
     * @param tabListUsed Is tab list used
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, String displayName, Class<P> playerClass, HyriGameType type, boolean tabListUsed) {
        this.hyrame = hyrame;
        this.plugin = plugin;
        this.name = name;
        this.displayName = displayName;
        this.playerClass = playerClass;
        this.type = type;
        this.tabListUsed = tabListUsed;
        this.state = HyriGameState.WAITING;
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.waitingScoreboards = new ArrayList<>();
        this.minPlayers = 4;
        this.maxPlayers = this.minPlayers;

        if (this.tabListUsed) {
            this.tabListManager = new HyriGameTabListManager(this);
        }
    }

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param name Game name (ex: bedwars)
     * @param displayName Game display name (ex: BedWars)
     * @param playerClass Game player class
     * @param tabListUsed Is tab list used
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, String displayName, Class<P> playerClass, boolean tabListUsed) {
        this(hyrame, plugin, name, displayName, playerClass, DEFAULT_TYPE, tabListUsed);
    }

    /**
     * Constructor of {@link HyriGame}
     *
     * @param hyrame Hyrame instance
     * @param plugin Game plugin instance
     * @param name Game name (ex: bedwars)
     * @param displayName Game display name (ex: BedWars)
     * @param playerClass Game player class
     */
    public HyriGame(IHyrame hyrame, JavaPlugin plugin, String name, String displayName, Class<P> playerClass) {
        this(hyrame, plugin, name, displayName, playerClass, true);
    }

    /**
     * Update tab list if its used
     */
    public void updateTabList() {
        if (this.tabListUsed) {
            this.tabListManager.updateTabList();
        }
    }

    /**
     * Start the game
     */
    public void start() {
        if (this.defaultStarting) {
            this.startingTimer.cancel();

            this.waitingScoreboards.forEach(Scoreboard::hide);
        }

        this.setRandomTeams();

        this.state = HyriGameState.PLAYING;

        this.players.forEach(player -> {
            final Player p = player.getPlayer().getPlayer();

            p.setLevel(0);
            p.setFoodLevel(20);
            p.setExp(0.0F);
        });
    }

    /**
     * Set a random team to all players who have not a team
     */
    public void setRandomTeams() {
        final Random random = new Random();

        List<HyriGameTeam> teams;
        for (P player : this.players) {
            if (!player.hasTeam()) {
                teams = this.teams.stream().filter(team -> !team.isFull()).collect(Collectors.toList());

                final int randomResult = random.nextInt(teams.size());
                final HyriGameTeam team = teams.get(randomResult);

                team.addPlayer(player);

                player.setTeam(team);

                this.updateTabList();
            }
        }
    }

    /**
     * Game post registration. Used after calling {@link IHyriGameManager#registerGame}
     */
    public void postRegistration() {
        if (this.defaultStarting) {
            this.startingTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, new HyriGameStartingTimer(this), 20L, 20L);
        }
    }

    /**
     * Register a game team
     *
     * @param team Team to register
     * @return The registered team
     */
    protected HyriGameTeam registerTeam(HyriGameTeam team) {
        if (this.getTeam(team.getName()) == null) {
            this.teams.add(team);

            this.tabListManager.addTeam(team);

            Hyrame.log("'" + team.getName() + "' team registered.");

            return team;
        }
        throw new IllegalStateException("A team with the same name is already registered!");
    }

    /**
     * Called on player login
     * Override this method to make actions on login
     *
     * @param p Logged player
     */
    public void handleLogin(Player p) {
        try {
            if (this.state == HyriGameState.WAITING) {
                final P player = this.playerClass.getConstructor(Player.class).newInstance(p);

                this.players.add(player);

                HyriAPI.get().getRedisProcessor().process(jedis -> jedis.set(CURRENT_GAME_KEY + p.getUniqueId().toString(), this.name));

                if (this.tabListUsed) {
                    this.tabListManager.handleLogin(p);

                    this.updateTabList();
                }

                if (this.defaultStarting) {
                    final HyriGameWaitingScoreboard scoreboard = new HyriGameWaitingScoreboard(this, this.plugin, p);

                    scoreboard.show();

                    this.waitingScoreboards.add(scoreboard);
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
            jedis.set(LAST_GAME_KEY + p.getUniqueId().toString(), this.name);
            jedis.del(CURRENT_GAME_KEY + p.getUniqueId().toString());
        });

        ThreadPool.EXECUTOR.execute(() -> {
            this.players.remove(player);

            if (player.hasTeam()) {
                player.getTeam().removePlayer(player);
            }

            if (this.tabListUsed) {
                this.tabListManager.handleLogout(p);

                this.updateTabList();
            }
        });
    }

    /**
     * To call when the game is over<br>
     * Override this method to make actions on game end
     */
    public void end() {
        this.state = HyriGameState.ENDED;

        this.hyrame.setChatHandler(new HyriDefaultChatHandler());

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (HyriGamePlayer player : this.players) {
                HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUUID());
            }
        }, 20 * 20);

        Bukkit.getScheduler().runTaskLater(this.plugin, Bukkit::shutdown, 20 * 25);
    }

    /**
     * Get a game player object by giving player {@link UUID}
     *
     * @param uuid Player {@link UUID}
     * @return Game player object
     */
    public P getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUUID().equals(uuid)).findFirst().orElse(null);
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
     * Check if two given players are in the same team
     *
     * @param first First player
     * @param second Second player
     * @return The result. <code>true</code> if they are in the same team
     */
    public boolean areInSameTeam(Player first, Player second) {
        return this.getPlayer(first.getUniqueId()).getTeam() == this.getPlayer(second.getUniqueId()).getTeam();
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
        this.state = state;
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
     * Get all waiting scoreboards
     *
     * @return A list of scoreboard
     */
    List<HyriGameWaitingScoreboard> getWaitingScoreboards() {
        return this.waitingScoreboards;
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
     * Check if tab list is used
     *
     * @return <code>true</code> if yes
     */
    public boolean isTabListUsed() {
        return this.tabListUsed;
    }

    /**
     * Set if tab list is used
     *
     * @param tabListUsed <code>true</code> if yes
     */
    public void setTabListUsed(boolean tabListUsed) {
        this.tabListUsed = tabListUsed;
    }

}
