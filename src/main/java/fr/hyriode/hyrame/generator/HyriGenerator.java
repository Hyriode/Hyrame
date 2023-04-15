package fr.hyriode.hyrame.generator;

import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.hyrame.generator.event.HyriGeneratorCreatedEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorDropEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorRemovedEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorUpgradedEvent;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/01/2022 at 18:13
 */
public class HyriGenerator {

    private static final String ITEMS_TAG = "HyriGeneratorItem";

    protected final JavaPlugin plugin;
    protected final Location location;

    protected Header header;
    protected ItemStack item;

    private IHyriGeneratorTier tier;
    private final IHyriGeneratorAnimation animation;

    protected long firstSpawnTime;

    private final Handler handler;

    protected List<Player> ignoredPlayers;

    protected BukkitTask spawnTask;

    private boolean upgraded;

    protected HyriGenerator(JavaPlugin plugin, Location location, Header header, ItemStack item, IHyriGeneratorTier defaultTier, IHyriGeneratorAnimation animation, long firstSpawnTime, List<Player> ignoredPlayers) {
        this.plugin = plugin;
        this.header = header;
        this.item = item.clone();
        this.location = location;
        this.tier = defaultTier;
        this.animation = animation;
        this.firstSpawnTime = firstSpawnTime;
        this.ignoredPlayers = ignoredPlayers;
        this.handler = new Handler();

        if (this.header != null) {
            final Hologram hologram = this.header.getHologram();

            if (hologram != null) {
                hologram.setLocation(this.location.clone().add(0.0D, 1.7D + (((float) hologram.getLines().size() / 2) * hologram.getLinesDistance()), 0.0D));
            }
        }
    }

    public void create() {
        final HyriGeneratorCreatedEvent event = new HyriGeneratorCreatedEvent(this);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.plugin.getServer().getPluginManager().registerEvents(this.handler, this.plugin);

            for (Player player : Bukkit.getOnlinePlayers()) {
                this.createHologram(player);
            }

            if (this.animation != null) {
                this.animation.start();
            }

            this.restartSpawnTask(this.firstSpawnTime);
        }
    }

    private void restartSpawnTask(long initial) {
        if (this.spawnTask != null) {
            this.spawnTask.cancel();
        }

        this.spawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawn();
            }
        }.runTaskTimer(this.plugin, initial, this.tier.getTimeBetweenSpawns());
    }

    private void spawn() {
        final HyriGeneratorDropEvent event = new HyriGeneratorDropEvent(this, this.item);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            int itemsCount = 0;
            for (Entity entity : this.location.getWorld().getNearbyEntities(this.location, 3.0D, 3.0D, 3.0D)) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    final Item item = (Item) entity;
                    final ItemStack itemStack = item.getItemStack();

                    if (item.hasMetadata(this.getTag())) {
                        itemsCount += itemStack.getAmount();
                    }

                    if (this.tier.getSpawnLimit() != -1 && itemsCount >= this.tier.getSpawnLimit()) {
                        this.checkForUpgrade();
                        return;
                    }
                }
            }

            this.dropItem();
        }

        this.checkForUpgrade();
    }

    private void checkForUpgrade() {
        if (this.upgraded) {
            this.upgraded = false;

            this.restartSpawnTask(this.tier.getTimeBetweenSpawns());
        }
    }

    private boolean splitItem() {
        final List<Entity> players = this.location.getWorld().getNearbyEntities(this.location, 2.0D, 2.0D, 2.0D).stream().filter(entity -> entity.getType() == EntityType.PLAYER).filter(entity -> !this.ignoredPlayers.contains((Player) entity)).collect(Collectors.toList());

        if (players.size() > 1) {
            for (Entity entity : players) {
                final Player player = (Player) entity;

                player.getInventory().addItem(this.item.clone());
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.8F, 1.0F);
            }
            return true;
        }
        return false;
    }

    private void dropItem() {
        final Item item = LocationUtil.dropItem(this.location, this.item.clone());

        item.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
        item.setMetadata(this.getTag(), new FixedMetadataValue(this.plugin, true));
    }

    private String getTag() {
        return ITEMS_TAG + ":" + this.item.getType().name();
    }

    private void createHologram(Player player) {
        if (!this.ignoredPlayers.contains(player)) {
            if (this.header != null) {
                final Hologram hologram = this.header.getHologram();

                if (hologram != null) {
                    hologram.addReceiver(player);
                }
            }
        }
    }

    public void upgrade(IHyriGeneratorTier tier) {
        final IHyriGeneratorTier oldTier = this.tier;
        final HyriGeneratorUpgradedEvent event = new HyriGeneratorUpgradedEvent(this, tier);

        this.tier = tier;

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.upgraded = true;
        } else {
            this.tier = oldTier;
        }
    }

    public void remove() {
        final HyriGeneratorRemovedEvent event = new HyriGeneratorRemovedEvent(this);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            HandlerList.unregisterAll(this.handler);

            if (this.header != null && this.header.getHologram() != null) {
                this.header.getHologram().destroy();
            }

            this.spawnTask.cancel();

            if (this.animation != null) {
                this.animation.stop();
            }
        }
    }

    public Location getLocation() {
        return this.location;
    }

    public Header getHeader() {
        return this.header;
    }

    public ItemStack getItem() {
        return this.item.clone();
    }

    public void setItem(ItemStack item) {
        this.item = item.clone();
    }

    public IHyriGeneratorTier getTier() {
        return this.tier;
    }

    public IHyriGeneratorAnimation getAnimation() {
        return this.animation;
    }

    public long getFirstSpawnTime() {
        return this.firstSpawnTime;
    }

    public void setFirstSpawnTime(long firstSpawnTime) {
        this.firstSpawnTime = firstSpawnTime;
    }

    public void addIgnoredPlayer(Player ignoredPlayer) {
        this.ignoredPlayers.add(ignoredPlayer);
    }

    public void removeIgnoredPlayer(Player ignoredPlayer) {
        this.ignoredPlayers.remove(ignoredPlayer);
    }

    public List<Player> getIgnoredPlayers() {
        return this.ignoredPlayers;
    }

    public void setIgnoredPlayers(List<Player> ignoredPlayers) {
        this.ignoredPlayers = ignoredPlayers;
    }

    private class Handler implements Listener {

        @EventHandler
        public void onPickup(PlayerPickupItemEvent event) {
            final Item droppedItem = event.getItem();
            final Player player = event.getPlayer();

            if (IHyriPlayerSession.get(player.getUniqueId()).isModerating()) {
                event.setCancelled(true);
                return;
            }

            if (ignoredPlayers.contains(player)) {
                return;
            }

            if (droppedItem.hasMetadata(getTag())) {
                if (tier.isSplitting()) {
                    if (splitItem()) {
                        droppedItem.remove();
                        event.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            createHologram(event.getPlayer());
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            if (header != null && header.getHologram() != null) {
                final Hologram hologram = header.getHologram();

                if (hologram != null) {
                    hologram.removeReceiver(event.getPlayer());
                }
            }

        }
    }

    public static class Header {

        protected ItemStack item;
        protected Hologram hologram;

        protected final UUID id;

        public Header() {
            this.id = UUID.randomUUID();
        }

        public UUID getId() {
            return this.id;
        }

        public Header withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public Header withItem(Material material) {
            return this.withItem(new ItemStack(material));
        }

        public ItemStack getItem() {
            return this.item;
        }

        public Header withHologram(Hologram hologram) {
            this.hologram = hologram;
            return this;
        }

        public Hologram getHologram() {
            return this.hologram;
        }

    }

    public static class Builder {

        private final JavaPlugin plugin;
        private Location location;

        private Header header;
        private ItemStack item;

        private IHyriGeneratorTier defaultTier;
        private IHyriGeneratorAnimation animation;

        private long firstSpawnTime = 0;

        private List<Player> ignoredPlayers = new ArrayList<>();

        public Builder(JavaPlugin plugin, Location location, IHyriGeneratorTier defaultTier) {
            this.plugin = plugin;
            this.location = location;
            this.defaultTier = defaultTier;
        }

        public Builder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Builder withHeader(Header header) {
            this.header = header;
            return this;
        }

        public Builder withDefaultHeader(ItemStack itemStack, Function<Player, String> generatorName) {
            this.header = new HyriGeneratorDefaultHeader(this.plugin, this.location, itemStack, generatorName, this.defaultTier);
            return this;
        }

        public Builder withDefaultHeader(Material material, Function<Player, String> generatorName) {
            return this.withDefaultHeader(new ItemStack(material), generatorName);
        }

        public Builder withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public Builder withDefaultTier(IHyriGeneratorTier defaultTier) {
            this.defaultTier = defaultTier;
            return this;
        }

        public Builder withAnimation(IHyriGeneratorAnimation animation) {
            this.animation = animation;
            return this;
        }

        public Builder withDefaultAnimation() {
            this.animation = new IHyriGeneratorAnimation.Default(this.plugin, this.location, this.header);
            return this;
        }

        public Builder withFirstSpawnTime(long firstSpawnTime) {
            this.firstSpawnTime = firstSpawnTime;
            return this;
        }

        public Builder withIgnoredPlayers(List<Player> ignoredPlayers) {
            this.ignoredPlayers = ignoredPlayers;
            return this;
        }

        public HyriGenerator build() {
            if (this.plugin != null && this.location != null && this.defaultTier != null) {
                return new HyriGenerator(this.plugin, this.location, this.header, this.item, this.defaultTier, this.animation, this.firstSpawnTime, this.ignoredPlayers);
            }
            throw new RuntimeException("Couldn't set a null value to a generator builder field!");
        }

    }

}