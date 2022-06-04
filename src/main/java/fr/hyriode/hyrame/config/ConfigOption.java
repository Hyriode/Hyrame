package fr.hyriode.hyrame.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by AstFaster
 * on 01/06/2022 at 07:15
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

    /**
     * The identifier of the option
     *
     * @return An identifier
     */
    String id();

    /**
     * Get the display name language key of the option.<br>
     * This display name will be shown to the player
     *
     * @return A display name
     */
    String displayNameKey();

    /**
     * Get the description language key of the option.<br>
     * This description needs to describe as much as possible what the option is and in which way it needs to be configured.
     *
     * @return A description
     */
    String descriptionKey();

}
