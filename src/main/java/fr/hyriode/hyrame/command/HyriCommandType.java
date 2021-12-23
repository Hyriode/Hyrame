package fr.hyriode.hyrame.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/12/2021 at 09:22
 */
public enum HyriCommandType {

    /** Command can only be executed by a player */
    PLAYER("a player", sender -> sender instanceof Player),
    /** Command can only be executed by the console */
    CONSOLE("the console", sender -> sender instanceof ConsoleCommandSender),
    /** Every sender can execute the command */
    ALL("", sender -> true);

    /** Text to be display when the check failed */
    private final String display;
    /** Check if sender is good */
    private final Function<CommandSender, Boolean> check;

    /**
     * Constructor of {@link HyriCommandType}
     *
     * @param display Text to display on failure
     * @param check Check function
     */
    HyriCommandType(String display, Function<CommandSender, Boolean> check) {
        this.display = display;
        this.check = check;
    }

    /**
     * Check type display<br>
     * This display text is used when the check
     *
     * @return A display text
     */
    public String getDisplay() {
        return this.display;
    }

    /**
     * Get check function<br>
     * Used to see if a {@link CommandSender} is one of the type
     *
     * @return {@link Function}
     */
    public Function<CommandSender, Boolean> getCheck() {
        return this.check;
    }

}
