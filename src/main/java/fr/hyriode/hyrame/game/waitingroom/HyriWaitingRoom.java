package fr.hyriode.hyrame.game.waitingroom;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameJoinEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameLeaveEvent;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.npc.NPC;
import fr.hyriode.hyrame.npc.NPCManager;
import fr.hyriode.hyrame.utils.LocationWrapper;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.block.Cuboid;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.world.WorldChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 26/05/2022 at 20:05
 */
public class HyriWaitingRoom {

    private static final int MIN_SLOT = 9;
    private static final int MAX_SLOT = 35;

    protected int inventorySize = 5 * 9;

    protected boolean setup;

    protected boolean clearBlocks = true;

    protected final Map<UUID, NPC> npcs;
    protected final Map<Integer, NPCCategory> npcCategories;

    protected final HyriGame<?> game;
    protected final JavaPlugin plugin;
    protected final Material item;
    protected final Config config;
    protected final Handler handler;

    public HyriWaitingRoom(HyriGame<?> game, Material item, Config config) {
        this.game = game;
        this.plugin = this.game.getPlugin();
        this.item = item;
        this.config = config;
        this.handler = new Handler();
        this.npcs = new HashMap<>();
        this.npcCategories = new HashMap<>();
    }

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
    }

    public void remove() {
        if (!this.setup) {
            throw new IllegalStateException("Waiting room cannot be remove because it has not been setup yet!");
        }

        HyrameLogger.log("Removing waiting room...");

        for (NPC npc : this.npcs.values()) {
            NPCManager.removeNPC(npc);
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

                        new GUI(p).open();
                    }
                });

        this.npcs.put(player.getUniqueId(), npc);

        return npc;
    }

    public HyriWaitingRoom addNPCCategory(int slot, NPCCategory category) {
        if (slot > MAX_SLOT || slot < MIN_SLOT) {
            throw new IllegalStateException("NPC category's slot need to be lower than " + MAX_SLOT + " and higher than " + MIN_SLOT + "!");
        }

        this.npcCategories.put(slot, category);
        return this;
    }

    public Map<Integer, NPCCategory> getNPCCategories() {
        return this.npcCategories;
    }

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

    }

    /**
     * The GUI of the waiting room's npc
     */
    private class GUI extends HyriInventory {

        public GUI(Player owner) {
            super(owner, name(owner, "waiting-room.gui.name"), inventorySize);

            final IHyriPlayer account = IHyriPlayer.get(this.owner.getUniqueId());

            this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
            this.setHorizontalLine(this.getSize() - 9, this.getSize() - 1, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

            this.setItem(4, new ItemBuilder(item)
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
