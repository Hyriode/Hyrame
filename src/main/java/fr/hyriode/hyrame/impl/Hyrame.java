package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.command.HyriCommandBlocker;
import fr.hyriode.hyrame.impl.command.HyriCommandManager;
import fr.hyriode.hyrame.impl.configuration.HyrameConfiguration;
import fr.hyriode.hyrame.impl.game.HyriGameManager;
import fr.hyriode.hyrame.impl.item.HyriItemManager;
import fr.hyriode.hyrame.impl.language.HyriLanguageManager;
import fr.hyriode.hyrame.impl.listener.HyriListenerManager;
import fr.hyriode.hyrame.impl.scanner.HyriScanner;
import fr.hyriode.hyrame.impl.tab.HyriTabManager;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class Hyrame implements IHyrame {

    private final List<IPluginProvider> pluginProviders;

    private final IHyriScanner scanner;
    private final IHyriLanguageManager languageManager;
    private final IHyriListenerManager listenerManager;
    private final IHyriCommandManager commandManager;
    private final HyriItemManager itemManager;
    private final IHyriGameManager gameManager;

    private final HyriTabManager tabManager;
    private final HyriCommandBlocker commandBlocker;

    private final HyrameConfiguration configuration;

    private final HyramePlugin plugin;

    private static Logger logger;

    public Hyrame(HyramePlugin plugin) {
        logger = plugin.getLogger();

        this.plugin = plugin;
        this.configuration = new HyrameConfiguration(this.plugin);
        this.commandBlocker = new HyriCommandBlocker();
        this.tabManager = new HyriTabManager(this);
        this.scanner = new HyriScanner();
        this.languageManager = new HyriLanguageManager();
        this.listenerManager = new HyriListenerManager(this);
        this.commandManager = new HyriCommandManager(this);
        this.itemManager = new HyriItemManager(this);
        this.gameManager = new HyriGameManager(this);
        this.pluginProviders = new ArrayList<>();
    }

    @Override
    public void load(IPluginProvider pluginProvider) {
        this.pluginProviders.add(pluginProvider);

        this.itemManager.registerItems(pluginProvider);
        this.languageManager.loadLanguagesMessages(pluginProvider);
        this.listenerManager.registerListeners(pluginProvider);
        this.commandManager.registerCommands(pluginProvider);
    }

    void disable() {
        log("Stopping " + NAME + "... Bye!");

        final HyriGame<?> game = this.gameManager.getCurrentGame();

        if (game != null) {
            this.gameManager.unregisterGame(game);
        }
    }

    @Override
    public boolean isLoaded(IPluginProvider pluginProvider) {
        return this.pluginProviders.contains(pluginProvider);
    }

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public static String formatPluginProviderName(IPluginProvider pluginProvider) {
        return " | " + pluginProvider.getClass().getSimpleName() + " ";
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public HyrameConfiguration getConfiguration() {
        return this.configuration;
    }

    public HyriCommandBlocker getCommandBlocker() {
        return this.commandBlocker;
    }

    public HyriTabManager getTabManager() {
        return this.tabManager;
    }

    @Override
    public IHyriScanner getScanner() {
        return this.scanner;
    }

    @Override
    public IHyriLanguageManager getLanguageManager() {
        return this.languageManager;
    }

    @Override
    public IHyriListenerManager getListenerManager() {
        return this.listenerManager;
    }

    @Override
    public IHyriCommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public HyriItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public IHyriGameManager getGameManager() {
        return this.gameManager;
    }

    public HyramePlugin getPlugin() {
        return this.plugin;
    }

    public List<IPluginProvider> getPluginProviders() {
        return this.pluginProviders;
    }

}
