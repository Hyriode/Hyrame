package fr.hyriode.hyrame.item.enchant;

import fr.hyriode.hyrame.impl.Hyrame;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.logging.Level;

public abstract class HyriEnchant extends EnchantmentWrapper {

    public static final HyriEnchant GLOW = new GlowEnchant(6379);

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
        return false;
    }

    public static void register() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);

            Enchantment.registerEnchantment(GLOW);

            f.setAccessible(false);

            Hyrame.log("Registered Glow enchantment");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Hyrame.log(Level.SEVERE, "Cannot register Glow enchantment");
            Hyrame.log(Level.SEVERE, e.getMessage());
        }
    }
}
