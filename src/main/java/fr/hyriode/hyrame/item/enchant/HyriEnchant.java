package fr.hyriode.hyrame.item.enchant;

import fr.hyriode.hyrame.reflection.Reflection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

public abstract class HyriEnchant extends EnchantmentWrapper {

    public static final HyriEnchant GLOW = new GlowEnchant();

    public HyriEnchant(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }

    public static void register() {
        Reflection.setStaticField("acceptingNew", Enchantment.class, true);

        Enchantment.registerEnchantment(GLOW);
    }

}
