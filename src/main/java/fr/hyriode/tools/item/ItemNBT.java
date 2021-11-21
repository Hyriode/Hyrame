package fr.hyriode.tools.item;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ItemNBT {

    private final NBTTagCompound nbtTagCompound;
    private final net.minecraft.server.v1_8_R3.ItemStack nmsItemStack;

    public ItemNBT(ItemStack itemStack) {
        this.nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        this.nbtTagCompound = this.nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
    }

    public String getString(String tag) {
        return this.nmsItemStack.getTag().getString(tag);
    }

    public ItemNBT setString(String tag, String value) {
        this.nbtTagCompound.setString(tag, value);

        return this;
    }

    public int getInt(String tag) {
        return this.nbtTagCompound.getInt(tag);
    }

    public ItemNBT setInt(String tag, int value) {
        this.nbtTagCompound.setInt(tag, value);

        return this;
    }

    public short getShort(String tag) {
        return this.nbtTagCompound.getShort(tag);
    }

    public ItemNBT setShort(String tag, short value) {
        this.nbtTagCompound.setShort(tag, value);

        return this;
    }

    public long getLong(String tag) {
        return this.nbtTagCompound.getLong(tag);
    }

    public ItemNBT setLong(String tag, long value) {
        this.nbtTagCompound.setLong(tag, value);

        return this;
    }

    public float getFloat(String tag) {
        return this.nbtTagCompound.getFloat(tag);
    }

    public ItemNBT setFloat(String tag, float value) {
        this.nbtTagCompound.setFloat(tag, value);

        return this;
    }

    public double getDouble(String tag) {
        return this.nbtTagCompound.getDouble(tag);
    }

    public ItemNBT setDouble(String tag, double value) {
        this.nbtTagCompound.setDouble(tag, value);

        return this;
    }

    public byte getByte(String tag) {
        return this.nbtTagCompound.getByte(tag);
    }

    public ItemNBT setByte(String tag, byte value) {
        this.nbtTagCompound.setByte(tag, value);

        return this;
    }

    public boolean getBoolean(String tag) {
        return this.nbtTagCompound.getBoolean(tag);
    }

    public ItemNBT setBoolean(String tag, boolean value) {
        this.nbtTagCompound.setBoolean(tag, value);

        return this;
    }

    public byte[] getByteArray(String tag) {
        return this.nbtTagCompound.getByteArray(tag);
    }

    public ItemNBT setByteArray(String tag, byte[] value) {
        this.nbtTagCompound.setByteArray(tag, value);

        return this;
    }

    public int[] getIntArray(String tag) {
        return this.nbtTagCompound.getIntArray(tag);
    }

    public ItemNBT setIntArray(String tag, int[] value) {
        this.nbtTagCompound.setIntArray(tag, value);

        return this;
    }

    public ItemNBT removeTag(String tag) {
        this.nbtTagCompound.remove(tag);

        return this;
    }

    public boolean hasTag(String tag) {
        return this.nbtTagCompound.hasKey(tag);
    }

    public ItemStack build() {
        this.nmsItemStack.setTag(this.nbtTagCompound);

        return CraftItemStack.asBukkitCopy(this.nmsItemStack);
    }

    public ItemBuilder toBuilder() {
        return new ItemBuilder(this.build());
    }

}