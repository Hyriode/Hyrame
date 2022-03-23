package fr.hyriode.hyrame.language;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:27
 */
public interface IHyriLanguageManager {

    /**
     * A static provider class for {@link IHyriLanguageManager}.<br>
     * Some methods in this class might throw exceptions if they are used at the wrong time.
     */
    class Provider {

        /** The static instance of {@link IHyriLanguageManager} */
        private static IHyriLanguageManager instance;

        /**
         * Register an instance of {@link IHyriLanguageManager}.<br>
         * This method can only be used if no other instance have been registered.
         *
         * @param instanceSupplier The {@link Supplier} of the instance to set
         */
        public static void registerInstance(Supplier<IHyriLanguageManager> instanceSupplier) {
            if (instance == null) {
                instance = instanceSupplier.get();
            }
            throw new IllegalStateException("Language manager static instance is already registered!");
        }

        /**
         * Get the {@link IHyriLanguageManager} instance.<br>
         * It might throw an {@link IllegalStateException}
         *
         * @return The instance of {@link IHyriLanguageManager}
         */
        public static IHyriLanguageManager get() {
            if (instance != null) {
                return instance;
            }
            throw new IllegalStateException("No language manager static instance has been registered");
        }

    }

    /**
     * Load all languages messages in languages package provided by an {@link IPluginProvider}
     * Languages files have to be in the following format: code.json. Example: en.json, fr.json, etc
     *
     * @param pluginProvider - {@link IPluginProvider} with languages packages
     * @return - All the messages loaded
     */
    List<HyriLanguageMessage> loadLanguagesMessages(IPluginProvider pluginProvider);

    /**
     * Call this method when you need to refresh all messages for a player
     */
    void updatePlayerLanguage(Player player);

    /**
     * Add a message
     *
     * @param message - Message to add
     */
    void addMessage(HyriLanguageMessage message);

    /**
     * Remove a message
     *
     * @param message - Message to remove
     */
    void removeMessage(HyriLanguageMessage message);

    /**
     * Remove a message
     *
     * @param key - Message's key to remove
     */
    void removeMessage(String key);

    /**
     * Get a message from a provided key
     *
     * @param key - Message key
     * @return - The message
     */
    HyriLanguageMessage getMessage(String key);

    /**
     * Get the message's value in a language by giving the message key
     *
     * @param language - Value language
     * @param messageKey - Message key
     * @return - Message value
     */
    String getValue(HyriLanguage language, String messageKey);

    /**
     * Get the message's value for a player
     *
     * @param playerUUID - Player {@link UUID}
     * @param message - Message
     * @return - Message value
     */
    String getValue(UUID playerUUID, HyriLanguageMessage message);

    /**
     * Get the message's value for a player by giving the message key
     *
     * @param playerUUID - Player {@link UUID}
     * @param key - Message key
     * @return - Message value
     */
    String getValue(UUID playerUUID, String key);

    /**
     * Get the message's value for a player by giving the message key
     *
     * @param player - Player account
     * @param key - Message key
     * @return - Message value
     */
    String getValue(IHyriPlayer player, String key);

    /**
     * Get the message's value for a player by giving the message key
     *
     * @param player - Player
     * @param key - Message key
     * @return - Message value
     */
    String getValue(Player player, String key);

    /**
     * Get all loaded messages
     *
     * @return - A {@link Set} of messages
     */
    Set<HyriLanguageMessage> getMessages();

}
