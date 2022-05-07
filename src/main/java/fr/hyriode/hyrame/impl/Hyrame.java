package fr.hyriode.hyrame.impl;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.IHyrameConfiguration;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import fr.hyriode.hyrame.command.IHyriCommandManager;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.IHyriGameManager;
import fr.hyriode.hyrame.impl.chat.HyriChatManager;
import fr.hyriode.hyrame.impl.command.HyriCommandBlocker;
import fr.hyriode.hyrame.impl.command.HyriCommandManager;
import fr.hyriode.hyrame.impl.game.HyriGameManager;
import fr.hyriode.hyrame.impl.inventory.HyriInventoryManager;
import fr.hyriode.hyrame.impl.item.HyriItemManager;
import fr.hyriode.hyrame.impl.join.HyriJoinHandler;
import fr.hyriode.hyrame.impl.language.HyriLanguageManager;
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
import fr.hyriode.hyrame.inventory.IHyriInventoryManager;
import fr.hyriode.hyrame.item.IHyriItemManager;
import fr.hyriode.hyrame.item.enchant.HyriEnchant;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.listener.IHyriListenerManager;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.packet.IPacketInterceptor;
import fr.hyriode.hyrame.placeholder.PlaceholderAPI;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.scanner.IHyriScanner;
import fr.hyriode.hyrame.scoreboard.IHyriScoreboardManager;
import fr.hyriode.hyrame.signgui.SignGUIManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

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
    private final IHyriInventoryManager inventoryManager;
    private final IHyriGameManager gameManager;
    private final HyriChatManager chatManager;
    private final PacketInterceptor packetInterceptor;

    private final NicknameModule nicknameModule;
    private final FriendModule friendModule;
    private final PrivateMessageModule privateMessageModule;
    private final PartyModule partyModule;
    private final LevelingModule levelingModule;

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
        this.inventoryManager = new HyriInventoryManager();
        this.gameManager = new HyriGameManager(this);
        this.chatManager = new HyriChatManager(this);
        this.packetInterceptor = new PacketInterceptor();
        this.pluginProviders = new ArrayList<>();
        this.commandBlocker = new HyriCommandBlocker();
        this.tabManager = new HyriTabManager(this);
        this.nicknameModule = new NicknameModule(this.tabManager, plugin);
        this.friendModule = new FriendModule();
        this.privateMessageModule = new PrivateMessageModule();
        this.partyModule = new PartyModule();
        this.levelingModule = new LevelingModule();

        IHyriLanguageManager.Provider.registerInstance(() -> this.languageManager);
        PlaceholderAPI.registerInstance(new PlaceholderImpl());
        PlaceholderRegistry.registerPlaceholders(this);
        HyriEnchant.register();

        BossBarManager.init(this.plugin);
        NPCManager.init(this.plugin, "npcs:");
        new SignGUIManager(this, this.plugin);

        HyriAPI.get().getServer().setState(IHyriServer.State.READY);
        HyriAPI.get().getServer().setSlots(50);

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
        this.languageManager.loadLanguagesMessages(pluginProvider);
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
