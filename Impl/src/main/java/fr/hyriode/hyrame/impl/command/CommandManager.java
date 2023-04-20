package fr.hyriode.hyrame.impl.command;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 19:59
 */
public class CommandManager implements ICommandManager {

    private final Map<Class<?>, HyriCommand<?>> commands;

    private final CommandMap commandMap;
    private final ICommandBlocker commandBlocker;

    private final Hyrame hyrame;

    public CommandManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.commandMap = (CommandMap) Reflection.invokeField(Bukkit.getServer(), "commandMap");
        this.commands = new HashMap<>();
        this.commandBlocker = new CommandBlocker();
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
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends HyriCommand<?>> T getCommand(Class<T> commandClass) {
        return commandClass.cast(this.commands.get(commandClass));
    }

    @Override
    public List<HyriCommand<?>> getCommands() {
        return new ArrayList<>(this.commands.values());
    }

    private Command createCommand(HyriCommand<?> command) {
        final CommandInfo info = command.getInfo();

        return new Command(info.getName(), info.getDescription(), "", info.getAliases()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                final Player player = (Player) sender;
                final Predicate<IHyriPlayer> permission = info.getPermission();

                if (permission != null) {
                    final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());

                    if (!permission.test(account)) {
                        sender.sendMessage(HyrameMessage.PERMISSION_ERROR.asString(account));
                        return true;
                    }
                }

                final CommandContext ctx = new CommandContext(player, label, args, info);

                if (info.isAsynchronous()) {
                    ThreadUtil.ASYNC_EXECUTOR.execute(() -> command.handle(ctx));
                } else {
                    command.handle(ctx);
                }
                return true;
            }
        };
    }

    @Override
    public ICommandBlocker getCommandBlocker() {
        return this.commandBlocker;
    }

}
