package fr.hyriode.hyrame.command;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 19/04/2023 at 21:29
 */
public class CommandArgument {

    private final String expected;
    private final Function<Player, BaseComponent[]> usage;
    private final Consumer<CommandOutput> action;

    public CommandArgument(String expected, Function<Player, BaseComponent[]> usage, Consumer<CommandOutput> action) {
        this.expected = expected;
        this.usage = usage;
        this.action = action;
    }

    public String getExpected() {
        return this.expected;
    }

    public Function<Player, BaseComponent[]> getUsage() {
        return this.usage;
    }

    public Consumer<CommandOutput> getAction() {
        return this.action;
    }

}
