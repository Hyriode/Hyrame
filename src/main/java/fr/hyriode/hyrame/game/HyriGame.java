package fr.hyriode.hyrame.game;

import fr.hyriode.common.board.Scoreboard;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.game.scoreboard.HyriGameWaitingScoreboard;
import fr.hyriode.hyrame.game.tab.HyriGameTabListManager;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.util.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:16
 */
public class HyriGame<P extends HyriGamePlayer> {

    private static final String CURRENT_GAME_KEY = "currentGame:";
    private static final String LAST_GAME_KEY = "lastGame:";
    private static final String REJOIN_KEY = "rejoin:";

    protected int minPlayers;
    protected int maxPlayers;

    protected boolean reconnectionAllowed;
    protected long maxReconnectionTime = -1;

    protected boolean tabListUsed = true;
    protected HyriGameTabListManager tabListManager;

    private final List<HyriGameWaitingScoreboard> waitingScoreboards;

    protected BukkitTask startingTimer;
    protected boolean defaultStarting = true;

    protected final List<HyriGameTeam> teams;

    protected final List<P> players;

    protected HyriGameState state;

    private final Class<P> playerClass;

    protected final String displayName;
    protected final String name;

    protected final JavaPlugin plugin;

    public HyriGame(Hyrame hyrame, String name, String displayName, Class<P> playerClass) {
        this.plugin = hyrame.getPlugin();
        this.name = name;
        this.displayName = displayName;
        this.playerClass = playerClass;
        this.state = HyriGameState.WAITING;
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.waitingScoreboards = new ArrayList<>();
    }

    protected void registerTabListManager() {
        this.tabListManager = new HyriGameTabListManager(this);
    }

    public void updateTabList() {
        this.tabListManager.updateTabList();
    }

    public void startGame() {
        if (this.defaultStarting) {
            this.waitingScoreboards.forEach(Scoreboard::hide);

            this.startingTimer.cancel();
        }

        this.state = HyriGameState.PLAYING;

        this.players.forEach(player -> {
            final Player p = player.getPlayer().getPlayer();

            p.setLevel(0);
            p.setExp(0.0F);
        });
    }

    void postRegistration(Hyrame hyrame) {
        if (this.defaultStarting) {
            this.startingTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(hyrame.getPlugin(), new HyriGameStartingTimer(hyrame, this), 20L, 20L);
        }
    }

    protected HyriGameTeam registerTeam(HyriGameTeam team) {
        if (this.getTeam(team.getName()) == null) {
            this.teams.add(team);

            Hyrame.log("'" + team.getName() + "' team registered.");

            return team;
        }
        throw new IllegalStateException("A team with the same name is already registered!");
    }

    public void handleLogin(Player p) {
        try {
            final P player = this.playerClass.getConstructor(Player.class).newInstance(p);

            this.players.add(player);

            ThreadPool.EXECUTOR.execute(() -> {
                final Jedis jedis = HyriAPI.get().getJedisResource();
                final String key = CURRENT_GAME_KEY + p.getUniqueId().toString();

                if (jedis != null) {
                    jedis.set(key, this.name);
                    jedis.close();
                }
            });

            if (this.tabListUsed) {
                this.tabListManager.handleLogin(p);
                this.updateTabList();
            }

            if (this.defaultStarting) {
                final HyriGameWaitingScoreboard scoreboard = new HyriGameWaitingScoreboard(this, this.plugin, p);

                scoreboard.show();

                this.waitingScoreboards.add(scoreboard);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            p.sendMessage(ChatColor.RED + "An error occurred when joining game! Sending you back to lobby...");
            HyriAPI.get().getServerManager().sendPlayerToLobby(p.getUniqueId());
            e.printStackTrace();
        }
    }

    public void handleLogout(Player p) {
        final P player = this.getPlayer(p.getUniqueId());

        ThreadPool.EXECUTOR.execute(() -> {
            final Jedis jedis = HyriAPI.get().getJedisResource();

            if (jedis != null) {
                jedis.set(LAST_GAME_KEY + p.getUniqueId().toString(), this.name);
                jedis.close();
            }

            if (this.reconnectionAllowed && this.maxReconnectionTime > 0 && this.state == HyriGameState.PLAYING) {
                if (jedis != null) {
                    // jedis.set(REJOIN_KEY + p.getUniqueId(), HyriAPI.get().getServer().getId());
                    jedis.set(REJOIN_KEY + p.getUniqueId(), "game-cx145sd");
                    jedis.expire(REJOIN_KEY + p.getUniqueId(), this.maxReconnectionTime);
                }
            } else {
                this.players.remove(player);

                if (player.hasTeam()) {
                    player.getTeam().removePlayer(player);
                }

                if (jedis != null) {
                    jedis.del(CURRENT_GAME_KEY + p.getUniqueId().toString());
                }
            }

            if (this.tabListUsed) {
                this.tabListManager.handleLogout(p);
                this.updateTabList();
            }
        });
    }

    public void handleEnd() {
        this.state = HyriGameState.ENDED;

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            for (HyriGamePlayer player : this.players) {
                if (player.isOnline()) {
                    HyriAPI.get().getServerManager().sendPlayerToLobby(player.getUuid());
                }
            }
        }, 20 * 20);

        Bukkit.getScheduler().runTaskLater(this.plugin, Bukkit::shutdown, 20 * 25);
    }

    public P getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public List<P> getSpectators() {
        return this.players.stream().filter(HyriGamePlayer::isSpectator).collect(Collectors.toList());
    }

    public HyriGameTeam getTeam(String name) {
        for (HyriGameTeam team : this.teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public HyriGameState getState() {
        return this.state;
    }

    public void setState(HyriGameState state) {
        this.state = state;
    }

    public List<P> getPlayers() {
        return this.players;
    }

    public List<HyriGameTeam> getTeams() {
        return this.teams;
    }

    public boolean isDefaultStarting() {
        return this.defaultStarting;
    }

    public void setDefaultStarting(boolean defaultStarting) {
        this.defaultStarting = defaultStarting;
    }

    List<HyriGameWaitingScoreboard> getWaitingScoreboards() {
        return this.waitingScoreboards;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public boolean isTabListUsed() {
        return this.tabListUsed;
    }

    public void setTabListUsed(boolean tabListUsed) {
        this.tabListUsed = tabListUsed;
    }

    public boolean isReconnectionAllowed() {
        return this.reconnectionAllowed;
    }

    public void setReconnectionAllowed(boolean reconnectionAllowed) {
        this.reconnectionAllowed = reconnectionAllowed;
    }

    public long getMaxReconnectionTime() {
        return this.maxReconnectionTime;
    }

    public void setMaxReconnectionTime(long maxReconnectionTime) {
        this.maxReconnectionTime = maxReconnectionTime;
    }

}
