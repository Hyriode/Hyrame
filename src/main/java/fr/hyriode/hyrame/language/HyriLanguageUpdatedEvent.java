package fr.hyriode.hyrame.language;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEvent;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriLanguage;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 19:39
 */
public class HyriLanguageUpdatedEvent extends HyriEvent {

    /** The player concerned */
    private final Player player;
    /** The account of the player concerned */
    private final IHyriPlayer playerAccount;
    /** The new language */
    private final HyriLanguage language;

    /**
     * Constructor of {@link HyriLanguageUpdatedEvent}
     *
     * @param player The player
     * @param language The new language
     */
    public HyriLanguageUpdatedEvent(Player player, HyriLanguage language) {
        this.player = player;
        this.playerAccount = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        this.language = language;
    }

    /**
     * Get the player concerned
     *
     * @return {@link Player} object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the player account
     *
     * @return {@link IHyriPlayer} object
     */
    public IHyriPlayer getPlayerAccount() {
        return this.playerAccount;
    }

    /**
     * Get the new language of the player
     *
     * @return {@link HyriLanguage}
     */
    public HyriLanguage getLanguage() {
        return this.language;
    }

}
