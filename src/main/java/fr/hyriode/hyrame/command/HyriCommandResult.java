package fr.hyriode.hyrame.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 10:07
 */
public class HyriCommandResult {

    /** Result's type */
    private final Type type;
    /** Result's message */
    private final BaseComponent[] message;

    /**
     * Constructor of {@link HyriCommandResult}
     *
     * @param type Result's type
     * @param message Result's message
     */
    public HyriCommandResult(Type type, BaseComponent[] message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Constructor of {@link HyriCommandResult}
     *
     * @param type Result's type
     * @param message Result's message
     */
    public HyriCommandResult(Type type, String message) {
        this(type, TextComponent.fromLegacyText(message));
    }

    /**
     * Constructor of {@link HyriCommandResult}
     *
     * @param type Result's type
     */
    public HyriCommandResult(Type type) {
        this(type, "");
    }

    /**
     * Get result's type
     *
     * @return {@link Type}
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get result's message
     *
     * @return A message
     */
    public BaseComponent[] getMessage() {
        return this.message;
    }

    /**
     * A simple enum with all result types
     */
    public enum Type {
        /** The command was successfully executed */
        SUCCESS,
        /** An error occurred */
        ERROR
    }

}
