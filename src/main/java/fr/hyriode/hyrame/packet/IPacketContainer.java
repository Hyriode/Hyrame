package fr.hyriode.hyrame.packet;

import fr.hyriode.hyrame.reflection.ObjectModifier;
import fr.hyriode.hyrame.utils.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 07:18
 */
public interface IPacketContainer {

    /**
     * Get the type of the packet
     *
     * @return A {@link PacketType}
     */
    PacketType getType();

    /**
     * Get the player that received/sent the packet
     *
     * @return The {@link Player} link to the packet process
     */
    Player getPlayer();

    /**
     * Get the packet
     *
     * @return A packet
     */
    Object getPacket();

    /**
     * Set the packet
     *
     * @param packet A packet
     */
    void setPacket(Object packet);

    /**
     * Get the name of the packet.<br>
     * Example: PacketPlayOutChat
     *
     * @return A name
     */
    String getPacketName();

    /**
     * Set the value of a field in the packet
     *
     * @param field The name of the field to edit
     * @param value The value of the field
     */
    void setValue(String field, Object value);

    Object getValue(String field);

    <T> ObjectModifier<T> getModifier(Class<T> targetClass);

    ObjectModifier<String> getStrings();

    ObjectModifier<Byte> getBytes();

    ObjectModifier<Short> getShorts();

    ObjectModifier<Integer> getIntegers();

    ObjectModifier<Long> getLongs();

    ObjectModifier<Float> getFloats();

    ObjectModifier<Double> getDoubles();

    ObjectModifier<ItemStack> getItems();

    Cancellable getCancellable();

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
