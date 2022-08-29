package fr.hyriode.hyrame.enchantment;

import fr.hyriode.api.language.HyriLanguageMessage;
import org.bukkit.enchantments.Enchantment;

/**
 * Created by AstFaster
 * on 25/08/2022 at 15:05
 */
public enum EnchantmentWrapper {

    PROTECTION(0, "protection"),
    PROTECTION_FIRE(1, "protection-fire"),
    FEATHER_FALLING(2, "feather-falling"),
    PROTECTION_EXPLOSION(3, "protection-explosion"),
    PROTECTION_PROJECTILE(4, "protection-projectile"),
    RESPIRATION(5, "respiration"),
    AQUA_AFFINITY(6, "aqua-affinity"),
    THORNS(7, "thorns"),
    DEPTH_STRIDER(8, "depth-strider"),
    SHARPNESS(16, "sharpness"),
    SMITE(17, "smite"),
    BANE_OF_ARTHROPODS(18, "bane-of-arthropods"),
    KNOCKBACK(19, "knockback"),
    FIRE_ASPECT(20, "fire-aspect"),
    LOOTING(21, "looting"),
    EFFICIENCY(32, "efficiency"),
    SILK_TOUCH(33, "silk-touch"),
    DURABILITY(34, "durability"),
    FORTUNE(35, "fortune"),
    POWER(48, "power"),
    PUNCH(49, "punch"),
    FLAME(50, "flame"),
    INFINITY(51, "infinity"),
    LUCK_OF_THE_SEA(61, "luck-of-the-sea"),
    LURE(62, "lure");
    
    private HyriLanguageMessage display;
    
    private final int id;
    private final String name;

    EnchantmentWrapper(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @SuppressWarnings("deprecation")
    public Enchantment asBukkit() {
        return Enchantment.getById(this.id);
    }

    public HyriLanguageMessage getDisplay() {
        return this.display == null ? this.display = HyriLanguageMessage.get("enchantment." + this.name + ".name") : this.display;
    }
    
}
