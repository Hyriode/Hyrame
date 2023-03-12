package fr.hyriode.hyrame.game.waitingroom;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.leveling.NetworkLeveling;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.leaderboard.HyriLeaderboardDisplay;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.world.WorldChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 26/05/2022 at 20:05.<br>
 *
 * Represents the handler of waiting room in a game.<br>
 * It handles the teleportation of players, statistics NPC and leaderboards.
 */
public class HyriWaitingRoom {

    private static final int MIN_SLOT = 9;
    private static final int MAX_SLOT = 35;

    /** The size of the NPC's inventory  */
    protected int npcInventorySize = 5 * 9;

    /** <code>true</code> if the waiting room is set up. */
    protected boolean setup;

    /** <code>true</code> if the waiting has to be removed when the game starts. */
    protected boolean clearBlocks = true;

    /** The map of {@link NPC} linked by their {@link UUID} */
    private final Map<UUID, NPC> npcs = new HashMap<>();
    /** The different categories shown in the NPC GUI (linked by their slot) */
    private final Map<Integer, NPCCategory> npcCategories = new HashMap<>();

    /** The list of leaderboards to display in the waiting room */
    private final List<Leaderboard> leaderboards = new ArrayList<>();
    /** The displays of the leaderboards */
    private final List<HyriLeaderboardDisplay> leaderboardsDisplays = new ArrayList<>();

    /** The events handler instance */
    private final Handler handler = new Handler();

    protected final HyriGame<?> game;
    protected final JavaPlugin plugin;
    protected final ItemStack icon;
    protected final Config config;

    public HyriWaitingRoom(HyriGame<?> game, ItemStack icon, Config config) {
        this.game = game;
        this.plugin = this.game.getPlugin();
        this.icon = icon;
        this.config = config;
    }

    public HyriWaitingRoom(HyriGame<?> game, Material icon, Config config) {
        this(game, new ItemStack(icon), config);
    }

    /**
     * Set up the waiting room
     */
    public void setup() {
        if (this.setup) {
            throw new IllegalStateException("Waiting room has already been setup!");
        }

        HyrameLogger.log("Creating waiting room...");

        this.setup = true;

        this.plugin.getServer().getPluginManager().registerEvents(this.handler, this.plugin);
        HyriAPI.get().getEventBus().register(this.handler);

        for (HyriGamePlayer gamePlayer : this.game.getPlayers()) {
            if (!gamePlayer.isOnline()) {
                continue;
            }

            this.createNPC(gamePlayer.getPlayer());
        }

        final String leaderboardType = HyriAPI.get().getServer().getType() + "#" + HyriAPI.get().getServer().getGameType();

        for (Leaderboard leaderboard : this.leaderboards) {
            final String leaderboardName = leaderboard.getName();
            final LocationWrapper location = this.config.getLeaderboardsLocation().get(leaderboardName);

            if (location == null) {
                System.err.println("Couldn't find the location of '" + leaderboardName + "' leaderboard!");
                continue;
            }

            final HyriLeaderboardDisplay display = new HyriLeaderboardDisplay.Builder(this.plugin, leaderboardType, leaderboardName, location.asBukkit())
                    .withHeader(leaderboard.getDisplay())
                    .withUpdateTime(20L * 60L)
                    .withScoreFormatter(leaderboard.getScoreFormatter())
                    .build();

            display.show();

            this.leaderboardsDisplays.add(display);
        }
    }

    /**
     * Remove the waiting room
     */
    public void remove() {
        if (!this.setup) {
            throw new IllegalStateException("Waiting room cannot be remove because it has not been setup yet!");
        }

        HyrameLogger.log("Removing waiting room...");

        for (NPC npc : this.npcs.values()) {
            NPCManager.removeNPC(npc);
        }

        for (HyriLeaderboardDisplay display : this.leaderboardsDisplays) {
            display.hide();
        }

        if (this.clearBlocks) {
            final Cuboid cuboid = new Cuboid(this.config.getFirstPos().asBukkit(), this.config.getSecondPos().asBukkit());

            for (Block block : cuboid.getBlocks()) {
                block.setType(Material.AIR);
            }
        }

        HandlerList.unregisterAll(this.handler);
        HyriAPI.get().getEventBus().unregister(this.handler);

        this.setup = false;
    }

    /**
     * Teleport all {@linkplain HyriGamePlayer game players} to the spawn of the waiting room.
     */
    public void teleportPlayers() {
        for (HyriGamePlayer gamePlayer : this.game.getPlayers()) {
            gamePlayer.getPlayer().teleport(this.config.getSpawn().asBukkit());
        }
    }

    private NPC createNPC(Player player) {
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final List<String> headerLines = ListReplacer.replace(HyrameMessage.WAITING_ROOM_NPC_DISPLAY.asList(player), "%game%", this.game.getDisplayName()).list();
        final NPC npc = NPCManager.createNPC(this.config.getNPCLocation().asBukkit(), account.getName(), headerLines)
                .addPlayer(player)
                .setShowingToAll(false)
                .setTrackingPlayer(true)
                .setInteractCallback((rightClick, p) -> {
                    if (rightClick) {
                        if (this.game.getPlayer(p.getUniqueId()) == null) {
                            return;
                        }

                        new NPCGUI(p).open();
                    }
                });

        this.npcs.put(player.getUniqueId(), npc);

        return npc;
    }

    /**
     * Add a category in the NPC GUI
     *
     * @param slot The slot where the category will be displayed
     * @param category The category object
     * @return This {@link HyriWaitingRoom} instance
     */
    public HyriWaitingRoom addNPCCategory(int slot, NPCCategory category) {
        if (slot > MAX_SLOT || slot < MIN_SLOT) {
            throw new IllegalStateException("NPC category's slot need to be lower than " + MAX_SLOT + " and higher than " + MIN_SLOT + "!");
        }

        this.npcCategories.put(slot, category);
        return this;
    }

    /**
     * Add a leaderboard to display in the waiting room
     *
     * @param leaderboard The leaderboard to add
     * @return This {@link HyriWaitingRoom} instance
     */
    public HyriWaitingRoom addLeaderboard(@NotNull Leaderboard leaderboard) {
        this.leaderboards.add(leaderboard);
        return this;
    }

    /**
     * Get all the registered NPC GUI categories
     *
     * @return A map of {@link NPCCategory} linked by their slot
     */
    public Map<Integer, NPCCategory> getNPCCategories() {
        return this.npcCategories;
    }

    /**
     * Get the config of the waiting room
     *
     * @return The {@link Config} object
     */
    public Config getConfig() {
        return this.config;
    }

    /**
     * The object representing a category in the NPC's GUI
     */
    public static class NPCCategory {

        private ItemStack icon = new ItemStack(Material.PAPER);

        private final HyriLanguageMessage name;
        private final List<NPCData> data;

        public NPCCategory(HyriLanguageMessage name) {
            this.name = name;
            this.data = new ArrayList<>();
        }

        public NPCCategory withIcon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public ItemStack getIcon() {
            return this.icon;
        }

        public HyriLanguageMessage getName() {
            return this.name;
        }

        public NPCCategory addData(NPCData data) {
            this.data.add(data);
            return this;
        }

        public NPCCategory removeData(NPCData data) {
            this.data.remove(data);
            return this;
        }

        public List<NPCData> getData() {
            return this.data;
        }

    }

    /**
     * The object representing a data in the NPC's GUI
     */
    public static class NPCData {

        private final HyriLanguageMessage prefix;
        private final Function<IHyriPlayer, String> value;

        public NPCData(HyriLanguageMessage prefix, Function<IHyriPlayer, String> value) {
            this.prefix = prefix;
            this.value = value;
        }

        public HyriLanguageMessage getPrefix() {
            return this.prefix;
        }

        public Function<IHyriPlayer, String> getValue() {
            return this.value;
        }

        public static NPCData voidData() {
            return new NPCData(null, null);
        }

    }

    /**
     * Represents the data of a leaderboard shown in the waiting room.
     */
    public static class Leaderboard {

        private final String name;
        private final Function<Player, String> display;
        private BiFunction<Player, Double, String> scoreFormatter = (account, score) -> String.valueOf(score.intValue());

        public Leaderboard(String name, Function<Player, String> display) {
            this.name = name;
            this.display = display;
        }

        public String getName() {
            return this.name;
        }

        public Function<Player, String> getDisplay() {
            return this.display;
        }

        public BiFunction<Player, Double, String> getScoreFormatter() {
            return this.scoreFormatter;
        }

        public Leaderboard withScoreFormatter(BiFunction<Player, Double, String> scoreFormatter) {
            this.scoreFormatter = scoreFormatter;
            return this;
        }

    }

    /**
     * Config class of the waiting room
     */
    public static class Config {

        /** The player spawn location of the waiting room */
        private final LocationWrapper spawn;
        /** First waiting room area position */
        private final LocationWrapper firstPos;
        /** Second waiting room area position */
        private final LocationWrapper secondPos;
        /** The location of the npc */
        private final LocationWrapper npcLocation;

        /** The different locations of the leaderboards. E.g. kills -> location of kills leaderboard */
        private final Map<String, LocationWrapper> leaderboardsLocation = new HashMap<>();

        public Config(LocationWrapper spawn, LocationWrapper firstPos, LocationWrapper secondPos, LocationWrapper npcLocation) {
            this.spawn = spawn;
            this.firstPos = firstPos;
            this.secondPos = secondPos;
            this.npcLocation = npcLocation;
        }

        public LocationWrapper getSpawn() {
            return this.spawn;
        }

        public LocationWrapper getFirstPos() {
            return this.firstPos;
        }

        public LocationWrapper getSecondPos() {
            return this.secondPos;
        }

        public LocationWrapper getNPCLocation() {
            return this.npcLocation;
        }

        public Map<String, LocationWrapper> getLeaderboardsLocation() {
            return this.leaderboardsLocation;
        }

        public void addLeaderboardLocation(String leaderboardName, LocationWrapper location) {
            this.leaderboardsLocation.put(leaderboardName, location);
        }

    }

    /**
     * The GUI of the waiting room's npc
     */
    private class NPCGUI extends HyriInventory {

        public NPCGUI(Player owner) {
            super(owner, name(owner, "waiting-room.gui.name"), npcInventorySize);

            final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());

            this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
            this.setHorizontalLine(this.getSize() - 9, this.getSize() - 1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

            this.setItem(4, new ItemBuilder(icon)
                    .withName(HyriLanguageMessage.get("waiting-room.gui.item.name").getValue(account).replace("%game%", game.getDisplayName()))
                    .withLore(HyriLanguageMessage.get("waiting-room.gui.item.lore").getValue(account))
                    .build());

            for (Map.Entry<Integer, NPCCategory> entry : npcCategories.entrySet()) {
                final NPCCategory category = entry.getValue();
                final List<String> lore = new ArrayList<>();

                for (NPCData data : category.getData()) {
                    final HyriLanguageMessage prefix = data.getPrefix();
                    final Function<IHyriPlayer, String> value = data.getValue();

                    if (prefix != null && value != null) {
                        lore.add(ChatColor.DARK_GRAY + Symbols.DOT_BOLD + " " + ChatColor.GRAY + data.getPrefix().getValue(account) + ": " + ChatColor.AQUA + data.getValue().apply(account));
                    } else {
                        lore.add("");
                    }
                }

                final ItemStack item = new ItemBuilder(category.getIcon())
                        .withName(HyriLanguageMessage.get("waiting-room.gui.item.name").getValue(account).replace("%game%", category.getName().getValue(account)))
                        .withLore(lore)
                        .build();

                this.setItem(entry.getKey(), item);
            }
        }

    }

    /**
     * The handler class with listeners of the waiting room
     */
    public class Handler implements Listener {

        @HyriEventHandler
        public void onJoin(HyriGameJoinEvent event) {
            final HyriGamePlayer gamePlayer = event.getGamePlayer();
            final Player player = gamePlayer.getPlayer();
            final UUID playerId = gamePlayer.getUniqueId();
            final NPC npc = npcs.getOrDefault(playerId, createNPC(player));

            player.teleport(config.getSpawn().asBukkit());

            NPCManager.sendNPC(player, npc);
        }

        @HyriEventHandler
        public void onLeave(HyriGameLeaveEvent event) {
            final HyriGamePlayer gamePlayer = event.getGamePlayer();
            final NPC npc = npcs.remove(gamePlayer.getUniqueId());

            if (npc != null) {
                NPCManager.removeNPC(gamePlayer.getPlayer(), npc);
            }
        }

        @HyriEventHandler
        public void onWorldChanged(WorldChangedEvent event) {
            teleportPlayers();

            for (Map.Entry<UUID, NPC> entry : npcs.entrySet()) {
                final Player player = Bukkit.getPlayer(entry.getKey());

                NPCManager.removeNPC(entry.getValue());

                final NPC npc = createNPC(player);

                NPCManager.sendNPC(player, npc);
            }
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            final double firstY = config.getFirstPos().getY();
            final double secondY = config.getSecondPos().getY();

            if (event.getTo().getY() < Math.min(firstY, secondY)) {
                event.getPlayer().teleport(config.getSpawn().asBukkit());
            }
        }

    }

}
