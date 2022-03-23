package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import fr.hyriode.hyrame.chat.HyriDefaultChatHandler;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.command.HyriCommandBlocker;
import fr.hyriode.hyrame.impl.command.HyriCommandManager;
import fr.hyriode.hyrame.impl.game.HyriGameManager;
import fr.hyriode.hyrame.impl.item.HyriItemManager;
import fr.hyriode.hyrame.impl.language.HyriLanguageManager;
import fr.hyriode.hyrame.impl.listener.HyriListenerManager;
import fr.hyriode.hyrame.impl.scanner.HyriScanner;
import fr.hyriode.hyrame.impl.scoreboard.HyriScoreboardManager;
import fr.hyriode.hyrame.impl.tab.HyriTabManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.item.enchant.HyriEnchant;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.signgui.SignGUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class Hyrame implements IHyrame {

    private final HyriTabManager tabManager;
    private final HyriCommandBlocker commandBlocker;

    private final List<IPluginProvider> pluginProviders;

    private final IHyrameConfiguration configuration;
    private final IHyriScanner scanner;
    private final IHyriLanguageManager languageManager;
    private final IHyriListenerManager listenerManager;
    private final IHyriCommandManager commandManager;
    private final IHyriItemManager itemManager;
    private final IHyriScoreboardManager scoreboardManager;
    private final IHyriGameManager gameManager;
    private IHyriChatHandler chatHandler;

    private final HyramePlugin plugin;

    public Hyrame(HyramePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new HyrameConfiguration(this);
        this.scanner = new HyriScanner();
        this.languageManager = new HyriLanguageManager(this);
        this.listenerManager = new HyriListenerManager(this);
        this.commandManager = new HyriCommandManager(this);
        this.itemManager = new HyriItemManager(this);
        this.scoreboardManager = new HyriScoreboardManager();
        this.gameManager = new HyriGameManager(this);
        this.chatHandler = new HyriDefaultChatHandler();
        this.pluginProviders = new ArrayList<>();
        this.commandBlocker = new HyriCommandBlocker();
        this.tabManager = new HyriTabManager(this);

        HyriEnchant.register();

        new BossBarManager(plugin);
        new NPCManager(plugin, "npcs:");
        new SignGUIManager();
    }

    void disable() {
        log("Stopping " + NAME + "... Bye!");

        final HyriGame<?> game = this.gameManager.getCurrentGame();

        if (game != null) {
            this.gameManager.unregisterGame(game);
        }
    }

    @Override
    public void load(IPluginProvider pluginProvider) {
        this.pluginProviders.add(pluginProvider);

        this.itemManager.registerItems(pluginProvider);
        this.languageManager.loadLanguagesMessages(pluginProvider);
        this.listenerManager.registerListeners(pluginProvider);
        this.commandManager.registerCommands(pluginProvider);
    }

    @Override
    public boolean isLoaded(IPluginProvider pluginProvider) {
        return this.pluginProviders.contains(pluginProvider);
    }

    public static void log(Level level, String message) {
        String prefix = ChatColor.DARK_PURPLE + "[" + NAME + "] ";

        if (level == Level.SEVERE) {
            prefix += ChatColor.RED;
        } else if (level == Level.WARNING) {
            prefix += ChatColor.YELLOW;
        } else {
            prefix += ChatColor.RESET;
        }

        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    public static void log(String msg) {
        log(Level.INFO, msg);
    }

    public static String formatPluginProviderName(IPluginProvider pluginProvider) {
        return " | " + ChatColor.DARK_PURPLE + pluginProvider.getClass().getSimpleName();
    }

    @Override
    public IHyrameConfiguration getConfiguration() {
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
    public IHyriItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public IHyriScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public IHyriGameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public IHyriChatHandler getChatHandler() {
        return this.chatHandler;
    }

    @Override
    public void setChatHandler(IHyriChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    public HyramePlugin getPlugin() {
        return this.plugin;
    }

    public List<IPluginProvider> getPluginProviders() {
        return this.pluginProviders;
    }

}
