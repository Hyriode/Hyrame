package fr.hyriode.hyrame.impl.listener.global;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.Area;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.EntitySlime;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AstFaster
 * on 27/06/2022 at 10:26
 */
public class Basketball {

    private static final String METADATA_KEY = "Basketball";

    private Slime slime;

    private final Handler handler;

    private final JavaPlugin plugin;
    private final Config config;

    public Basketball(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        this.handler = new Handler();
    }

    public void start() {
        this.spawnSlime();

        this.plugin.getServer().getPluginManager().registerEvents(this.handler, this.plugin);
        this.handler.start();
    }

    public void stop() {
        this.slime.remove();
        this.slime = null;

        this.handler.stop();

        HandlerList.unregisterAll(this.handler);
    }

    private void spawnSlime() {
        final World world = IHyrame.WORLD.get();
        final Location location = this.config.getBallLocation();

        this.slime = (Slime) world.spawnEntity(location, EntityType.SLIME);
        this.slime.setSize(2);
        this.slime.setMetadata(METADATA_KEY, new FixedMetadataValue(this.plugin, this));

        final EntitySlime nmsSlime = this.getNMSSlime();

        try {
            final List<String> pathfinders = Arrays.asList("goalSelector", "targetSelector");

            for (String pathfinderField : pathfinders) {
                final Field field = nmsSlime.getClass().getField(pathfinderField);
                final Object pathfinder = field.get(nmsSlime);

                Reflection.setField("b", pathfinder, new UnsafeList<>());
                Reflection.setField("c", pathfinder, new UnsafeList<>());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void moveBall(Vector vector) {
        this.slime.setVelocity(vector);
    }

    public boolean isBall(Entity entity) {
        return entity.hasMetadata(METADATA_KEY);
    }

    private EntitySlime getNMSSlime() {
        return ((CraftSlime) this.slime).getHandle();
    }

    private class Handler implements Listener {

        private Vector last;
        private boolean wasIn = true;
        private BukkitTask collisionTask;

        protected void start() {
            this.collisionTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (!config.getGameArea().isInArea(slime.getLocation())) {
                    if (wasIn) {
                        this.last = slime.getVelocity().clone();
                        /*final double x = Math.floor(velocity.getX());
                        final double y = Math.floor(velocity.getY());
                        final double z = Math.floor(velocity.getZ());
                        final Vector roundedVector = new Vector(x > 0 ? 1 : (x < 0 ? -1 : 0), y > 0 ? 1 : (y < 0 ? -1 : 0), z > 0 ? 1 : (z < 0 ? -1 : 0)).normalize();
                        final Vector output = velocity.subtract(roundedVector.multiply(velocity.dot(roundedVector) * 2.0D));*/

                        slime.setVelocity(this.last.multiply(-1));
                        wasIn = false;
                    } else {
                        slime.setVelocity(this.last);
                    }
                } else {
                    wasIn = true;
                }

                if (!wasIn) {
                    return;
                }

                final AxisAlignedBB boundingBox = getNMSSlime().getBoundingBox();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    final AxisAlignedBB playerBox = ((CraftPlayer) player).getHandle().getBoundingBox();

                    if (boundingBox.b(playerBox)) {
                        final Vector vector = player.getEyeLocation().getDirection().clone();

                        moveBall(vector.multiply(new Vector(1.12D, 1.24D, 1.12D)));

                        Bukkit.broadcastMessage("COLLISION!");
                    }
                }
            }, 0L, 3L);
        }

        protected void stop() {
            this.collisionTask.cancel();
            this.collisionTask = null;
        }

        @EventHandler
        public void onDamage(EntityDamageEvent event) {
            final Entity entity = event.getEntity();

            if (isBall(entity)) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onDamageByEntity(EntityDamageByEntityEvent event) {
            final Entity dealer = event.getDamager();

            if (isBall(dealer)) {
                event.setCancelled(true);
            }
        }

    }

    public static class Config {

        private final Location ballLocation;
        private final Area gameArea;

        public Config(Location ballLocation, Area gameArea) {
            this.ballLocation = ballLocation;
            this.gameArea = gameArea;
        }

        public Location getBallLocation() {
            return this.ballLocation;
        }

        public Area getGameArea() {
            return this.gameArea;
        }

    }

}
