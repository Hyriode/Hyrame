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

    /**
     * Get the value of a field in the packet
     *
     * @param field The name of the field to get the value
     * @return An object or <code>null</code> if the field doesn't exist
     */
    Object getValue(String field);

    /**
     * Get the modifier of a target class
     *
     * @param targetClass The target class of fields to edit
     * @param <T> The type of modifier
     * @return A created {@link ObjectModifier} with the given type
     */
    <T> ObjectModifier<T> getModifier(Class<T> targetClass);

    /**
     * Get the modifier of string fields
     *
     * @return A {@link ObjectModifier} of {@link String}
     */
    ObjectModifier<String> getStrings();

    /**
     * Get the modifier of byte fields
     *
     * @return A {@link ObjectModifier} of {@link Byte}
     */
    ObjectModifier<Byte> getBytes();

    /**
     * Get the modifier of short fields
     *
     * @return A {@link ObjectModifier} of {@link Short}
     */
    ObjectModifier<Short> getShorts();

    /**
     * Get the modifier of integer fields
     *
     * @return A {@link ObjectModifier} of {@link Integer}
     */
    ObjectModifier<Integer> getIntegers();

    /**
     * Get the modifier of long fields
     *
     * @return A {@link ObjectModifier} of {@link Long}
     */
    ObjectModifier<Long> getLongs();

    /**
     * Get the modifier of float fields
     *
     * @return A {@link ObjectModifier} of {@link Float}
     */
    ObjectModifier<Float> getFloats();

    /**
     * Get the modifier of double fields
     *
     * @return A {@link ObjectModifier} of {@link Double}
     */
    ObjectModifier<Double> getDoubles();

    /**
     * Get the modifier of item fields
     *
     * @return A {@link ObjectModifier} of {@link ItemStack}
     */
    ObjectModifier<ItemStack> getItems();

    /**
     * Get the cancellable object
     *
     * @return The {@link Cancellable} object
     */
    Cancellable getCancellable();

    /**
     * Check if the packet process in cancelled or not
     *
     * @return <code>true</code> if the process is cancelled
     */
    boolean isCancelled();

    /**
     * Set if the packet process is cancelled
     *
     * @param cancelled The new value for cancelling the process
     */
    void setCancelled(boolean cancelled);

}
