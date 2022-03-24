package fr.hyriode.hyrame.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/03/2022 at 16:17
 */
public abstract class PlaceholderAPI {

    /** The static instance of the {@link PlaceholderAPI} implementation */
    private static PlaceholderAPI instance;

    /** The list of registered {@link PlaceholderHandler} */
    protected final List<PlaceholderHandler> handlers;

    /**
     * Constructor of {@link PlaceholderAPI}
     */
    public PlaceholderAPI() {
        this.handlers = new ArrayList<>();
    }

    /**
     * Replace all placeholders in a text
     *
     * @param player A player
     * @param text The text with placeholders
     * @return The same text but with replaced placeholders
     */
    public abstract String replacePlaceholders(OfflinePlayer player, String text);

    /**
     * Get the list of registered {@link PlaceholderHandler}
     *
     * @return A {@link List} of {@link PlaceholderHandler}
     */
    public List<PlaceholderHandler> getHandlers() {
        return this.handlers;
    }

    /**
     * Register the implementation of {@link PlaceholderAPI}
     *
     * @param instance The instance of {@link PlaceholderAPI}
     */
    public static void registerInstance(PlaceholderAPI instance) {
        if (instance == null) {
            throw new IllegalStateException("PlaceholderAPI instance is already registered!");
        }

        PlaceholderAPI.instance = instance;

        //HyrameLogger.log("Registered '" + instance.getClass().getName() + "' as PlaceholderAPI implementation.");
    }

    /**
     * Replace all placeholders in a text
     *
     * @param player A player
     * @param text The text with placeholders
     * @return The same text but with replaced placeholders
     */
    public static String setPlaceholders(OfflinePlayer player, String text) {
        return instance.replacePlaceholders(player, text);
    }

    /**
     * Replace all placeholders in a text
     *
     * @param player A player
     * @param text The text with placeholders
     * @return The same text but with replaced placeholders
     */
    public static String setPlaceholders(Player player, String text) {
        return instance.replacePlaceholders(player, text);
    }

    /**
     * Replace all placeholders in a text but the number of times requested
     *
     * @param player A player
     * @param text The text with placeholders
     * @param times The number of times to set placeholders
     * @return The same text but with replaced placeholders
     */
    public static String setPlaceholders(OfflinePlayer player, String text, int times) {
        String result = text;
        for (int i = 0; i < times; i++) {
            result = setPlaceholders(player, result);
        }
        return result;
    }

    /**
     * Replace all placeholders in a text but the number of times requested
     *
     * @param player A player
     * @param text The text with placeholders
     * @param times The number of times to set placeholders
     * @return The same text but with replaced placeholders
     */
    public static String setPlaceholders(Player player, String text, int times) {
        String result = text;
        for (int i = 0; i < times; i++) {
            result = setPlaceholders(player, result);
        }
        return result;
    }

    /**
     * Register a given placeholder handler
     *
     * @param handler The {@link PlaceholderHandler} to register
     */
    public static void registerHandler(PlaceholderHandler handler) {
        instance.getHandlers().add(handler);
    }

}
