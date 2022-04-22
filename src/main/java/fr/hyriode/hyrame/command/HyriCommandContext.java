package fr.hyriode.hyrame.command;

import org.bukkit.command.CommandSender;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 13:48
 */
public class HyriCommandContext {

    /** The sender of the command */
    private final CommandSender sender;
    /** The label used for the command */
    private final String label;
    /** The arguments provided by the player */
    private final String[] args;
    /** The result of the execution */
    private HyriCommandResult result;
    /** The information about the command */
    private final HyriCommandInfo commandInfo;

    private int argumentPosition;

    /**
     * Constructor of {@link HyriCommandContext}
     *  @param sender Command"s sender
     * @param label Command's label
     * @param args Command's arguments
     * @param commandInfo The command information
     */
    public HyriCommandContext(CommandSender sender, String label, String[] args, HyriCommandInfo commandInfo) {
        this.sender = sender;
        this.label = label;
        this.args = args;
        this.commandInfo = commandInfo;
    }

    /**
     * Get the sender of the command
     *
     * @return {@link CommandSender} instance
     */
    public CommandSender getSender() {
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
     * @return {@link HyriCommandResult} object
     */
    public HyriCommandResult getResult() {
        return this.result;
    }

    /**
     * Set the result of the execution
     *
     * @param result New execution's result
     */
    public void setResult(HyriCommandResult result) {
        this.result = result;
    }

    HyriCommandInfo getCommandInfo() {
        return this.commandInfo;
    }

    int getArgumentPosition() {
        return this.argumentPosition;
    }

    void setArgumentPosition(int argumentPosition) {
        this.argumentPosition = argumentPosition;
    }

}
