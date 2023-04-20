package fr.hyriode.hyrame.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/12/2021 at 10:07
 */
public class CommandResult {

    /** Result's type */
    private final Type type;
    /** Result's message */
    private final CommandUsage usage;

    public CommandResult(Type type, CommandUsage usage) {
        this.type = type;
        this.usage = usage;
    }

    public CommandResult(Type type, String usage) {
        this(type, new CommandUsage().withStringMessage(player -> usage));
    }

    public CommandResult(Type type) {
        this(type, "");
    }

    public Type getType() {
        return this.type;
    }

    public CommandUsage getUsage() {
        return this.usage;
    }

    /**
     * A simple enum with all result types
     */
    public enum Type {
        /** The command was successfully executed */
        SUCCESS,
        /** An error occurred */
        ERROR,
        /** An error occurred in a check */
        CHECK_ERROR,
    }

}
