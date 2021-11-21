package fr.hyriode.hyrame.command;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyriapi.HyriAPI;
import fr.hyriode.hyriapi.player.IHyriPlayer;
import fr.hyriode.hyriapi.rank.HyriPermission;
import fr.hyriode.hyriapi.settings.HyriLanguage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 18:52
 */
public abstract class HyriCommand<T extends JavaPlugin> extends Command {

    /** Some languages constants */
    static final HyriLanguageMessage INVALID_COMMAND = new HyriLanguageMessage("commad.invalid")
            .addValue(HyriLanguage.EN, "Invalid command: ")
            .addValue(HyriLanguage.FR, "Commande invalide : ");

    static final HyriLanguageMessage DONT_HAVE_PERMISSION = new HyriLanguageMessage("command.permission")
            .addValue(HyriLanguage.EN, "You don't have the permission to execute this command!")
            .addValue(HyriLanguage.FR, "Tu n'as pas la permission d'éxécuter cette commande!");

    /** Command arguments */
    protected final List<HyriCommandArgument> arguments;


    /** Plugin of tre command */
    protected final T plugin;

    /** Command name (because the name field in {@link Command} is in private) */
    protected String name;

    /** Is command only for players */
    protected boolean onlyPlayers;

    /** Permission needed to execute command */
    protected HyriPermission permission;

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     */
    public HyriCommand(T plugin, String name) {
        this(plugin, name, "", false);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param permission - Command's permission
     */
    public HyriCommand(T plugin, String name, HyriPermission permission) {
        this(plugin, name, "", permission);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param onlyPlayers - Is command only for players
     */
    public HyriCommand(T plugin, String name, boolean onlyPlayers) {
        this(plugin, name, "", onlyPlayers);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     */
    public HyriCommand(T plugin, String name, String description) {
        this(plugin, name, description, new ArrayList<>(), null);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param permission - Command's permission
     */
    public HyriCommand(T plugin, String name, String description, HyriPermission permission) {
        this(plugin, name, description, new ArrayList<>(), permission, false);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param onlyPlayers - Is command only for players
     */
    public HyriCommand(T plugin, String name, String description, boolean onlyPlayers) {
        this(plugin, name, description, new ArrayList<>(), null, onlyPlayers);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param permission - Command's permission
     * @param onlyPlayers - Is command only for players
     */
    public HyriCommand(T plugin, String name, String description, HyriPermission permission, boolean onlyPlayers) {
        this(plugin, name, description, new ArrayList<>(), permission, onlyPlayers);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param aliases - Command's aliases
     */
    public HyriCommand(T plugin, String name, String description, List<String> aliases) {
        this(plugin, name, description, aliases, false);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param aliases - Command's aliases
     * @param permission - Command's permission
     */
    public HyriCommand(T plugin, String name, String description, List<String> aliases, HyriPermission permission) {
        this(plugin, name, description, aliases, permission, false);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param aliases - Command's aliases
     * @param onlyPlayers - Is command only for players
     */
    public HyriCommand(T plugin, String name, String description, List<String> aliases, boolean onlyPlayers) {
        this(plugin, name, description, aliases, null, onlyPlayers);
    }

    /**
     * Constructor of {@link HyriCommand}
     *
     * @param plugin - Command's plugin
     * @param name - Command's name
     * @param description - Command's description
     * @param aliases - Command's aliases
     * @param permission - Command's permission
     * @param onlyPlayers - Is command only for players
     */
    public HyriCommand(T plugin, String name, String description, List<String> aliases, HyriPermission permission, boolean onlyPlayers) {
        super(name, description, "", aliases);
        this.name = name;
        this.plugin = plugin;
        this.arguments = new ArrayList<>();
        this.permission = permission;
        this.onlyPlayers = onlyPlayers;
    }

    public abstract void handle(CommandSender sender, String label, String[] args);

    /**
     * Fired when a sender execute the command
     *
     * @param sender - The command sender
     * @param label - The command label
     * @param args - The command arguments
     * @return - <code>true</code> if there are no problem
     */
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (this.onlyPlayers) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You need to be a player to execute this command!");

                return true;
            }
        }
        if (sender instanceof Player && this.permission != null) {
            final Player player = (Player) sender;

            if (!HyriAPI.get().getPlayerManager().hasPermission(player.getUniqueId(), this.permission)) {
                player.sendMessage(DONT_HAVE_PERMISSION.getForPlayer(player));

                return true;
            }
        }

        if (this.handleArguments(sender, args)) {
            this.handle(sender, label, args);
        }
        return true;
    }

    /**
     * Handle arguments to fire them if the first given argument exists
     * Example: /test argument1, if argument1 exists it will call it
     *
     * @param sender - The command sender
     * @param args - The command arguments
     * @return <code>true</code> if it can handle basic command
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
                    this.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());
                }
            } else {
                this.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());
            }

            return false;
        } else {
            if (this.arguments.size() > 0) {
                this.sendInvalidCommandMessage(sender, this.getFormattedUsageMessage());

                return false;
            }
        }
        return true;
    }

    /**
     * Used to send an error message when the command is valid
     *
     * @param sender Command sender
     */
    void sendInvalidCommandMessage(CommandSender sender, String formattedUsageMessage) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + INVALID_COMMAND.getForPlayer((Player) sender) + formattedUsageMessage);
        } else {
            sender.sendMessage(ChatColor.RED + INVALID_COMMAND.getValue(HyriLanguage.EN) + formattedUsageMessage);
        }
    }

    /**
     * Used to handle errors
     *
     * @return - A formatted string
     */
    private String getFormattedUsageMessage() {
        if (this.arguments.size() > 0) {
            final StringBuilder builder = new StringBuilder(" <");

            for (HyriCommandArgument argument : this.arguments) {
                builder.append(argument.getName())
                        .append("|");
            }

            return ChatColor.WHITE + "/" + this.name + builder.substring(0, builder.length() - 1) + ">";
        }
        return ChatColor.WHITE + "/" + this.name;
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
                throw new IllegalArgumentException("This command has a dynamic argument!");
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

}
