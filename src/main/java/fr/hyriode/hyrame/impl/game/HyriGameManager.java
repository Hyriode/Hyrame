package fr.hyriode.hyrame.impl.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.game.event.HyriGameRegisteredEvent;
import fr.hyriode.hyrame.game.event.HyriGameUnregisteredEvent;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.game.chat.HyriGameChatHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

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
    public void registerGame(Supplier<HyriGame<?>> gameSupplier) {
        if (this.currentGame != null) {
            throw new IllegalStateException("A game is already registered on this server! (" + this.currentGame.getName() + ")");
        }

        HyriAPI.get().getEventBus().publishAsync(new HyriGameRegisteredEvent(this.currentGame = gameSupplier.get()));

        this.hyrame.getTabManager().disableTabList();

        this.gameHandler = new HyriGameHandler(this.hyrame);

        this.hyrame.getConfiguration().setRanksInTabList(false);
        this.hyrame.setChatHandler(new HyriGameChatHandler(this.hyrame));

        this.currentGame.postRegistration();

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            if (this.currentGame != null) {
                final String key = GAMES_KEY + this.currentGame.getName() + ":" + this.currentGame.getType().getName();

                jedis.sadd(key, HyriAPI.get().getServer().getName());

                HyrameLogger.log("Registered '" + this.currentGame.getName() + "' game.");
            }
        });
    }

    @Override
    public void unregisterGame(HyriGame<?> game) {
        if (!this.currentGame.equals(game)) {
            throw new IllegalStateException("The provided game is not registered!");
        }

        HandlerList.unregisterAll(this.gameHandler);

        HyriAPI.get().getRedisProcessor().process(jedis -> {
            final String key = GAMES_KEY + game.getName() + ":" + game.getType().getName();

            jedis.srem(key, HyriAPI.get().getServer().getName());

            game.getProtocolManager().disable();

            this.hyrame.getTabManager().enableTabList();

            this.hyrame.getConfiguration().setRanksInTabList(true);
            this.hyrame.setChatHandler(new HyriDefaultChatHandler());

            this.currentGame = null;

            HyriAPI.get().getEventBus().publishAsync(new HyriGameUnregisteredEvent(game));

            HyrameLogger.log("Unregistered '" + game.getName() + "' game.");
        });
    }

    @Override
    public HyriGame<?> getCurrentGame() {
        return this.currentGame;
    }

    @Override
    public List<String> getGames(String name, String type) {
        return HyriAPI.get().getRedisProcessor().get(jedis -> new ArrayList<>(jedis.smembers(GAMES_KEY + name + ":" + type)));
    }

    @Override
    public List<String> getGames(String name) {
        return this.getGames(name, HyriGame.DEFAULT_TYPE.getName());
    }

    @Override
    public List<String> getGames() {
        return HyriAPI.get().getRedisProcessor().get(jedis -> {
            final Set<String> keys = jedis.keys(GAMES_KEY + "*");
            final List<String> games = new ArrayList<>();

            for (String key : keys) {
                if (key.contains(":")) {
                    final String[] splitted = key.split(":");

                    if (splitted.length == 3) {
                        games.addAll(this.getGames(splitted[1], splitted[2]));
                    }
                }
            }
            return games;
        });
    }

}
