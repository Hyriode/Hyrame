package fr.hyriode.hyrame.commands;


import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandManager {

    private JavaPlugin plugin;
    private CommandMap commandMap;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandMap = this.getCommandMap();
        this.autoRegister();
    }


    private void autoRegister() {
        try {
            ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive("fr.hyriode.hyrame")) {

                final Class<?> clazz = Class.forName(classInfo.getName());

                if(this.checkSuperClass(clazz)) {
                    if(this.hasParameterLessConstructor(clazz)) {
                        final HyriCommand command = (HyriCommand) clazz.newInstance();
                        command.setPlugin(this.plugin);

                        this.commandMap.register(command.getName(), command);
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


