package fr.hyriode.hyrame.impl;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import fr.hyriode.hyrame.command.ICommandManager;
import fr.hyriode.hyrame.enchantment.HyriEnchant;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.impl.chat.ChatManager;
import fr.hyriode.hyrame.impl.command.CommandManager;
import fr.hyriode.hyrame.impl.game.HyriGameManager;
import fr.hyriode.hyrame.impl.hologram.HologramHandler;
import fr.hyriode.hyrame.impl.host.HostController;
import fr.hyriode.hyrame.impl.inventory.HyriInventoryManager;
import fr.hyriode.hyrame.impl.item.ItemManager;
import fr.hyriode.hyrame.impl.join.JoinHandler;
import fr.hyriode.hyrame.impl.language.LanguageLoader;
import fr.hyriode.hyrame.impl.listener.ListenerManager;
import fr.hyriode.hyrame.impl.packet.PacketInterceptor;
import fr.hyriode.hyrame.impl.placeholder.PlaceholderImpl;
import fr.hyriode.hyrame.impl.placeholder.handler.PlaceholderRegistry;
import fr.hyriode.hyrame.impl.scanner.HyriScanner;
import fr.hyriode.hyrame.impl.scoreboard.ScoreboardManager;
import fr.hyriode.hyrame.impl.tablist.TabListManager;
import fr.hyriode.hyrame.impl.world.WorldProvider;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IItemManager;
import fr.hyriode.hyrame.language.ILanguageLoader;
import fr.hyriode.hyrame.listener.IListenerManager;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IScoreboardManager;
import fr.hyriode.hyrame.signgui.SignGUIManager;
import fr.hyriode.hyrame.world.IWorldProvider;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class Hyrame implements IHyrame {

    private final List<IPluginProvider> pluginProviders;

    private final IHyrameConfiguration configuration;
    private final IHyriScanner scanner;
    private final ILanguageLoader languageLoader;
    private final IListenerManager listenerManager;
    private final ICommandManager commandManager;
    private final IItemManager itemManager;
    private final IScoreboardManager scoreboardManager;
    private final IHyriInventoryManager inventoryManager;
    private final IHyriGameManager gameManager;
    private final ChatManager chatManager;
    private final TabListManager tabListManager;
    private final PacketInterceptor packetInterceptor;
    private final IHostController hostController;
    private final IWorldProvider worldProvider;

    private final HyramePlugin plugin;

    public Hyrame(HyramePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new HyrameConfiguration();
        this.scanner = new HyriScanner();
        this.languageLoader = new LanguageLoader();
        this.listenerManager = new ListenerManager(this);
        this.commandManager = new CommandManager(this);
        this.itemManager = new ItemManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.inventoryManager = new HyriInventoryManager();
        this.gameManager = new HyriGameManager(this);
        this.chatManager = new ChatManager(this);
        this.tabListManager = new TabListManager(this, plugin);
        this.tabListManager.enable();
        this.packetInterceptor = new PacketInterceptor();
        this.hostController = new HostController(this);
        this.worldProvider = new WorldProvider();
        this.pluginProviders = new ArrayList<>();

        PlaceholderAPI.register(new PlaceholderImpl());
        PlaceholderRegistry.registerPlaceholders(this);
        HyriEnchant.register();
        HologramHandler.init(plugin, this);
        BossBarManager.init(this.plugin);
        new SignGUIManager(this, this.plugin);

        HyriAPI.get().getLanguageManager().registerAdapter(HyriGamePlayer.class, (message, gamePlayer) -> message.getValue(gamePlayer.getUniqueId()));
        HyriAPI.get().getJoinManager().registerHandler(10, new JoinHandler(this));

    }

    void disable() {
        HyrameLogger.log("Stopping " + NAME + "... Bye!");

        final HyriGame<?> game = this.gameManager.getCurrentGame();

        if (game != null) {
            this.gameManager.unregisterGame(game);
        }

        this.packetInterceptor.stopIntercepting();
    }

    @Override
    public void load(IPluginProvider pluginProvider) {
        this.pluginProviders.add(pluginProvider);

        this.languageLoader.loadLanguages(pluginProvider);
        this.itemManager.registerItems(pluginProvider);
        this.listenerManager.registerListeners(pluginProvider);
        this.commandManager.registerCommands(pluginProvider);
    }

    @Override
    public boolean isLoaded(IPluginProvider pluginProvider) {
        return this.pluginProviders.contains(pluginProvider);
    }

    public static String formatPluginProviderName(IPluginProvider pluginProvider) {
        return " | " + ChatColor.DARK_PURPLE + pluginProvider.getClass().getSimpleName();
    }

    @Override
    public HyramePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public IHyrameConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public IHyriScanner getScanner() {
        return this.scanner;
    }

    @Override
    public ILanguageLoader getLanguageLoader() {
        return this.languageLoader;
    }

    @Override
    public IListenerManager getListenerManager() {
        return this.listenerManager;
    }

    @Override
    public ICommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public IItemManager getItemManager() {
        return this.itemManager;
    }

    @Override
    public IScoreboardManager getScoreboardManager() {
        return this.scoreboardManager;
    }

    @Override
    public IHyriInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public IHyriGameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public ChatManager getChatManager() {
        return this.chatManager;
    }

    @Override
    public TabListManager getTabListManager() {
        return this.tabListManager;
    }

    @Override
    public PacketInterceptor getPacketInterceptor() {
        return this.packetInterceptor;
    }

    @Override
    public IHostController getHostController() {
        return this.hostController;
    }

    @Override
    public IWorldProvider getWorldProvider() {
        return this.worldProvider;
    }

    public List<IPluginProvider> getPluginProviders() {
        return this.pluginProviders;
    }

}
