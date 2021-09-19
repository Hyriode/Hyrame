package fr.hyriode.hyrame.language;

import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 20:32
 */
public class LanguageMessage {

    private String key;
    private Map<Language, String> values;

    public LanguageMessage(String key) {
        this.key = key;
        this.values = new HashMap<>();
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LanguageMessage addValue(Language language, String value) {
        this.values.put(language, value);

        return this;
    }

    public LanguageMessage removeValue(Language language, String value) {
        this.values.remove(language, value);

        return this;
    }

    public void setValues(Map<Language, String> values) {
        this.values = values;
    }

    public Map<Language, String> getValues() {
        return this.values;
    }

    public String getValue(Language language) {
        return this.values.get(language);
    }

    public String getForPlayer(IHyriPlayer player) {
        return this.getValue(Language.valueOf(player.getSettings().getLanguage().name()));
    }

    public String getForPlayer(Player player) {
        return this.getForPlayer(HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId()));
    }

}