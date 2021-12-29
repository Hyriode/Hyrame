package fr.hyriode.hyrame.language;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:27
 */
public interface IHyriLanguageManager {

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
