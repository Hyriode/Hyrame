package fr.hyriode.hyrame.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Supplier;

public abstract class HyriCommand extends Command {

    protected final Set<HyriCommandArgument> arguments;

    protected Supplier<? extends JavaPlugin> pluginSupplier;

    public HyriCommand(String name) {
        super(name);
        this.arguments = new HashSet<>();
    }

    public HyriCommand(String name, String description, String usageMessage) {
        this(name, description, usageMessage, new ArrayList<>());
    }

    public HyriCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.arguments = new HashSet<>();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        this.handleArguments(sender, args);

        return true;
    }

    protected void handleArguments(CommandSender sender, String[] args) {
        if (args.length > 0) {
            final String firstArg = args[0];
            final HyriCommandArgument argument = this.getArgumentByName(firstArg);

            if (argument != null) {
                argument.handle(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
    }

    public void setPluginSupplier(Supplier<? extends JavaPlugin> pluginSupplier) {
        this.pluginSupplier = pluginSupplier;
    }

    protected void addArguments() {}

    public boolean addArgument(HyriCommandArgument argument) {
        if (this.getArgumentByName(argument.getName()) == null) {
            this.arguments.add(argument);

            return true;
        }
        return false;
    }

    public boolean removeArgument(String name) {
        final HyriCommandArgument argument = this.getArgumentByName(name);

        if (argument != null) {
            this.arguments.remove(argument);

            return true;
        }
        return false;
    }

    public HyriCommandArgument getArgumentByName(String name) {
        for (HyriCommandArgument argument : this.arguments) {
            if (argument.getName().equals(name)) {
                return argument;
            }
        }
        return null;
    }

    public Set<HyriCommandArgument> getArguments() {
        return this.arguments;
    }

}
