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

    PacketType getType();

    Player getPlayer();

    Object getPacket();

    void setPacket(Object packet);

    String getPacketName();

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
