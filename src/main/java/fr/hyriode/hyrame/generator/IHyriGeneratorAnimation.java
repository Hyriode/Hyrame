package fr.hyriode.hyrame.generator;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/01/2022 at 21:26
 */
public interface IHyriGeneratorAnimation {

    void start();

    void stop();

    class Default implements IHyriGeneratorAnimation {

        private ArmorStand armorStand;
        private BukkitTask task;

        private double rotate = 0;
        private boolean up = true;

        private final JavaPlugin plugin;
        private final Location location;
        private final HyriGenerator.Header header;

        public Default(JavaPlugin plugin, Location location, HyriGenerator.Header header) {
            this.plugin = plugin;
            this.location = location;
            this.header = header;
        }

        @Override
        public void start() {
            this.createArmorStand();

            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    rotate();
                }
            }.runTaskTimerAsynchronously(this.plugin, 0L, 1L);
        }

        private void createArmorStand() {
            this.armorStand = this.location.getWorld().spawn(this.location, ArmorStand.class);
            this.armorStand.setVisible(false);
            this.armorStand.setHelmet(this.header.getItem());
            this.armorStand.setGravity(false);
            this.armorStand.setRemoveWhenFarAway(false);
            this.armorStand.setCanPickupItems(false);
            this.armorStand.setArms(false);
            this.armorStand.setBasePlate(false);
            this.armorStand.setMarker(true);
        }

        private void rotate() {
            if (up) {
                if (rotate >= 540) {
                    up = false;
                }
                if (rotate > 500) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate += 1), 0));
                } else if (rotate > 470) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate += 2), 0));
                } else if (rotate > 450) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate += 3), 0));
                } else {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate += 4), 0));
                }
            } else {
                if (rotate <= 0) {
                    up = true;
                }
                if (rotate > 120) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate -= 4), 0));
                } else if (rotate > 90) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate -= 3), 0));
                } else if (rotate > 70) {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate -= 2), 0));
                } else {
                    this.armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotate -= 1), 0));
                }
            }
        }

        @Override
        public void stop() {
            this.task.cancel();
            this.armorStand.remove();
        }

    }

}
