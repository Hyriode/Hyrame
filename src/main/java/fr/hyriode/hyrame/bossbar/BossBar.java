package fr.hyriode.hyrame.bossbar;

import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by AstFaster
 * on 06/06/2022 at 13:02
 */
public class BossBar {

    private EntityWither wither;

    protected BossBarAnimation animation;

    protected String text;
    protected float progress = 1.0F;

    protected final Player player;
    protected final JavaPlugin plugin;

    public BossBar(Player player, JavaPlugin plugin, String text) {
        this.player = player;
        this.plugin = plugin;
        this.text = text;
    }

    protected void spawn() {
        final Location witherLocation = this.getWitherLocation();

        this.wither = new EntityWither(((CraftWorld) player.getWorld()).getHandle());
        this.wither.setCustomName(this.text);
        this.wither.setCustomNameVisible(true);
        this.wither.setHealth(this.progress * this.wither.getMaxHealth());
        this.wither.setInvisible(true);
        this.wither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), witherLocation.getYaw(), witherLocation.getPitch());
        this.wither.r(880); // Best value to hide the Wither from player

        PacketUtil.sendPacket(player, new PacketPlayOutSpawnEntityLiving(this.wither));
    }

    protected void destroy() {
        if (this.wither != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutEntityDestroy(this.wither.getId()));
        }
    }

    public void updateMovement() {
        final Location location = this.getWitherLocation();

        this.wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        PacketUtil.sendPacket(player, new PacketPlayOutEntityTeleport(this.wither.getId(), location.getBlockX() * 32, location.getBlockY() * 32, location.getBlockZ() * 32, (byte) (location.getYaw() * 256 / 360), (byte) (location.getPitch() * 256 / 360), true));
    }

    public void applyAnimation(BossBarAnimation animation) {
        this.animation = animation;
        this.animation.start(this);
    }

    public void disableAnimation() {
        if (this.animation != null) {
            this.animation.disable();
            this.animation = null;
        }
    }

    public BossBarAnimation getAnimation() {
        return this.animation;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        if (progress == 0.0F) {
            progress = 0.0001F;
        }

        this.progress = progress;

        this.wither.setHealth(this.progress * this.wither.getMaxHealth());

        this.sendMetadata();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text, boolean update) {
        this.text = text;
        this.wither.setCustomName(text);
        this.wither.setCustomNameVisible(true);

        if (update) {
            this.sendMetadata();
        }
    }

    public void setText(String text) {
        this.setText(text, true);
    }

    private void sendMetadata() {
        PacketUtil.sendPacket(this.player, new PacketPlayOutEntityMetadata(this.wither.getId(), this.wither.getDataWatcher(), true));
    }

    private Location getWitherLocation() {
        final Location eyes = this.player.getEyeLocation().clone();

        return eyes.add(eyes.getDirection().multiply(32));
    }

    public EntityWither getHandle() {
        return this.wither;
    }

}
