package fr.hyriode.hyrame.listener;

import com.google.common.reflect.ClassPath;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 18:15
 */
public class HyriListenerManager {

    private final IPluginProvider pluginProvider;

    public HyriListenerManager(Hyrame hyrame) {
        this.pluginProvider = hyrame.getPluginProvider();

        this.autoRegisterCommands();
    }

    private void autoRegisterCommands() {
        Hyrame.log("Searching for listeners in packages provided...");

        for (String listenersPackage : this.pluginProvider.getListenersPackages()) {
            Hyrame.log("Searching for listeners in package '" + listenersPackage + "'...");

            this.autoRegisterListener(listenersPackage);
        }
    }

    public void autoRegisterListener(String listenersPackage) {
        try {
            final ClassPath classPath = ClassPath.from(this.pluginProvider.getClass().getClassLoader());

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(listenersPackage)) {
                final Class<?> clazz = Class.forName(classInfo.getName());

                if(this.checkSuperClass(clazz)) {
                    if(this.hasSupplierParameterConstructor(clazz)) {
                        final Supplier<? extends JavaPlugin> pluginSupplier = this.pluginProvider::getPlugin;
                        final HyriListener listener = (HyriListener) clazz.getConstructor(Supplier.class).newInstance(pluginSupplier);

                        Hyrame.log("Registering '" + clazz.getName() + "' listener");

                        this.pluginProvider.getPlugin().getServer().getPluginManager().registerEvents(listener, this.pluginProvider.getPlugin());
                    } else {
                        Hyrame.log(clazz.getSimpleName() + " inherit of " + HyriListener.class.getSimpleName() + " but doesn't have a constructor with only plugin supplier parameter!");
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
            if(superClass.equals(HyriListener.class)) {
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

}
