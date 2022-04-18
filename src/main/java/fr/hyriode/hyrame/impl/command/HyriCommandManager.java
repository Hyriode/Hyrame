package fr.hyriode.hyrame.impl.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.HyriPermission;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.reflection.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 19:59
 */
public class HyriCommandManager implements IHyriCommandManager {

    private final Map<Class<?>, HyriCommand<?>> commands;

    private final CommandMap commandMap;

    private final Hyrame hyrame;

    public HyriCommandManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.commandMap = (CommandMap) Reflection.invokeField(Bukkit.getServer(), "commandMap");
        this.commands = new HashMap<>();
    }

    @Override
    public void registerCommands() {
        for (IPluginProvider pluginProvider : this.hyrame.getPluginProviders()) {
            this.registerCommands(pluginProvider);
        }
    }

    @Override
    public void registerCommands(IPluginProvider pluginProvider) {
        for (String packageName : pluginProvider.getCommandsPackages()) {
            this.registerCommands(pluginProvider, packageName);
        }
    }

    @Override
    public void registerCommands(IPluginProvider pluginProvider, String packageName) {
        final String formattedPluginProviderName = Hyrame.formatPluginProviderName(pluginProvider);

        HyrameLogger.log("Searching for commands in '" + packageName + "' package" + formattedPluginProviderName);

        final Set<Class<?>> classes = this.hyrame.getScanner().scan(pluginProvider.getClass().getClassLoader(), packageName);

        try {
            for (Class<?> clazz : classes) {
                if (Reflection.inheritOf(clazz, HyriCommand.class)) {
                    final Class<?> pluginClass = pluginProvider.getPlugin().getClass();

                    if (Reflection.hasConstructorWithParameters(clazz, pluginClass)) {
                        final HyriCommand<?> command = (HyriCommand<?>) clazz.getConstructor(pluginClass).newInstance(pluginProvider.getPlugin());
                        final String name = command.getInfo().getName();

                        if (name != null && !name.isEmpty()) {
                            this.commandMap.register(COMMANDS_PREFIX, this.createCommand(command));

                            this.commands.put(command.getClass(), command);

                            HyrameLogger.log("Registered '" + clazz.getName() + "' command with name '" + name + "'" + formattedPluginProviderName);
                        } else {
                            HyrameLogger.log(Level.WARNING, ChatColor.RED + "'" + clazz.getName() + "' command has an empty name! Cannot register it!");
                        }
                    } else {
                        HyrameLogger.log(Level.WARNING, ChatColor.RED + "'" + clazz.getName() + "' command plugin type is not the same as the provided one in plugin provider!" + formattedPluginProviderName);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HyriCommand<?>> T getCommand(Class<T> commandClass) {
        return (T) this.commands.get(commandClass);
    }

    @Override
    public List<HyriCommand<?>> getCommands() {
        return new ArrayList<>(this.commands.values());
    }

    private Command createCommand(HyriCommand<?> command) {
        final HyriCommandInfo info = command.getInfo();
        final HyriCommandType type = info.getType();

        return new Command(info.getName(), info.getDescription(), "", info.getAliases()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (type.getCheck().apply(sender)) {
                    final HyriPermission permission = info.getPermission();

                    if (permission != null) {
                        if (sender instanceof Player) {
                            if (!HyriAPI.get().getPlayerManager().hasPermission(((Player) sender).getUniqueId(), info.getPermission())) {
                                sender.sendMessage(ChatColor.RED + HyriCommonMessages.DONT_HAVE_PERMISSION.getForSender(sender));
                                return true;
                            }
                        }
                    }

                    final HyriCommandContext ctx = new HyriCommandContext(sender, label, args);

                    command.handle(ctx);

                    final HyriCommandResult result = ctx.getResult();

                    if (result != null) {
                        if (result.getType() == HyriCommandResult.Type.ERROR) {
                            sender.sendMessage(result.getMessage());
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to be " + type.getDisplay() + " to execute this command!");
                }
                return true;
            }
        };
    }

}
