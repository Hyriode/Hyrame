package fr.hyriode.hyrame.bossbar;

import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class BossBar {

    protected int messageCooldown;
    protected final int delay;
    protected double timeoutCooldown;
    protected final int timeout;

    protected boolean updateProgressWithTimeout;
    protected float progress = 1.0F;

    private EntityWither wither;

    private AtomicInteger actualPlayerTitle;
    protected List<String> titles;

    protected BukkitTask timeoutTask;
    protected BukkitTask witherTask;

    protected final Player player;

    protected final JavaPlugin plugin;

    public BossBar(JavaPlugin plugin, Player player, List<String> titles, int delay, int timeout, boolean updateProgressWithTimeout) {
        this.plugin = plugin;
        this.player = player;
        this.titles = titles;
        this.delay = delay;
        this.timeout = timeout;
        this.updateProgressWithTimeout = updateProgressWithTimeout;
        this.messageCooldown = 0;
        this.timeoutCooldown = 0;
    }

    protected void spawn() {
        final Location playerLocation = this.player.getLocation();
        final Location witherLocation = this.getWitherLocation(playerLocation);

        this.wither = new EntityWither(((CraftWorld) player.getWorld()).getHandle());

        this.wither.setCustomName(titles.get(0));
        this.wither.setHealth(this.progress * this.wither.getMaxHealth());
        this.wither.setInvisible(true);
        this.wither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), witherLocation.getYaw(), witherLocation.getPitch());
        this.wither.r(880);

        PacketUtil.sendPacket(player, new PacketPlayOutSpawnEntityLiving(wither));

        this.actualPlayerTitle = new AtomicInteger(1);
        this.witherTask = this.witherTask().runTaskTimer(plugin, 20, 20);
        this.timeoutTask = this.timeoutTask().runTaskTimer(plugin, 1, 1);
    }

    protected void destroy() {
        if (this.wither != null) {
            PacketUtil.sendPacket(player, new PacketPlayOutEntityDestroy(wither.getId()));

            this.witherTask.cancel();
            this.timeoutTask.cancel();
        }
    }

    public void updateMovement() {
        final Location location = this.getWitherLocation(this.player.getLocation());

        this.wither.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        PacketUtil.sendPacket(player, new PacketPlayOutEntityTeleport(this.wither));
    }

    protected void sendMetaData() {
        PacketUtil.sendPacket(this.player, new PacketPlayOutEntityMetadata(this.wither.getId(), this.wither.getDataWatcher(), true));
    }

    private BukkitRunnable witherTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                updateMovement();

                // Change title
                messageCooldown++;

                if (messageCooldown == delay) {
                    wither.setCustomName(titles.get(actualPlayerTitle.get()));

                    sendMetaData();

                    if (actualPlayerTitle.addAndGet(1) >= titles.size()) actualPlayerTitle.set(0);

                    messageCooldown = 0;
                }
            }
        };
    }

    private BukkitRunnable timeoutTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                // Check timeout
                if (timeout > 0) {
                    timeoutCooldown += 0.04;

                    if (timeoutCooldown >= timeout) {
                        BossBarManager.removeBar(player);
                    }

                    if (updateProgressWithTimeout) {
                        final float progress = (float) (timeoutCooldown / timeout);

                        if (progress * wither.getMaxHealth() > 1) {
                            setProgress(progress);
                        }
                    }
                }
            }
        };
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
        this.actualPlayerTitle = new AtomicInteger(0);
    }

    public void setProgress(float progress) {
        this.progress = progress;

        this.wither.setHealth(this.progress * this.wither.getMaxHealth());

        if (this.wither.getHealth() <= 1) {
            BossBarManager.removeBar(this.player);
        } else {
            this.sendMetaData();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public boolean isUpdateProgressWithTimeout() {
        return this.updateProgressWithTimeout;
    }

    public List<String> getTitles() {
        return this.titles;
    }

    private Location getWitherLocation(Location base) {
        return base.add(base.getDirection().multiply(32));
    }

}
