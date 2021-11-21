package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.util.ThreadPool;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.server.IHyriServer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 20:18
 */
public class HyriGameManager implements IHyriGameManager {

    private static final String GAMES_KEY = "games:";

    private HyriGame<?> currentGame;
    private String gameServerName;

    private final Hyrame hyrame;

    public HyriGameManager(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public void registerGame(HyriGame<?> game) {
        if (this.currentGame != null) {
            throw new IllegalStateException("A game is already registered on this server! (" + this.currentGame.getName() + ")");
        }

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            final String key = GAMES_KEY + game.getName();

            if (jedis != null) {
                final IHyriServer server = HyriAPI.get().getServer();

                this.gameServerName = server == null ? game.getName() + "-" + UUID.randomUUID().toString().split("-")[0] : server.getName();

                jedis.rpush(key, this.gameServerName);
                jedis.close();

                this.hyrame.getTabManager().disable();

                this.currentGame = game;

                new HyriGameHandler(this.hyrame);

                game.postRegistration();

                Hyrame.log("Registered '" + game.getName() + "' game.");
            } else {
                Hyrame.log(Level.SEVERE, "Cannot register game! Error caused by Redis!");
            }
        });
    }

    @Override
    public void unregisterGame(HyriGame<?> game) {
        if (!this.currentGame.equals(game)) {
            throw new IllegalStateException("The provided game is not registered!");
        }

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            final String key = GAMES_KEY + game.getName();

            if (jedis != null) {
                jedis.lrem(key, 0, this.gameServerName);
                jedis.close();

                this.currentGame = null;
                this.gameServerName = null;

                this.hyrame.getTabManager().enable();

                Hyrame.log("Unregistered '" + game.getName() + "' game.");
            } else {
                Hyrame.log(Level.SEVERE, "Cannot unregister game! Error caused by Redis!");
            }
        });
    }

    @Override
    public HyriGame<?> getCurrentGame() {
        return this.currentGame;
    }

    @Override
    public String getGameServerName() {
        return this.gameServerName;
    }

    @Override
    public List<String> getGames(String name) {
        final Jedis jedis = HyriAPI.get().getRedisResource();
        final String key = GAMES_KEY + name;

        if (jedis != null) {
            final List<String> games = jedis.lrange(key, 0, -1);

            jedis.close();

            return games;
        } else {
            Hyrame.log(Level.SEVERE, "Cannot get games! Error caused by Redis!");
        }
        return null;
    }

    @Override
    public Set<String> getGames() {
        final Jedis jedis = HyriAPI.get().getRedisResource();
        final String key = GAMES_KEY + "*";

        if (jedis != null) {
            final Set<String> games = jedis.keys(key);

            jedis.close();

            return games;
        } else {
            Hyrame.log(Level.SEVERE, "Cannot get games! Error caused by Redis!");
        }
        return null;
    }

}
