package fr.hyriode.hyrame.armorstand;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.reflection.entity.EntityUseAction;
import fr.hyriode.hyrame.reflection.entity.EnumItemSlot;
import fr.hyriode.hyrame.utils.ThreadUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 24/08/2022 at 11:10
 */
public class ArmorStandInteraction {

    private static final double DISTANCE = 2.5D;

    private final Handler handler;

    private final List<Data> data;
    private final Map<UUID, Entity> entities;

    private final JavaPlugin plugin;
    private final Player player;

    public ArmorStandInteraction(Player player) {
        this.player = player;
        this.plugin = HyrameLoader.getHyrame().getPlugin();
        this.data = new ArrayList<>();
        this.entities = new HashMap<>();
        this.handler = new Handler();
    }

    public void create() {
        for (Data data : this.data) {
            this.handleData(data);
        }

        this.handler.enable();
    }

    public void remove() {
        this.handler.disable();

        for (Entity entity : this.entities.values()) {
            this.destroyEntity(entity);
        }

        this.entities.clear();
    }

    public ArmorStandInteraction addData(Data data) {
        this.data.add(data);
        return this;
    }

    public ArmorStandInteraction removeData(Data data) {
        final Entity entity = this.entities.remove(data.getId());

        this.data.remove(data);

        if (entity != null) {
            this.destroyEntity(entity);
        }
        return this;
    }

    private void handleData(Data data) {
        final Location location = this.createLocation(data.getAngle());
        final EntityArmorStand armorStand = this.createArmorStand(location, data.getItem());
        final Hologram hologram = new Hologram.Builder(this.plugin, location.clone().add(0.0D, 0.45D, 0.0D))
                .withLine(new Hologram.Line(data.getText()))
                .build();

        hologram.addReceiver(this.player);

        this.entities.put(data.getId(), new Entity(hologram, armorStand));

        this.sendArmorStand(armorStand);
    }

    private void update() {
        for (Map.Entry<UUID, Entity> entry : this.entities.entrySet()) {
            final Entity entity = entry.getValue();
            final EntityArmorStand armorStand = entity.getArmorStand();
            final Hologram hologram = entity.getHologram();
            final Data data = this.getData(entry.getKey());

            if (data == null) {
                continue;
            }

            final Location location = this.createLocation(data.getAngle());

            hologram.setLocation(location.clone().add(0.0D, 0.45D, 0.0D));
            armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            PacketUtil.sendPacket(this.player, new PacketPlayOutEntityTeleport(armorStand));
        }
    }

    private Location createLocation(float angle) {
        final Location playerLocation = this.player.getLocation();
        final Location rotatedLocation = playerLocation.clone();

        rotatedLocation.setPitch(0.0F);
        rotatedLocation.setYaw(rotatedLocation.getYaw() + angle);

        final Vector direction = rotatedLocation.getDirection();
        final Vector vector = rotatedLocation.toVector();
        final Location location = vector.add(direction.multiply(DISTANCE)).toLocation(this.player.getWorld());

        location.setY(location.getY() - 0.20D);
        location.setDirection(rotatedLocation.clone().subtract(location).toVector());

        return location;
    }

    private void destroyEntity(Entity entity) {
        entity.getHologram().destroy();

        PacketUtil.sendPacket(this.player, new PacketPlayOutEntityDestroy(entity.getArmorStand().getId()));
    }

    private EntityArmorStand createArmorStand(Location location, ItemStack head) {
        final EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());

        armorStand.setEquipment(EnumItemSlot.HELMET.getSlot(), CraftItemStack.asNMSCopy(head));
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setCustomNameVisible(false);
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        return armorStand;
    }

    private void sendArmorStand(EntityArmorStand armorStand) {
        if (armorStand != null) {
            PacketUtil.sendPacket(this.player, new PacketPlayOutSpawnEntity(armorStand, 78));
            PacketUtil.sendPacket(this.player, new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true));

            for (EnumItemSlot value : EnumItemSlot.values()) {
                final int slot = value.getSlot();
                final net.minecraft.server.v1_8_R3.ItemStack equipment = armorStand.getEquipment(slot);

                if (equipment == null) {
                    continue;
                }

                PacketUtil.sendPacket(this.player, new PacketPlayOutEntityEquipment(armorStand.getId(), slot, equipment));
            }
        }
    }

    private Data getData(UUID id) {
        for (Data data : this.data) {
            if (data.getId().equals(id)) {
                return data;
            }
        }
        return null;
    }

    private class Handler implements Listener {

        private IPacketHandler packetHandler;

        private double oldX;
        private double oldY;
        private double oldZ;

        public void enable() {
            final Location location = player.getLocation();

            this.oldX = location.getX();
            this.oldY = location.getY();
            this.oldZ = location.getZ();

            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

            IHyrame.get().getPacketInterceptor().addHandler(PacketType.Play.Client.USE_ENTITY, this.packetHandler = new IPacketHandler() {
                @Override
                public void onReceive(IPacketContainer container) {
                    final Player player = container.getPlayer();
                    final int entityId = container.getIntegers().read(0);

                    for (Data data : data) {
                        final Entity entity = entities.get(data.getId());

                        if (entity == null || entity.getArmorStand().getId() != entityId) {
                            continue;
                        }

                        final Consumer<Player> callback = data.getInteraction();

                        if (callback == null) {
                            continue;
                        }

                        final Object action = container.getValue("action");

                        if (action != null && action.toString().equals(EntityUseAction.ATTACK.name())) {
                            ThreadUtil.backOnMainThread(plugin, () -> callback.accept(player));
                        }
                    }
                }
            });
        }

        public void disable() {
            HandlerList.unregisterAll(this);

            HyrameLoader.getHyrame().getPacketInterceptor().removeHandler(this.packetHandler);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return;
            }

            final Location location = event.getTo();
            final double x = location.getX();
            final double y = location.getY();
            final double z = location.getZ();

            if (x != this.oldX || y != this.oldY || z != this.oldZ) {
                update();
            }

            this.oldX = x;
            this.oldY = y;
            this.oldZ = z;
        }

        @EventHandler
        public void onTeleport(PlayerTeleportEvent event) {
            if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return;
            }

            update();
        }

    }

    private static class Entity {

        private final Hologram hologram;
        private final EntityArmorStand armorStand;

        public Entity(Hologram hologram, EntityArmorStand armorStand) {
            this.hologram = hologram;
            this.armorStand = armorStand;
        }

        public Hologram getHologram() {
            return this.hologram;
        }

        public EntityArmorStand getArmorStand() {
            return this.armorStand;
        }

    }

    public static class Data {

        protected Function<Player, String> text;
        protected ItemStack item;
        protected float angle;
        protected Consumer<Player> interaction;

        protected final UUID id;

        public Data() {
            this.id = UUID.randomUUID();
        }

        protected UUID getId() {
            return this.id;
        }

        public Function<Player, String> getText() {
            return this.text;
        }

        public Data withText(Function<Player, String> text) {
            this.text = text;
            return this;
        }

        public Data withText(HyriLanguageMessage message) {
            this.text = message::getValue;
            return this;
        }

        public Data withText(String text) {
            this.text = target -> text;
            return this;
        }

        public ItemStack getItem() {
            return this.item;
        }

        public Data withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public Data withItem(Material material) {
            this.item = new ItemStack(material);
            return this;
        }

        public float getAngle() {
            return this.angle;
        }

        public Data withAngle(float angle) {
            this.angle = angle;
            return this;
        }

        public Consumer<Player> getInteraction() {
            return this.interaction;
        }

        public Data withInteraction(Consumer<Player> interaction) {
            this.interaction = interaction;
            return this;
        }

    }

}
