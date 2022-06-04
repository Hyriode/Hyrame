package fr.hyriode.hyrame.config;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hystia.api.config.IConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 01/06/2022 at 13:58
 */
public class ConfigProcess<T extends IConfig> {

    /** The current running context */
    private ConfigContext current;

    /** The callback to call after the process execution */
    private Consumer<T> finishCallback;

    /** Whether the process is started or not */
    private boolean started;
    /** The initial size of the process (all contexts to run) */
    private int initialSize;

    /** The Hyrame instance */
    private final IHyrame hyrame;
    /** The player that started the process */
    private final Player player;
    /** The configuration object to edit */
    private final T config;
    /** The queue with all {@link ConfigContext} to run */
    private final Queue<ConfigContext> contexts;

    /**
     * Default constructor of a {@linkplain ConfigProcess config process}
     *
     * @param hyrame The Hyrame instance
     * @param player The player running the process
     * @param config The configuration to create during the process
     */
    public ConfigProcess(IHyrame hyrame, Player player, T config) {
        this.hyrame = hyrame;
        this.player = player;
        this.config = config;
        this.contexts = new ConcurrentLinkedQueue<>();
    }

    /**
     * Start the configuration process
     */
    public void start() {
        if (this.started) {
            throw new IllegalStateException("Config process has already started!");
        }

        this.started = true;

        this.initialSize = this.contexts.size();

        this.player.sendMessage(HyriLanguageMessage.get("message.config.start").getForPlayer(this.player).replaceAll("%line%", Symbols.HYPHENS_LINE));

        this.next();
    }

    /**
     * Switch to the next context to run
     */
    private void next() {
        this.current = this.contexts.poll();

        if (this.current != null) {
            try {
                final ConfigOption configOption = this.current.getConfigOption();
                final Constructor<? extends ConfigOptionHandler<?>> constructor = this.current.getHandlerClass().getConstructor(IHyrame.class, ConfigProcess.class);

                constructor.setAccessible(true);

                this.player.sendMessage(HyriLanguageMessage.get("message.config.new-value").getForPlayer(player)
                        .replace("%name%", HyriLanguageMessage.get(configOption.displayNameKey()).getForPlayer(this.player))
                        .replace("%id%", configOption.id())
                        .replace("%description%", HyriLanguageMessage.get(configOption.descriptionKey()).getForPlayer(this.player))
                        .replaceAll("%line%", Symbols.HYPHENS_LINE));

                final ConfigOptionHandler<?> handler = constructor.newInstance(this.hyrame, this);

                this.current.setHandler(handler);

                handler.handle();
                handler.getCompletion().whenComplete((BiConsumer<Object, Throwable>) (object, throwable) -> {
                    Title.sendTitle(this.player, ChatColor.GREEN + Symbols.TICK_BOLD, "", 2, 10, 2);

                    this.write(this.current, object);
                    this.next();
                });
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.finishCallback.accept(this.config);
        }
    }

    /**
     * Write a value for a context's field
     *
     * @param context The context
     * @param value The value to write
     */
    private void write(ConfigContext context, Object value) {
        try {
            final Field field = context.getField();

            if (field == null) {
                return;
            }

            field.setAccessible(true);
            field.set(this.config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the finish callback that will be run when the process will end
     *
     * @param finishCallback The callback
     * @return This {@linkplain ConfigProcess process} instance
     */
    public ConfigProcess<T> withFinishCallback(Consumer<T> finishCallback) {
        this.finishCallback = finishCallback;
        return this;
    }

    /**
     * Get the player that started the process
     *
     * @return A {@link Player}
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the current running context.<br>
     * It can be null if the process has ended
     *
     * @return A {@link ConfigContext}
     */
    public ConfigContext current() {
        return this.current;
    }

    /**
     * Add a context to run during the process.<br>
     *
     * @throws IllegalStateException If the process has already started it will throw an exception
     * @param context The {@link ConfigContext} to add
     */
    public void addContext(ConfigContext context) {
        if (this.started) {
            throw new IllegalStateException("Cannot add more context to configure after the process being started!");
        }

        this.contexts.add(context);
    }

    /**
     * Get all the contexts that will be run during the process.<br>
     * This queue change each time a context is run because it will remove it.
     *
     * @return A queue of {@link ConfigContext}
     */
    public Queue<ConfigContext> getContexts() {
        return this.contexts;
    }

    /**
     * Check whether the process has started or not
     *
     * @return <code>true</code> if yes
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * Get the initial size of the process
     *
     * @return A number representing the size
     */
    public int initialSize() {
        return this.initialSize;
    }
    
}
