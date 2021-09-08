package fr.hyriode.hyrame.command;


import com.google.common.reflect.ClassPath;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class HyriCommandManager {

    private final CommandMap commandMap;

    private final IPluginProvider pluginProvider;

    private final Hyrame hyrame;

    public HyriCommandManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.pluginProvider = this.hyrame.getPluginProvider();
        this.commandMap = this.getCommandMap();

        this.autoRegisterCommands();
    }

    private void autoRegisterCommands() {
        this.hyrame.log("Searching for commands in packages provided...");

        for (String commandsPackage : this.pluginProvider.getCommandsPackages()) {
            this.hyrame.log("Searching for commands in package '" + commandsPackage + "'...");

            this.autoRegisterCommand(commandsPackage);
        }
    }

    public void autoRegisterCommand(String commandsPackage) {
        try {
            final ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(commandsPackage)) {
                final Class<?> clazz = Class.forName(classInfo.getName());

                if(this.checkSuperClass(clazz)) {
                    if(this.hasParameterLessConstructor(clazz)) {
                        final HyriCommand command = (HyriCommand) clazz.newInstance();

                        command.setPluginSupplier(this.pluginProvider::getPlugin);
                        command.addArguments();

                        this.hyrame.log("Registering '" + command.getName() + "' command");

                        this.commandMap.register(command.getName(), command);
                    } else {
                        this.hyrame.log(clazz.getSimpleName() + " inherit of " + HyriCommand.class.getSimpleName() + " but doesn't have a parameter less constructor!");
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
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

    private boolean hasParameterLessConstructor(Class<?> clazz) {
        for(Constructor<?> constructor : clazz.getConstructors()) {
            if(constructor.getParameterCount() == 0) {
                return true;
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

