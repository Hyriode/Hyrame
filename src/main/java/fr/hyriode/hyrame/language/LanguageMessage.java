package fr.hyriode.hyrame.language;

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

    public void addValue(Language language, String value) {
        this.values.put(language, value);
    }

    public void removeValue(Language language, String value) {
        this.values.remove(language, value);
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

}