package fr.hyriode.hyrame.command;

import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.rank.HyriPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 18:15
 */
public abstract class HyriCommandArgument {

    /** Arguments of the argument */
    protected final List<HyriCommandArgument> arguments;

    /** Main command of the argument */
    protected final HyriCommand<?> command;

    /** Argument's name */
    protected final String name;

    /** Is argument dynamic. If it is, it will accept all input */
    protected final boolean dynamic;

    /** Permission needed to execute this argument */
    protected final HyriPermission permission;

    /** Is default handle method used */
    protected boolean defaultHandle = true;

    /**
     * Constructor of {@link HyriCommandArgument}
     *
     * @param command - Main command
     * @param name - Argument's name
     */
    public HyriCommandArgument(HyriCommand<?> command, String name) {
        this(command, name, null);
    }

    /**
     * Constructor of {@link HyriCommandArgument}
     *
     * @param command - Main command
     * @param name - Argument's name
     * @param permission - Argument's permission
     */
    public HyriCommandArgument(HyriCommand<?> command, String name, HyriPermission permission) {
        this(command, name, false, permission);
    }

    /**
     * Constructor of {@link HyriCommandArgument}
     *
     * @param command - Main command
     * @param name - Argument's name
     * @param dynamic - Is argument dynamic
     */
    public HyriCommandArgument(HyriCommand<?> command, String name, boolean dynamic) {
        this(command, name, dynamic, null);
    }

    /**
     * Constructor of {@link HyriCommandArgument}
     *
     * @param command - Main command
     * @param name - Argument's name
     * @param dynamic - Is argument dynamic
     * @param permission - Argument's permission
     */
    public HyriCommandArgument(HyriCommand<?> command, String name, boolean dynamic, HyriPermission permission) {
        this.command = command;
        this.name = name;
        this.dynamic = dynamic;
        this.arguments = new ArrayList<>();
        this.permission = permission;

        if (this.name == null || this.name.isEmpty()) {
            throw new IllegalArgumentException("Argument name cannot be null!");
        }
    }

    public abstract void handle(CommandSender sender, String label, String[] args);

    /**
     * Execute argument
     *
     * @param sender - Command's sender
     * @param label - Argument's label
     * @param args - Command's arguments
     */
    void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player && this.permission != null) {
            final Player player = (Player) sender;

            if (!HyriAPI.get().getPlayerManager().hasPermission(player.getUniqueId(), this.permission)) {
                player.sendMessage(HyriCommand.DONT_HAVE_PERMISSION.getForPlayer(player));

                return;
            }
        }

        if (this.handleArguments(sender, args)) {
            this.handle(sender, label, args);
        }
    }

    /**
     * Handle arguments to fire them if the first given argument exists
     * Example: /test argument1, if argument1 exists it will call it
     *
     * @param sender - The command sender
     * @param args - The command arguments
     * @return <code>true</code> if it can handle basic argument
     */
    protected boolean handleArguments(CommandSender sender, String[] args) {
        if (args.length > 0) {
            final String firstArg = args[0].toLowerCase();

            if (this.arguments.size() > 0) {
                final HyriCommandArgument firstArgument = this.arguments.get(0);

                HyriCommandArgument argument = this.getArgumentByName(firstArg);
                if (firstArgument != null && firstArgument.isDynamic()) {
                    argument = firstArgument;
                }

                if (argument != null) {
                    argument.execute(sender, firstArg, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    this.command.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());
                }
            } else {
                this.command.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());
            }

            return false;
        } else {
            if (this.arguments.size() > 0 && !this.defaultHandle) {
                this.command.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());

                return false;
            }
        }
        return true;
    }

    /**
     * Used to handle errors
     *
     * @return - A formatted string
     */
    private String getFormattedUsageMessage() {
        if (this.arguments.size() > 0) {
            final StringBuilder builder = new StringBuilder("<");

            for (HyriCommandArgument argument : this.arguments) {
                builder.append(argument.getName())
                        .append("|");
            }

            return ChatColor.WHITE + "/" + this.command.getName() + " " + this.name + " " + builder.substring(0, builder.length() - 1) + ">";
        }
        return ChatColor.WHITE + "/" + this.command.getName() + " " + this.name;
    }

    /**
     * Add an argument to the command
     *
     * @param argument - Argument to add
     * @return - <code>true</code> if it was successful
     */
    public boolean addArgument(HyriCommandArgument argument) {
        if (this.getArgumentByName(argument.getName()) == null) {
            if (this.arguments.size() > 0 && this.arguments.get(0).isDynamic()) {
                throw new IllegalArgumentException("This argument has a dynamic argument!");
            }

            this.arguments.add(argument);

            return true;
        }
        return false;
    }

    /**
     * Remove an argument to the command
     *
     * @param name - Argument's name
     * @return - <code>true</code> if it was successful
     */
    public boolean removeArgument(String name) {
        final HyriCommandArgument argument = this.getArgumentByName(name);

        if (argument != null) {
            this.arguments.remove(argument);

            return true;
        }
        return false;
    }

    /**
     * Get a command argument by giving its name
     *
     * @param name - Argument's name
     * @return - The argument object
     */
    public HyriCommandArgument getArgumentByName(String name) {
        for (HyriCommandArgument argument : this.arguments) {
            if (argument.getName().equals(name)) {
                return argument;
            }
        }
        return null;
    }

    /**
     * Get all command arguments
     *
     * @return - A list of {@link HyriCommandArgument}
     */
    public List<HyriCommandArgument> getArguments() {
        return this.arguments;
    }

    /**
     * Get argument's name
     *
     * @return - Argument's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Check if argument is dynamic
     *
     * @return - <code>true</code> if yes
     */
    public boolean isDynamic() {
        return this.dynamic;
    }

}
