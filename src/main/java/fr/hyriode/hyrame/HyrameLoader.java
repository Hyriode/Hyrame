package fr.hyriode.hyrame;

import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.function.Supplier;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyrameLoader {

    /** {@link IHyrame} instance */
    private static IHyrame hyrame;

    /**
     * Load a plugin who wants to use Hyrame by giving its {@link IPluginProvider}
     *
     * @param pluginProvider {@link IPluginProvider} to load
     * @return {@link IHyrame} instance
     */
    public static IHyrame load(IPluginProvider pluginProvider) {
        if (hyrame == null) {
            throw new HyrameNotRegisteredException();
        }

        if (hyrame.isLoaded(pluginProvider)) {
            throw new IllegalStateException("A plugin provider for '" + pluginProvider.getClass().getName() + "' is already registered!");
        }

        hyrame.load(pluginProvider);

        return hyrame;
    }

    /**
     * Get the current registered {@link IHyrame} instance
     *
     * @return A {@link IHyrame} instance; or <code>null</code> if no implementation has been registered
     */
    public static IHyrame getHyrame() {
        if (hyrame == null) {
            throw new HyrameNotRegisteredException();
        }

        return hyrame;
    }

    /**
     * Register an implementation of Hyrame
     *
     * @param hyrameSupplier New {@link IHyrame} implementation
     * @param <T> The type of the {@link IHyrame} instance to return
     * @return The registered {@link IHyrame} instance
     */
    public static <T extends IHyrame> T register(Supplier<T> hyrameSupplier) {
        final ChatColor color = ChatColor.DARK_PURPLE;
        final ConsoleCommandSender sender = Bukkit.getConsoleSender();

        sender.sendMessage(color + "  _  _                         ");
        sender.sendMessage(color + " | || |_  _ _ _ __ _ _ __  ___ ");
        sender.sendMessage(color + " | __ | || | '_/ _` | '  \\/ -_)");
        sender.sendMessage(color + " |_||_|\\_, |_| \\__,_|_|_|_\\___|");
        sender.sendMessage(color + "       |__/                    ");

        final T hyrame = hyrameSupplier.get();

        HyrameLoader.hyrame = hyrame;

        sender.sendMessage(color + "[" + IHyrame.NAME + "] " + ChatColor.RESET + "Registered '" + hyrame.getClass().getName() + "' as an implementation of " + IHyrame.NAME + ".");

        return hyrame;
    }

    private static final class HyrameNotRegisteredException extends IllegalStateException {

        public HyrameNotRegisteredException() {
            super(IHyrame.NAME + " has not been registered yet!\n" +
                    "\n        These might be the following reasons:\n" +
                    "          1) The " + IHyrame.NAME + " plugin is not installed or it failed while enabling\n" +
                    "          2) Your plugin is loading before " + IHyrame.NAME + "\n" +
                    "          3) No implementation of " + IHyrame.NAME + " exists\n");
        }

    }

}
