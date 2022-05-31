package fr.hyriode.hyrame.impl.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.game.event.HyriGameRegisteredEvent;
import fr.hyriode.hyrame.game.event.HyriGameUnregisteredEvent;
import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 20:18
 */
public class HyriGameManager implements IHyriGameManager {

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

        if (this.currentGame.isUsingGameTabList()) {
            this.hyrame.getConfiguration().setRanksInTabList(false);
        }


        this.gameHandler = new HyriGameHandler(this.hyrame);

        this.currentGame.postRegistration();

        HyrameLogger.log("Registered '" + this.currentGame.getName() + "' game.");
    }

    @Override
    public void unregisterGame(HyriGame<?> game) {
        if (!this.currentGame.equals(game)) {
            throw new IllegalStateException("The provided game is not registered!");
        }

        HandlerList.unregisterAll(this.gameHandler);

        game.getProtocolManager().disable();

        if (this.currentGame.isUsingGameTabList()) {
            this.hyrame.getConfiguration().setRanksInTabList(true);
        }

        this.currentGame = null;

        HyriAPI.get().getEventBus().publishAsync(new HyriGameUnregisteredEvent(game));

        HyrameLogger.log("Unregistered '" + game.getName() + "' game.");
    }

    @Override
    public HyriGame<?> getCurrentGame() {
        return this.currentGame;
    }

}
