package fr.hyriode.hyrame.config;

import fr.hyriode.hyrame.config.handler.ConfigOptionHandler;

import java.lang.reflect.Field;

/**
 * Created by AstFaster
 * on 01/06/2022 at 14:58
 *
 * The config context is an object that represents a data to run during the process.<br>
 * It contains all information about the field to set and the {@linkplain ConfigOptionHandler handler} to use.
 */
public class ConfigContext {

    /** The option of the config field to set */
    private final ConfigOption configOption;
    /** The field to set */
    private final Field field;
    /** The handler that will be used to set the field's value */
    private final Class<? extends ConfigOptionHandler<?>> handlerClass;

    /** The handler instance. It might be <code>null</code> if the context has not been run yet */
    private ConfigOptionHandler<?> handler;

    /**
     * Constructor of {@link ConfigContext}
     *
     * @param configOption The option
     * @param field The field
     * @param handlerClass The handler
     */
    public ConfigContext(ConfigOption configOption, Field field, Class<? extends ConfigOptionHandler<?>> handlerClass) {
        this.configOption = configOption;
        this.field = field;
        this.handlerClass = handlerClass;
    }

    /**
     * Get the config option with all information about the field
     *
     * @return The {@linkplain ConfigOption config option}
     */
    public ConfigOption getConfigOption() {
        return this.configOption;
    }

    /**
     * Get the field related to the config option
     *
     * @return A {@link Field}; cannot be null
     */
    public Field getField() {
        return this.field;
    }

    /**
     * Get the handler type used to run the context
     *
     * @return The {@linkplain ConfigOptionHandler handler} class
     */
    Class<? extends ConfigOptionHandler<?>> getHandlerClass() {
        return this.handlerClass;
    }

    /**
     * Get the handler instance.<br>
     * This handler might be <code>null</code> if this context has not been run yet
     *
     * @return The {@linkplain ConfigOptionHandler handler} instance
     */
    public ConfigOptionHandler<?> getHandler() {
        return this.handler;
    }

    /**
     * Set the handler instance that is running the process
     *
     * @param handler The {@linkplain ConfigOptionHandler handler} instance
     */
    void setHandler(ConfigOptionHandler<?> handler) {
        this.handler = handler;
    }

}
