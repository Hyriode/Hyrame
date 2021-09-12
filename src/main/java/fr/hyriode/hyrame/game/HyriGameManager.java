package fr.hyriode.hyrame.game;

import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyriapi.HyriAPI;
import redis.clients.jedis.Jedis;

import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 09/09/2021 at 19:59
 */
public class HyriGameManager {

    private static final String GAMES_KEY = "games:";

    private HyriGame<?> currentGame;

    private final Hyrame hyrame;

    public HyriGameManager(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    public void registerGame(HyriGame<?> game) {
        if (this.currentGame != null) {
            throw new IllegalStateException("A game is already registered on this server! (" + this.currentGame.getName() + ")");
        }

        final Jedis jedis = HyriAPI.get().getJedisResource();
        final String key = GAMES_KEY + game.getName();

        if (jedis != null) {
            //jedis.rpush(key, HyriAPI.get().getServer().getId());
            jedis.rpush(key, "game-cx145sd");
            jedis.close();

            this.currentGame = game;

            new HyriGameHandler(this.hyrame);

            this.hyrame.log("Registered '" + game.getName() + "' game.");
        } else {
            this.hyrame.log(Level.SEVERE, "Cannot register game! Error caused by Redis!");
        }
    }

    public void unregisterGame(HyriGame<?> game) {
        if (!game.getName().equals(this.currentGame.getName())) {
            throw new IllegalStateException("The provided game is not registered!");
        }

        final Jedis jedis = HyriAPI.get().getJedisResource();
        final String key = GAMES_KEY + game.getName();

        if (jedis != null) {
            //jedis.lrem(key, 0, HyriAPI.get().getServer().getId());
            jedis.lrem(key, 0, "game-cx145sd");
            jedis.close();

            this.currentGame = null;

            this.hyrame.log("Unregistered '" + game.getName() + "' game.");
        } else {
            this.hyrame.log(Level.SEVERE, "Cannot unregister game! Error caused by Redis!");
        }
    }

    public void unregisterGame() {
        this.unregisterGame(this.currentGame);
    }

    public HyriGame<?> getCurrentGame() {
        return this.currentGame;
    }

}
