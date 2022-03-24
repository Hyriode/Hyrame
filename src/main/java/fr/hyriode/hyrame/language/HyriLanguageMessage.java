package fr.hyriode.hyrame.language;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriLanguage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 20:32
 */
public class HyriLanguageMessage {

    /** Message's key. For example: gui.team.name */
    private String key;

    /** All the values in different languages for message */
    private Map<HyriLanguage, String> values;

    /**
     * Constructor of {@link HyriLanguageMessage}
     *
     * @param key Message's key
     */
    public HyriLanguageMessage(String key) {
        this.key = key;
        this.values = new HashMap<>();
    }

    /**
     * Constructor of {@link HyriLanguageMessage}
     *
     */
    public HyriLanguageMessage() {
        this.values = new HashMap<>();
    }

    /**
     * Get the message's key
     *
     * @return - Message's key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Set the message's key
     *
     * @param key - New key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Add a value to message
     *
     * @param HyriLanguage - Value's language
     * @param value - Value to add
     * @return - This message (useful to make inline pattern)
     */
    public HyriLanguageMessage addValue(HyriLanguage HyriLanguage, String value) {
        this.values.put(HyriLanguage, value);
        return this;
    }

    /**
     * Remove a value from the message
     *
     * @param HyriLanguage - Value's HyriLanguage to remove
     * @return - This message (useful to make inline pattern)
     */
    public HyriLanguageMessage removeValue(HyriLanguage HyriLanguage) {
        this.values.remove(HyriLanguage);
        return this;
    }

    /**
     * Set the values of the message
     *
     * @param values - Values
     * @return {@link HyriLanguageMessage}
     */
    public HyriLanguageMessage setValues(Map<HyriLanguage, String> values) {
        this.values = values;
        return this;
    }

    /**
     * Get all the values of the message
     *
     * @return - Values
     */
    public Map<HyriLanguage, String> getValues() {
        return this.values;
    }

    /**
     * Get a value of a HyriLanguage
     *
     * @param language - Value's language to get
     * @return - Value
     */
    public String getValue(HyriLanguage language) {
        return this.values.getOrDefault(language, this.values.get(HyriLanguage.EN));
    }

    /**
     * Get the value of the message for a given player
     *
     * @param player - Player account
     * @return - Value
     */
    public String getForPlayer(IHyriPlayer player) {
        return this.getValue(player.getSettings().getLanguage());
    }

    /**
     * Get the value of the message for a given player
     *
     * @param player - Player
     * @return - Value
     */
    public String getForPlayer(Player player) {
        return this.getForPlayer(HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId()));
    }

    /**
     * Get the value of the message for a given sender
     *
     * @param sender A sender
     * @return A value
     */
    public String getForSender(CommandSender sender) {
        if (sender instanceof Player) {
            return this.getForPlayer((Player) sender);
        }
        return this.getValue(HyriLanguage.EN);
    }

    /**
     * Transform the language message to a function
     *
     * @return A {@link Function}
     */
    public Function<Player, String> toFunction() {
        return this::getForPlayer;
    }

    /**
     * Create a {@link HyriLanguageMessage} from values
     *
     * @param values Map with all values
     * @return The created {@link HyriLanguageMessage}
     */
    public static HyriLanguageMessage from(Map<HyriLanguage, String> values) {
        return new HyriLanguageMessage("").setValues(values);
    }

    /**
     * Get a {@link HyriLanguageMessage} from its key
     *
     * @param key the key of the message
     * @return A {@link HyriLanguageMessage}
     */
    public static HyriLanguageMessage get(String key) {
        return IHyriLanguageManager.Provider.get().getMessage(key);
    }

}