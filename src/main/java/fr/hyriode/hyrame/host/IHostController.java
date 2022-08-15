package fr.hyriode.hyrame.host;

import fr.hyriode.api.host.IHostConfig;
import fr.hyriode.hylios.api.host.HostData;
import fr.hyriode.hyrame.host.option.HostOption;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by AstFaster
 * on 31/07/2022 at 11:04
 */
public interface IHostController {

    /**
     * Enable the host controller.<br>
     * It might throw a {@link IllegalStateException}
     */
    void enable();

    /**
     * Apply a given configuration to the host
     *
     * @param config The {@linkplain IHostConfig config} to apply
     */
    void applyConfig(IHostConfig config);

    /**
     * Send a message as the host
     *
     * @param player The {@link Player} that sent the message
     * @param message The message that will be sent
     */
    void sendHostMessage(Player player, String message);

    /**
     * Find an option from its name
     *
     * @param name The name of the config to find
     * @return The found {@linkplain HostOption option}; or <code>null</code> if no option has been found
     */
    HostOption<?> findOption(String name);

    /**
     * Reset all the options registered in the host
     */
    void resetOptions();

    /**
     * Get all the options registered in the host
     *
     * @return A list of {@linkplain HostOption option}
     */
    List<HostOption<?>> getOptions();

    /**
     * Add a main category to the host
     *
     * @param slot The slot where the category will be on the {@link fr.hyriode.hyrame.host.gui.HostGUI}
     * @param category The {@linkplain HostCategory category} to add
     */
    void addCategory(int slot, HostCategory category);

    /**
     * Remove a main category from the host
     *
     * @param category The {@linkplain HostCategory category} to remove
     */
    void removeCategory(HostCategory category);

    /**
     * Get a category from its name
     *
     * @param name The name of the category to find
     * @return The {@linkplain HostCategory category} found; or <code>null</code> if no category has been found with the given name
     */
    HostCategory getCategory(String name);

    /**
     * Get all the main registered categories
     *
     * @return A map of {@linkplain HostCategory category} link to their slot in {@link fr.hyriode.hyrame.host.gui.HostGUI}
     */
    Map<Integer, HostCategory> getCategories();

    /**
     * Get the current applied configuration on the host
     *
     * @return The current {@link IHostConfig}; or <code>null</code> if no config has been applied
     */
    IHostConfig getCurrentConfig();

    /**
     * Get the data of the host
     *
     * @return A {@link HostData} object
     */
    HostData getHostData();

    /**
     * Start the timer between each advertisement
     */
    void startAdvertTimer();

    /**
     * Get the current value of the timer between each advertisement
     *
     * @return A time (in seconds)
     */
    long getAdvertTimer();

}
