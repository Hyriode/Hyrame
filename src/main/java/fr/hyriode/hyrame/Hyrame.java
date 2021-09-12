package fr.hyriode.hyrame;

import fr.hyriode.hyrame.command.HyriCommandManager;
import fr.hyriode.hyrame.game.HyriGameManager;
import fr.hyriode.hyrame.language.LanguageManager;
import fr.hyriode.hyrame.listener.HyriListenerManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Hyrame {

    private final HyriGameManager gameManager;

    private final LanguageManager languageManager;

    private final HyriListenerManager listenerManager;
    private final HyriCommandManager commandManager;

    private final IPluginProvider pluginProvider;

    private final Logger logger;

    public Hyrame(IPluginProvider pluginProvider) {
        this.pluginProvider = pluginProvider;
        this.logger = this.pluginProvider.getLogger();
        this.commandManager = new HyriCommandManager(this);
        this.listenerManager = new HyriListenerManager(this);
        this.languageManager = new LanguageManager(this);
        this.gameManager = new HyriGameManager(this);

        this.registerListeners();
    }

    private void registerListeners() {
        this.listenerManager.autoRegisterListener("fr.hyriode.hyrame");
    }

    public void log(Level level, String msg) {
        this.logger.log(level, msg);
    }

    public void log(String msg) {
        this.log(Level.INFO, msg);
    }

    public IPluginProvider getPluginProvider() {
        return this.pluginProvider;
    }

    public JavaPlugin getPlugin() {
        return this.pluginProvider.getPlugin();
    }

    public HyriCommandManager getCommandManager() {
        return this.commandManager;
    }

    public HyriListenerManager getListenerManager() {
        return this.listenerManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public HyriGameManager getGameManager() {
        return this.gameManager;
    }

    public Logger getLogger() {
        return this.logger;
    }

}

