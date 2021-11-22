package fr.hyriode.hyrame.impl.language;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:38
 */
public class HyriLanguageManager implements IHyriLanguageManager {

    private final Set<HyriLanguageMessage> messages;

    public HyriLanguageManager() {
        this.messages = ConcurrentHashMap.newKeySet();
    }

    @Override
    public List<HyriLanguageMessage> loadLanguagesMessages(IPluginProvider pluginProvider) {
        final List<HyriLanguageMessage> messages = new ArrayList<>();
        final String formattedPluginProviderName = Hyrame.formatPluginProviderName(pluginProvider);

        Hyrame.log("Loading languages from " + pluginProvider.getLanguagesPath() + " folder ..." + formattedPluginProviderName);

        for (HyriLanguage language : HyriLanguage.values()) {
            final String fileName = language.getCode() + ".json";
            final String path = pluginProvider.getLanguagesPath() + fileName;
            final InputStream inputStream = pluginProvider.getClass().getResourceAsStream(path);

            if (inputStream != null) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final JsonReader reader = new JsonReader(bufferedReader);
                final Map<String, String> map = new Gson().fromJson(reader, Map.class);

                Hyrame.log("Loading " + language.getCode() + " language from " + fileName + "..." + formattedPluginProviderName);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    final String key = entry.getKey();
                    final String value = ChatColor.translateAlternateColorCodes('&', entry.getValue());

                    HyriLanguageMessage message = this.getMessage(key);

                    if (message == null) {
                        message = new HyriLanguageMessage(entry.getKey());
                    } else {
                        this.messages.remove(message);
                    }

                    message.addValue(language, value);

                    this.messages.add(message);

                    messages.remove(message);
                    messages.add(message);
                }
            } else {
                Hyrame.log(Level.INFO, "Cannot get resource from " + fileName + "!" + formattedPluginProviderName);
            }
        }

        return messages;
    }

    @Override
    public void addMessage(HyriLanguageMessage message) {
        this.messages.add(message);
    }

    @Override
    public void removeMessage(HyriLanguageMessage message) {
        this.messages.remove(message);
    }

    @Override
    public void removeMessage(String key) {
        this.messages.remove(this.getMessage(key));
    }

    @Override
    public HyriLanguageMessage getMessage(String key) {
        for (HyriLanguageMessage message : this.messages) {
            if (message.getKey().equalsIgnoreCase(key)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public String getMessageValue(HyriLanguage language, String key) {
        return this.getMessage(key).getValue(language);
    }

    @Override
    public String getMessageValueForPlayer(UUID playerUUID, HyriLanguageMessage message) {
        return message.getForPlayer(HyriAPI.get().getPlayerManager().getPlayer(playerUUID));
    }

    @Override
    public String getMessageValueForPlayer(UUID playerUUID, String key) {
        return this.getMessageValueForPlayer(playerUUID, this.getMessage(key));
    }

    @Override
    public String getMessageValueForPlayer(IHyriPlayer player, String key) {
        return this.getMessageValueForPlayer(player.getUUID(), key);
    }

    @Override
    public String getMessageValueForPlayer(Player player, String key) {
        return this.getMessageValueForPlayer(player.getUniqueId(), key);
    }

    @Override
    public Set<HyriLanguageMessage> getMessages() {
        return this.messages;
    }

}