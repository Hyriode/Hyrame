package fr.hyriode.hyrame.impl.game;

import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.impl.game.chat.HyriGameChatHandler;
import fr.hyriode.hyriapi.HyriAPI;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 20:18
 */
public class HyriGameManager implements IHyriGameManager {

    private static final String GAMES_KEY = "games:";

    private Listener gameHandler;
    private HyriGame<?> currentGame;

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
            final String key = GAMES_KEY + game.getName() + ":" + game.getType().getName();

            if (jedis != null) {
                jedis.rpush(key, HyriAPI.get().getServer().getName());
            } else {
                Hyrame.log(Level.SEVERE, "Cannot register game! Error caused by Redis!");
            }
        }, () -> {
            this.hyrame.getTabManager().disableTabList();

            this.currentGame = game;

            this.gameHandler = new HyriGameHandler(this.hyrame);

            this.hyrame.setChatHandler(new HyriGameChatHandler(this.hyrame));

            Hyrame.log("Registered '" + game.getName() + "' game.");

            game.postRegistration();
        });
    }

    @Override
    public void unregisterGame(HyriGame<?> game) {
        if (!this.currentGame.equals(game)) {
            throw new IllegalStateException("The provided game is not registered!");
        }

        HandlerList.unregisterAll(this.gameHandler);

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            final String key = GAMES_KEY + game.getName();

            if (jedis != null) {
                jedis.lrem(key, 0, HyriAPI.get().getServer().getName());
            } else {
                Hyrame.log(Level.SEVERE, "Cannot unregister game! Error caused by Redis!");
            }
        }, () -> {
            this.currentGame = null;

            this.hyrame.getTabManager().enableTabList();

            this.hyrame.setChatHandler(new HyriDefaultChatHandler());

            Hyrame.log("Unregistered '" + game.getName() + "' game.");
        });
    }

    @Override
    public HyriGame<?> getCurrentGame() {
        return this.currentGame;
    }

    @Override
    public List<String> getGames(String name, String type) {
        final Jedis jedis = HyriAPI.get().getRedisResource();
        final String key = GAMES_KEY + name + ":" + type;

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
    public List<String> getGames(String name) {
        return this.getGames(name, HyriGame.DEFAULT_TYPE.getName());
    }

    @Override
    public List<String> getGames() {
        final Jedis jedis = HyriAPI.get().getRedisResource();
        final String key = GAMES_KEY + "*";

        if (jedis != null) {
            final List<String> games = new ArrayList<>(jedis.keys(key));

            jedis.close();

            return games;
        } else {
            Hyrame.log(Level.SEVERE, "Cannot get games! Error caused by Redis!");
        }
        return null;
    }

}
