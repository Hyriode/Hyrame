package fr.hyriode.hyrame.command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Created by AstFaster
 * on 19/04/2023 at 17:06.<br>
 *
 * Represents how the usage message of a command will be sent.
 */
public class CommandUsage {

    private Function<Player, BaseComponent[]> message;
    private boolean errorPrefix = true;

    public CommandUsage(Function<Player, BaseComponent[]> message, boolean errorPrefix) {
        this.message = message;
        this.errorPrefix = errorPrefix;
    }

    public CommandUsage() {}

    public Function<Player, BaseComponent[]> getMessage() {
        return this.message;
    }

    public CommandUsage withMessage(Function<Player, BaseComponent[]> message) {
        this.message = message;
        return this;
    }

    public CommandUsage withStringMessage(Function<Player, String> message) {
        this.message = player -> TextComponent.fromLegacyText(message.apply(player));
        return this;
    }

    public boolean isErrorPrefix() {
        return this.errorPrefix;
    }

    public CommandUsage withErrorPrefix(boolean errorPrefix) {
        this.errorPrefix = errorPrefix;
        return this;
    }

}
