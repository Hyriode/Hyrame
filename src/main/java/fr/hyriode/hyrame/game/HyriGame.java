package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.util.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:16
 */
public class HyriGame<P extends HyriGamePlayer> {

    private static final String CURRENT_GAME_KEY = "currentGame:";
    private static final String LAST_GAME_KEY = "lastGame:";
    private static final String REJOIN_KEY = "rejoin:";

    protected HyriGameState state;

    protected boolean reconnectionAllowed;
    protected long maxReconnectionTime = -1;

    protected final List<P> players;

    private final Class<P> playerClass;

    protected final String displayName;
    protected final String name;

    public HyriGame(String name, String displayName, Class<P> playerClass) {
        this.name = name;
        this.displayName = displayName;
        this.playerClass = playerClass;
        this.state = HyriGameState.WAITING;
        this.players = new ArrayList<>();
    }

    public P getPlayer(UUID uuid) {
        return this.players.stream().filter(player -> player.getUuid().equals(uuid)).findFirst().orElse(null);
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            p.sendMessage(ChatColor.RED + "An error occurred during game join! Sending you back to lobby...");

            HyriAPI.get().getServerManager().sendPlayerToLobby(p.getUniqueId());
            e.printStackTrace();
        }
    }

    public void handleLogout(Player p) {
        final P player = this.getPlayer(p.getUniqueId());

        final Jedis jedis = HyriAPI.get().getJedisResource();

        if (jedis != null) {
            jedis.set(LAST_GAME_KEY + p.getUniqueId().toString(), this.name);
            jedis.close();
        }

        if (this.reconnectionAllowed && this.maxReconnectionTime > 0) {
            if (jedis != null) {
                // jedis.set(REJOIN_KEY + p.getUniqueId(), HyriAPI.get().getServer().getId());
                jedis.set(REJOIN_KEY + p.getUniqueId(), "game-cx145sd");
                jedis.expire(REJOIN_KEY + p.getUniqueId(), this.maxReconnectionTime);
            }
        } else {
            this.players.remove(player);

            if (jedis != null) {
                jedis.del(CURRENT_GAME_KEY + p.getUniqueId().toString());
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<P> getPlayers() {
        return this.players;
    }

    public HyriGameState getState() {
        return this.state;
    }

    public void setState(HyriGameState state) {
        this.state = state;
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
