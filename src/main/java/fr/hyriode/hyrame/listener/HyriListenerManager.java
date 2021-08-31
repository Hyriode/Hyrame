package fr.hyriode.hyrame.listener;

import com.google.common.reflect.ClassPath;
import fr.hyriode.hyrame.Hyrame;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.plugin.IPluginProvider;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/08/2021 at 18:15
 */
public class HyriListenerManager {

    private final IPluginProvider<?> pluginProvider;

    private final Hyrame hyrame;

    public HyriListenerManager(Hyrame hyrame) {
        this.hyrame = hyrame;
        this.pluginProvider = this.hyrame.getPluginProvider();

        this.autoRegisterCommands();
    }

    private void autoRegisterCommands() {
        this.hyrame.log("Searching for listeners in packages provided...");

        for (String listenersPackage : this.pluginProvider.getListenersPackages()) {
            this.hyrame.log("Searching for listeners in package '" + listenersPackage + "'...");

            this.autoRegisterListener(listenersPackage);
        }
    }

    public void autoRegisterListener(String listenersPackage) {
        try {
            final ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());

            for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(listenersPackage)) {
                final Class<?> clazz = Class.forName(classInfo.getName());

                if(this.checkSuperClass(clazz)) {
                    if(this.hasParameterLessConstructor(clazz)) {
                        final HyriListener listener = (HyriListener) clazz.newInstance();

                        listener.setPluginSupplier(this.pluginProvider::getPlugin);

                        this.hyrame.log("Registering '" + clazz.getName() + "' listener");

                        this.pluginProvider.getPlugin().getServer().getPluginManager().registerEvents(listener, this.pluginProvider.getPlugin());
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
            if(superClass.equals(HyriListener.class)) {
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

}
