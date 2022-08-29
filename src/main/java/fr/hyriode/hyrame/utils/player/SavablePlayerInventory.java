package fr.hyriode.hyrame.utils.player;

import fr.hyriode.hyrame.utils.SerializerUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by AstFaster
 * on 24/08/2022 at 19:19
 */
public class SavablePlayerInventory {

    private transient ItemStack[] deserializedContents;
    private transient ItemStack[] deserializedArmor;

    private final String contents;
    private final String armor;

    public SavablePlayerInventory(ItemStack[] contents, ItemStack[] armor) {
        this.deserializedContents = contents;
        this.deserializedArmor = armor;
        this.contents = SerializerUtil.itemStackArrayToString(this.deserializedContents);
        this.armor = SerializerUtil.itemStackArrayToString(this.deserializedArmor);
    }

    public SavablePlayerInventory(PlayerInventory inventory) {
        this(inventory.getContents(), inventory.getArmorContents());
    }

    public void setTo(PlayerInventory inventory) {
        if (this.deserializedContents == null || this.deserializedArmor == null) {
            this.deserializedContents = SerializerUtil.itemStackArrayFromString(this.contents);
            this.deserializedArmor = SerializerUtil.itemStackArrayFromString(this.armor);
        }

        inventory.setContents(this.deserializedContents);
        inventory.setArmorContents(this.deserializedArmor);
    }

}
