package fr.hyriode.hyrame.impl.config;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.ConfigContext;
import fr.hyriode.hyrame.config.ConfigOption;
import fr.hyriode.hyrame.config.ConfigProcess;
import fr.hyriode.hyrame.config.IConfigManager;
import fr.hyriode.hyrame.config.handler.CBooleanHandler;
import fr.hyriode.hyrame.config.handler.CLocationHandler;
import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by AstFaster
 * on 01/06/2022 at 13:49
 */
public class ConfigManager implements IConfigManager {

    private final Map<Class<?>, Class<? extends ConfigOptionHandler<?>>> handlersClasses;
    private final List<ConfigProcess<?>> processes;

    private final IHyrame hyrame;

    public ConfigManager(IHyrame hyrame, JavaPlugin plugin) {
        this.hyrame = hyrame;
        this.handlersClasses = new HashMap<>();
        this.processes = new ArrayList<>();

        this.registerOptionHandler(LocationWrapper.class, CLocationHandler.class);
        this.registerOptionHandler(Boolean.class, CBooleanHandler.class);

        plugin.getServer().getPluginManager().registerEvents(new Handler(), plugin);
    }

    @Override
    public <T extends IConfig> ConfigProcess<T> initConfigProcess(Player player, Class<T> configClass) {
        if (this.getProcess(player.getUniqueId()) != null) {
            throw new RuntimeException(player.getName() + "(" + player.getUniqueId().toString() + ") is already running a process!");
        }

        try {
            final Constructor<T> emptyConstructor = configClass.getConstructor();
            final T config = emptyConstructor.newInstance();
            final ConfigProcess<T> process = new ConfigProcess<>(this.hyrame, player, config);

            this.loadFields(process, configClass.getFields());
            this.loadFields(process, configClass.getDeclaredFields());

            this.processes.add(process);

            return process;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Auto-configuration creation requires an empty constructor in the config class!");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFields(ConfigProcess<?> process, Field[] fields) {
        for (Field field : fields) {
            final Class<? extends ConfigOptionHandler<?>> handler = this.handlersClasses.get(Reflection.DataType.getReference(field.getType()));
            final ConfigOption option = field.getAnnotation(ConfigOption.class);

            if (option == null || handler == null) {
                continue;
            }

            process.addContext(new ConfigContext(option, field, handler));
        }
    }

    @Override
    public <T> void registerOptionHandler(Class<T> optionClass, Class<? extends ConfigOptionHandler<T>> handlerClass) {
        this.handlersClasses.put(Reflection.DataType.getReference(optionClass), handlerClass);
    }

    @Override
    public void unregisterOptionHandler(Class<?> optionClass) {
        this.handlersClasses.remove(Reflection.DataType.getReference(optionClass));
    }

    @Override
    public Map<Class<?>, Class<? extends ConfigOptionHandler<?>>> getHandlersClasses() {
        return this.handlersClasses;
    }

    @Override
    public ConfigProcess<?> getProcess(UUID player) {
        for (ConfigProcess<?> process : this.processes) {
            if (process.getPlayer().getUniqueId().equals(player)) {
                return process;
            }
        }
        return null;
    }

    @Override
    public List<ConfigProcess<?>> getProcesses() {
        return this.processes;
    }

    private class Handler implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onQuit(PlayerQuitEvent event) {
            final Player player = event.getPlayer();
            final ConfigProcess<?> process = getProcess(player.getUniqueId());

            if (process == null) {
                return;
            }

            processes.remove(process);
        }

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            final Player player = (Player) event.getWhoClicked();
            final ConfigProcess<?> process = getProcess(player.getUniqueId());

            if (process == null) {
                return;
            }

            event.setCancelled(true);
        }

    }

}
