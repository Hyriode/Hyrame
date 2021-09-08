package fr.hyriode.hyrame.language;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 20:28
 */
public enum Language {

    EN("en"),
    FR("fr");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
