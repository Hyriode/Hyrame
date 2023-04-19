package fr.hyriode.hyrame.command;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 13:48
 */
public class CommandContext {

    /** The arguments to run in this context */
    private final List<CommandArgument> arguments = new ArrayList<>();

    /** The sender of the command */
    private final Player sender;
    /** The label used for the command */
    private final String label;
    /** The arguments provided by the player */
    private final String[] args;
    /** The result of the execution */
    private CommandResult result;
    /** The information about the command */
    private final CommandInfo commandInfo;

    private int argumentPosition;

    /**
     * Constructor of {@link CommandContext}
     *  @param sender Command"s sender
     * @param label Command's label
     * @param args Command's arguments
     * @param commandInfo The command information
     */
    public CommandContext(Player sender, String label, String[] args, CommandInfo commandInfo) {
        this.sender = sender;
        this.label = label;
        this.args = args;
        this.commandInfo = commandInfo;
    }

    /**
     * Register a command argument
     *
     * @param expected The expected input for the argument
     * @param usage The usage of the command
     * @param action The action to trigger
     */
    public void registerArgument(String expected, Function<Player, BaseComponent[]> usage, Consumer<CommandOutput> action) {
        final CommandArgument argument = new CommandArgument(expected, usage, action);

        this.arguments.add(argument);
    }

    /**
     * Register a command argument
     *
     * @param expected The expected input for the argument
     * @param action The action to trigger
     */
    public void registerArgument(String expected, Consumer<CommandOutput> action) {
        this.registerArgument(expected, this.commandInfo.getUsage().getMessage(), action);
    }

    /**
     * Get the arguments to run in this context
     *
     * @return A list of {@link CommandArgument}
     */
    public List<CommandArgument> getArguments() {
        return this.arguments;
    }

    /**
     * Get the sender of the command
     *
     * @return {@link CommandSender} instance
     */
    public Player getSender() {
        return this.sender;
    }

    /**
     * Get the label used
     *
     * @return A label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Get the arguments provided
     *
     * @return A list of arguments
     */
    public String[] getArgs() {
        return this.args;
    }

    /**
     * Get the result of the execution
     *
     * @return {@link CommandResult} object
     */
    public CommandResult getResult() {
        return this.result;
    }

    /**
     * Set the result of the execution
     *
     * @param result New execution's result
     */
    public void setResult(CommandResult result) {
        this.result = result;
    }

    CommandInfo getCommandInfo() {
        return this.commandInfo;
    }

    int getArgumentPosition() {
        return this.argumentPosition;
    }

    void setArgumentPosition(int argumentPosition) {
        this.argumentPosition = argumentPosition;
    }

}
