package fr.hyriode.hyrame.language;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 20:41
 */
public class LanguageManager {

    private final List<LanguageMessage> messages;

    private final Hyrame hyrame;

    public LanguageManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.messages = new ArrayList<>();

        this.loadLanguages();
    }

    public void loadLanguages() {
        final IPluginProvider pluginProvider = this.hyrame.getPluginProvider();

        this.hyrame.log("Loading languages...");

        this.messages.clear();

        for (Language language : Language.values()) {
            final String fileName = language.getCode() + ".json";
            final String path = pluginProvider.getLanguagesPath() + fileName;
            final InputStream inputStream = pluginProvider.getClass().getResourceAsStream(path);

            if (inputStream != null) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final JsonReader reader = new JsonReader(bufferedReader);
                final Map<String, String> map = new Gson().fromJson(reader, Map.class);

                this.hyrame.log("Loading " + language.getCode() + " language from " + fileName + "...");

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    final String key = entry.getKey();
                    final String value = ChatColor.translateAlternateColorCodes('&', entry.getValue());

                    LanguageMessage message = this.getMessage(key);

                    if (message == null) {
                        message = new LanguageMessage(entry.getKey());
                    } else {
                        this.messages.remove(message);
                    }

                    message.addValue(language, value);

                    this.messages.add(message);
                }
            } else {
                this.hyrame.log(Level.SEVERE, "Cannot get resource from " + path + "!");
            }
        }
    }

    public LanguageMessage getMessage(String key) {
        for (LanguageMessage message : this.messages) {
            if (message.getKey().equalsIgnoreCase(key)) {
                return message;
            }
        }
        return null;
    }

    public String getMessage(Language language, String key) {
        return this.getMessage(key).getValue(language);
    }

    public String getMessageForPlayer(UUID uuid, LanguageMessage message) {
        final IHyriPlayer player = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        return message.getValue(Language.valueOf(player.getSettings().getLanguage().name()));
    }

    public String getMessageForPlayer(UUID uuid, String key) {
        final IHyriPlayer player = HyriAPI.get().getPlayerManager().getPlayer(uuid);

        return this.getMessage(Language.valueOf(player.getSettings().getLanguage().name()), key);
    }

    public String getMessageForPlayer(Player player, String key) {
        return this.getMessageForPlayer(player.getUniqueId(), key);
    }

}
