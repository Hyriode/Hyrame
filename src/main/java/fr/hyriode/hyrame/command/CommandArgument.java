package fr.hyriode.hyrame.command;

import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 19/04/2023 at 21:29
 */
public class CommandArgument {

    private final String expected;
    private final CommandUsage usage;
    private final Consumer<CommandOutput> action;

    public CommandArgument(String expected, CommandUsage usage, Consumer<CommandOutput> action) {
        this.expected = expected;
        this.usage = usage;
        this.action = action;
    }

    public String getExpected() {
        return this.expected;
    }

    public CommandUsage getUsage() {
        return this.usage;
    }

    public Consumer<CommandOutput> getAction() {
        return this.action;
    }

}
