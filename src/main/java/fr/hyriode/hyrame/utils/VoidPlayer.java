package fr.hyriode.hyrame.utils;

import fr.hyriode.hyrame.IHyrame;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 24/05/2022 at 16:57
 */
@SuppressWarnings("ALL")
public class VoidPlayer implements Player {

    private String displayName;
    private final String name;
    private final UUID uniqueId;

    public VoidPlayer(String displayName, String name, UUID uniqueId) {
        this.displayName = displayName;
        this.name = name;
        this.uniqueId = uniqueId;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getPlayerListName() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setPlayerListName(String s) {

    }

    @Override
    public void setCompassTarget(Location location) {

    }

    @Override
    public Location getCompassTarget() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public InetSocketAddress getAddress() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String s) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {

    }

    @Override
    public void sendRawMessage(String s) {

    }

    @Override
    public void kickPlayer(String s) {

    }

    @Override
    public void chat(String s) {

    }

    @Override
    public boolean performCommand(String s) {
        return false;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public void setSneaking(boolean b) {

    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void setSprinting(boolean b) {

    }

    @Override
    public void saveData() {

    }

    @Override
    public void loadData() {

    }

    @Override
    public void setSleepingIgnored(boolean b) {

    }

    @Override
    public boolean isSleepingIgnored() {
        return false;
    }

    @Override
    public void playNote(Location location, byte b, byte b1) {

    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note) {

    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v1) {

    }

    @Override
    public void playSound(Location location, String s, float v, float v1) {

    }

    @Override
    public void playEffect(Location location, Effect effect, int i) {

    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t) {

    }

    @Override
    public void sendBlockChange(Location location, Material material, byte b) {

    }

    @Override
    public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes) {
        return false;
    }

    @Override
    public void sendBlockChange(Location location, int i, byte b) {

    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException {

    }

    @Override
    public void sendMap(MapView mapView) {

    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {

    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {

    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {

    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent baseComponent, BaseComponent baseComponent1) {

    }

    @Override
    public void setTitleTimes(int i, int i1, int i2) {

    }

    @Override
    public void setSubtitle(BaseComponent[] baseComponents) {

    }

    @Override
    public void setSubtitle(BaseComponent baseComponent) {

    }

    @Override
    public void showTitle(BaseComponent[] baseComponents) {

    }

    @Override
    public void showTitle(BaseComponent baseComponent) {

    }

    @Override
    public void showTitle(BaseComponent[] baseComponents, BaseComponent[] baseComponents1, int i, int i1, int i2) {

    }

    @Override
    public void showTitle(BaseComponent baseComponent, BaseComponent baseComponent1, int i, int i1, int i2) {

    }

    @Override
    public void sendTitle(Title title) {

    }

    @Override
    public void updateTitle(Title title) {

    }

    @Override
    public void hideTitle() {

    }

    @Override
    public void updateInventory() {

    }

    @Override
    public void awardAchievement(Achievement achievement) {

    }

    @Override
    public void removeAchievement(Achievement achievement) {

    }

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return false;
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {

    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {

    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {

    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {

    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {

    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {

    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {

    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i) {

    }

    @Override
    public void setPlayerTime(long l, boolean b) {

    }

    @Override
    public long getPlayerTime() {
        return 0;
    }

    @Override
    public long getPlayerTimeOffset() {
        return 0;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return false;
    }

    @Override
    public void resetPlayerTime() {

    }

    @Override
    public void setPlayerWeather(WeatherType weatherType) {

    }

    @Override
    public WeatherType getPlayerWeather() {
        return WeatherType.CLEAR;
    }

    @Override
    public void resetPlayerWeather() {

    }

    @Override
    public void giveExp(int i) {

    }

    @Override
    public void giveExpLevels(int i) {

    }

    @Override
    public float getExp() {
        return 0;
    }

    @Override
    public void setExp(float v) {

    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void setLevel(int i) {

    }

    @Override
    public int getTotalExperience() {
        return 0;
    }

    @Override
    public void setTotalExperience(int i) {

    }

    @Override
    public float getExhaustion() {
        return 0;
    }

    @Override
    public void setExhaustion(float v) {

    }

    @Override
    public float getSaturation() {
        return 0;
    }

    @Override
    public void setSaturation(float v) {

    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public void setFoodLevel(int i) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public void setBanned(boolean b) {

    }

    @Override
    public boolean isWhitelisted() {
        return false;
    }

    @Override
    public void setWhitelisted(boolean b) {

    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public long getFirstPlayed() {
        return 0;
    }

    @Override
    public long getLastPlayed() {
        return 0;
    }

    @Override
    public boolean hasPlayedBefore() {
        return false;
    }

    @Override
    public Location getBedSpawnLocation() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setBedSpawnLocation(Location location) {

    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {

    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public void setAllowFlight(boolean b) {

    }

    @Override
    public void hidePlayer(Player player) {

    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public boolean canSee(Player player) {
        return false;
    }

    @Override
    public Location getLocation() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Location getLocation(Location location) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setVelocity(Vector vector) {

    }

    @Override
    public Vector getVelocity() {
        return new Vector();
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public World getWorld() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean teleport(Location location) {
        return false;
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        return false;
    }

    @Override
    public boolean teleport(Entity entity) {
        return false;
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
        return false;
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v1, double v2) {
        return new ArrayList<>();
    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public int getFireTicks() {
        return 0;
    }

    @Override
    public int getMaxFireTicks() {
        return 0;
    }

    @Override
    public void setFireTicks(int i) {

    }

    @Override
    public void remove() {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void sendMessage(String s) {

    }

    @Override
    public void sendMessage(String[] strings) {

    }

    @Override
    public Server getServer() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Entity getPassenger() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean setPassenger(Entity entity) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean eject() {
        return false;
    }

    @Override
    public float getFallDistance() {
        return 0;
    }

    @Override
    public void setFallDistance(float v) {

    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent) {

    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public int getTicksLived() {
        return 0;
    }

    @Override
    public void setTicksLived(int i) {

    }

    @Override
    public void playEffect(EntityEffect entityEffect) {

    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public Entity getVehicle() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setCustomName(String s) {

    }

    @Override
    public String getCustomName() {
        return this.displayName;
    }

    @Override
    public void setCustomNameVisible(boolean b) {

    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public void setFlying(boolean b) {

    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException {

    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException {

    }

    @Override
    public float getFlySpeed() {
        return 0;
    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public void setTexturePack(String s) {

    }

    @Override
    public void setResourcePack(String s) {

    }

    @Override
    public Scoreboard getScoreboard() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public boolean isHealthScaled() {
        return false;
    }

    @Override
    public void setHealthScaled(boolean b) {

    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException {

    }

    @Override
    public double getHealthScale() {
        return 0;
    }

    @Override
    public Entity getSpectatorTarget() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setSpectatorTarget(Entity entity) {

    }

    @Override
    public void sendTitle(String s, String s1) {

    }

    @Override
    public void resetTitle() {

    }

    @Override
    public Spigot spigot() {
        return new Spigot();
    }

    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PlayerInventory getInventory() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Inventory getEnderChest() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i) {
        return false;
    }

    @Override
    public InventoryView getOpenInventory() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void openInventory(InventoryView inventoryView) {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public ItemStack getItemInHand() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {

    }

    @Override
    public ItemStack getItemOnCursor() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack) {

    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    @Override
    public int getSleepTicks() {
        return 0;
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public void setGameMode(GameMode gameMode) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public int getExpToLevel() {
        return 0;
    }

    @Override
    public double getEyeHeight() {
        return 0;
    }

    @Override
    public double getEyeHeight(boolean b) {
        return 0;
    }

    @Override
    public Location getEyeLocation() {
        return new Location(IHyrame.WORLD.get(), 0, 0, 0);
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> hashSet, int i) {
        return new ArrayList<>();
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        return new ArrayList<>();
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> hashSet, int i) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> hashSet, int i) {
        return new ArrayList<>();
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        return new ArrayList<>();
    }

    @Override
    public Egg throwEgg() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Snowball throwSnowball() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public Arrow shootArrow() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public int getRemainingAir() {
        return 0;
    }

    @Override
    public void setRemainingAir(int i) {

    }

    @Override
    public int getMaximumAir() {
        return 0;
    }

    @Override
    public void setMaximumAir(int i) {

    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public void setMaximumNoDamageTicks(int i) {

    }

    @Override
    public double getLastDamage() {
        return 0;
    }

    @Override
    public int _INVALID_getLastDamage() {
        return 0;
    }

    @Override
    public void setLastDamage(double v) {

    }

    @Override
    public void _INVALID_setLastDamage(int i) {

    }

    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    @Override
    public void setNoDamageTicks(int i) {

    }

    @Override
    public Player getKiller() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect) {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b) {
        return false;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> collection) {
        return false;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType) {
        return false;
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType) {

    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    @Override
    public void setRemoveWhenFarAway(boolean b) {

    }

    @Override
    public EntityEquipment getEquipment() {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void setCanPickupItems(boolean b) {

    }

    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public boolean setLeashHolder(Entity entity) {
        return false;
    }

    @Override
    public void damage(double v) {

    }

    @Override
    public void _INVALID_damage(int i) {

    }

    @Override
    public void damage(double v, Entity entity) {

    }

    @Override
    public void _INVALID_damage(int i, Entity entity) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public int _INVALID_getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double v) {

    }

    @Override
    public void _INVALID_setHealth(int i) {

    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public int _INVALID_getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(double v) {

    }

    @Override
    public void _INVALID_setMaxHealth(int i) {

    }

    @Override
    public void resetMaxHealth() {

    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {}

    @Override
    public boolean isPermissionSet(String s) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<>();
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean b) {}

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {}

    @Override
    public Set<String> getListeningPluginChannels() {
        return new HashSet<>();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass) {
        return this.launchProjectile(aClass, new Vector());
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
        throw new IllegalStateException("Cannot invoke this method for a void player!");
    }

}
