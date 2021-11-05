package fr.hyriode.hyrame.command;


import com.google.common.reflect.ClassPath;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class HyriCommandManager {

    public static final String COMMAND_PREFIX = "hyricommand";

    private final CommandMap commandMap;

    private final IPluginProvider pluginProvider;

    public HyriCommandManager(Hyrame hyrame) {
        this.pluginProvider = hyrame.getPluginProvider();
        this.commandMap = this.getCommandMap();

        this.autoRegisterCommands();
    }

    private void autoRegisterCommands() {
        Hyrame.log("Searching for commands in packages provided...");

        for (String commandsPackage : this.pluginProvider.getCommandsPackages()) {
            Hyrame.log("Searching for commands in package '" + commandsPackage + "'...");

            this.autoRegisterCommand(commandsPackage);
        }
    }

    public void autoRegisterCommand(String commandsPackage) {
        try {
            final ClassPath classPath = ClassPath.from(this.pluginProvider.getClass().getClassLoader());

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(commandsPackage)) {
                final Class<?> clazz = Class.forName(classInfo.getName());

                if(this.checkSuperClass(clazz)) {
                    if(this.hasSupplierParameterConstructor(clazz)) {
                        final Supplier<? extends JavaPlugin> pluginSupplier = this.pluginProvider::getPlugin;
                        final HyriCommand command = (HyriCommand) clazz.getConstructor(Supplier.class).newInstance(pluginSupplier);

                        command.addArguments();

                        Hyrame.log("Registering '" + command.getName() + "' command");

                        this.commandMap.register(COMMAND_PREFIX, command);
                    } else {
                        Hyrame.log(clazz.getSimpleName() + " inherit of " + HyriCommand.class.getSimpleName() + " but doesn't have a constructor with only plugin supplier parameter!");
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private boolean checkSuperClass(Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null) {
            if(superClass.equals(HyriCommand.class)) {
                return true;
            }
            clazz = superClass;
            superClass = clazz.getSuperclass();
        }
        return false;
    }

    private boolean hasSupplierParameterConstructor(Class<?> clazz) {
        for(Constructor<?> constructor : clazz.getConstructors()) {
            if(constructor.getParameterCount() == 1) {
                if (constructor.getParameterTypes()[0].equals(Supplier.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private CommandMap getCommandMap() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
