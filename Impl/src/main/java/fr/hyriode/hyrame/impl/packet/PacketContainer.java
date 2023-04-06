package fr.hyriode.hyrame.impl.packet;

import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.reflection.ObjectModifier;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:23
 */
public class PacketContainer implements IPacketContainer {

    private final PacketType type;
    private Object packet;
    private final Player player;
    private final Cancellable cancellable;

    private ObjectModifier<String> strings;
    private ObjectModifier<Byte> bytes;
    private ObjectModifier<Boolean> booleans;
    private ObjectModifier<Short> shorts;
    private ObjectModifier<Integer> integers;
    private ObjectModifier<Long> longs;
    private ObjectModifier<Float> floats;
    private ObjectModifier<Double> doubles;
    private ObjectModifier<ItemStack> items;

    public PacketContainer(PacketType type, Object packet, Player player) {
        this.type = type;
        this.packet = packet;
        this.player = player;
        this.cancellable = new Cancellable();
    }

    @Override
    public PacketType getType() {
        return this.type;
    }

    @Override
    public void setValue(String field, Object value) {
        Reflection.setField(field, this.packet, value);
    }

    @Override
    public Object getValue(String field) {
        return Reflection.invokeField(this.packet, field);
    }

    @Override
    public <T> ObjectModifier<T> getModifier(Class<T> targetClass) {
        return new ObjectModifier<>(targetClass, this.packet);
    }

    @Override
    public ObjectModifier<String> getStrings() {
        if (this.strings == null) {
            this.strings = new ObjectModifier<>(String.class, this.packet);
        }
        return this.strings;
    }

    @Override
    public ObjectModifier<Byte> getBytes() {
        if (this.bytes == null) {
            this.bytes = new ObjectModifier<>(Byte.class, this.packet);
        }
        return this.bytes;
    }

    @Override
    public ObjectModifier<Boolean> getBooleans() {
        if (this.booleans == null) {
            this.booleans = new ObjectModifier<>(Boolean.class, this.packet);
        }
        return this.booleans;
    }

    @Override
    public ObjectModifier<Short> getShorts() {
        if (this.shorts == null) {
            this.shorts = new ObjectModifier<>(Short.class, this.packet);
        }
        return this.shorts;
    }

    @Override
    public ObjectModifier<Integer> getIntegers() {
        if (this.integers == null) {
            this.integers = new ObjectModifier<>(Integer.class, this.packet);
        }
        return this.integers;
    }

    @Override
    public ObjectModifier<Long> getLongs() {
        if (this.longs == null) {
            this.longs = new ObjectModifier<>(Long.class, this.packet);
        }
        return this.longs;
    }

    @Override
    public ObjectModifier<Float> getFloats() {
        if (this.floats == null) {
            this.floats = new ObjectModifier<>(Float.class, this.packet);
        }
        return this.floats;
    }

    @Override
    public ObjectModifier<Double> getDoubles() {
        if (this.doubles == null) {
            this.doubles = new ObjectModifier<>(Double.class, this.packet);
        }
        return this.doubles;
    }

    @Override
    public ObjectModifier<ItemStack> getItems() {
        if (this.items == null) {
            this.items = new ObjectModifier<>(ItemStack.class, this.packet);
        }
        return this.items;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Object getPacket() {
        return this.packet;
    }

    @Override
    public void setPacket(Object packet) {
        this.packet = packet;
    }

    @Override
    public String getPacketName() {
        return this.packet.getClass().getSimpleName();
    }

    @Override
    public Cancellable getCancellable() {
        return this.cancellable;
    }

    @Override
    public boolean isCancelled() {
        return this.cancellable.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancellable.setCancelled(cancelled);
    }

}
