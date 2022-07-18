package fr.hyriode.hyrame.impl;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.config.IConfigManager;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.chat.HyriChatManager;
import fr.hyriode.hyrame.impl.command.CommandBlocker;
import fr.hyriode.hyrame.impl.command.HyriCommandManager;
import fr.hyriode.hyrame.impl.config.ConfigManager;
import fr.hyriode.hyrame.impl.game.HyriGameManager;
import fr.hyriode.hyrame.impl.hologram.HologramHandler;
import fr.hyriode.hyrame.impl.inventory.HyriInventoryManager;
import fr.hyriode.hyrame.impl.item.HyriItemManager;
import fr.hyriode.hyrame.impl.join.HyriJoinHandler;
import fr.hyriode.hyrame.impl.language.LanguageLoader;
import fr.hyriode.hyrame.impl.listener.HyriListenerManager;
import fr.hyriode.hyrame.impl.module.chat.message.PrivateMessageModule;
import fr.hyriode.hyrame.impl.module.friend.FriendModule;
import fr.hyriode.hyrame.impl.module.leveling.LevelingModule;
import fr.hyriode.hyrame.impl.module.nickname.NicknameModule;
import fr.hyriode.hyrame.impl.module.party.PartyModule;
import fr.hyriode.hyrame.impl.packet.PacketInterceptor;
import fr.hyriode.hyrame.impl.placeholder.PlaceholderImpl;
import fr.hyriode.hyrame.impl.placeholder.handler.PlaceholderRegistry;
import fr.hyriode.hyrame.impl.scanner.HyriScanner;
import fr.hyriode.hyrame.impl.scoreboard.HyriScoreboardManager;
import fr.hyriode.hyrame.impl.tab.HyriTabManager;
import fr.hyriode.hyrame.impl.tablist.HyriTabListManager;
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.item.enchant.HyriEnchant;
import fr.hyriode.hyrame.language.ILanguageLoader;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.signgui.SignGUIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class Hyrame implements IHyrame {

    private final HyriTabManager tabManager;

    private final List<IPluginProvider> pluginProviders;

    private final IHyrameConfiguration configuration;
    private final IHyriScanner scanner;
    private final ILanguageLoader languageLoader;
    private final IHyriListenerManager listenerManager;
    private final IHyriCommandManager commandManager;
    private final IHyriItemManager itemManager;
    private final IHyriScoreboardManager scoreboardManager;
    private final IHyriInventoryManager inventoryManager;
    private final IHyriGameManager gameManager;
    private final HyriChatManager chatManager;
    private final HyriTabListManager tabListManager;
    private final IConfigManager configManager;
    private final PacketInterceptor packetInterceptor;

    private final NicknameModule nicknameModule;
    private final FriendModule friendModule;
    private final PrivateMessageModule privateMessageModule;
    private final PartyModule partyModule;
    private final LevelingModule levelingModule;

    private final HyramePlugin plugin;

    public Hyrame(HyramePlugin plugin) {
        this.plugin = plugin;
        this.configuration = new HyrameConfiguration();
        this.scanner = new HyriScanner();
        this.languageLoader = new LanguageLoader();
        this.listenerManager = new HyriListenerManager(this);
        this.commandManager = new HyriCommandManager(this);
        this.itemManager = new HyriItemManager(this);
        this.scoreboardManager = new HyriScoreboardManager();
        this.inventoryManager = new HyriInventoryManager();
        this.gameManager = new HyriGameManager(this);
        this.chatManager = new HyriChatManager(this);
        this.tabListManager = new HyriTabListManager(this, plugin);
        this.tabListManager.enable();
        this.configManager = new ConfigManager(this, plugin);
        this.packetInterceptor = new PacketInterceptor();
        this.pluginProviders = new ArrayList<>();
        this.tabManager = new HyriTabManager();
        this.nicknameModule = new NicknameModule(this, plugin);
        this.friendModule = new FriendModule();
        this.privateMessageModule = new PrivateMessageModule();
        this.partyModule = new PartyModule();
        this.levelingModule = new LevelingModule();

        PlaceholderAPI.register(new PlaceholderImpl());
        PlaceholderRegistry.registerPlaceholders(this);
        HyriEnchant.register();
        HologramHandler.init(plugin, this);
        BossBarManager.init(this.plugin);
        NPCManager.init(this.plugin, "npcs:");
        new SignGUIManager(this, this.plugin);

        HyriAPI.get().getLanguageManager().registerAdapter(HyriGamePlayer.class, (message, gamePlayer) -> message.getValue(gamePlayer.getUniqueId()));
        HyriAPI.get().getLanguageManager().registerAdapter(CommandSender.class, (message, sender) -> message.getValue(HyriLanguage.EN));

        HyriAPI.get().getServerManager().getJoinManager().registerHandler(10, new HyriJoinHandler(this));
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

        this.itemManager.registerItems(pluginProvider);
        this.languageLoader.loadLanguages(pluginProvider);
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
    public IHyrameConfiguration getConfiguration() {
        return this.configuration;
    }

    public HyriTabManager getTabManager() {
        return this.tabManager;
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
    public IHyriInventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    public IHyriGameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public HyriChatManager getChatManager() {
        return this.chatManager;
    }

    @Override
    public HyriTabListManager getTabListManager() {
        return this.tabListManager;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public PacketInterceptor getPacketInterceptor() {
        return this.packetInterceptor;
    }

    public HyramePlugin getPlugin() {
        return this.plugin;
    }

    public List<IPluginProvider> getPluginProviders() {
        return this.pluginProviders;
    }

    public NicknameModule getNicknameModule() {
        return this.nicknameModule;
    }

    public FriendModule getFriendModule() {
        return this.friendModule;
    }

    public PrivateMessageModule getPrivateMessageModule() {
        return this.privateMessageModule;
    }

    public PartyModule getPartyModule() {
        return this.partyModule;
    }

    public LevelingModule getLevelingModule() {
        return this.levelingModule;
    }

}
