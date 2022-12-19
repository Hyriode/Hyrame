package fr.hyriode.hyrame.impl.host.config;

import fr.hyriode.api.host.IHostConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 08/08/2022 at 15:25
 */
public enum HostConfigIcon {

    WOOD_SWORD(Material.WOOD_SWORD),
    STONE_SWORD(Material.STONE_SWORD),
    IRON_SWORD(Material.IRON_SWORD),
    GOLD_SWORD(Material.GOLD_SWORD),
    DIAMOND_SWORD(Material.DIAMOND_SWORD),

    WOOD_PICKAXE(Material.WOOD_PICKAXE),
    STONE_PICKAXE(Material.STONE_PICKAXE),
    IRON_PICKAXE(Material.IRON_PICKAXE),
    GOLD_PICKAXE(Material.GOLD_PICKAXE),
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE),

    FISHING_ROD(Material.FISHING_ROD),
    BOW(Material.BOW),
    ARROW(Material.ARROW),

    LEATHER_CHEST_PLATE(Material.LEATHER_CHESTPLATE),
    CHAIN_MAIL_CHEST_PLATE(Material.CHAINMAIL_CHESTPLATE),
    IRON_CHEST_PLATE(Material.IRON_CHESTPLATE),
    GOLD_CHEST_PLATE(Material.GOLD_CHESTPLATE),
    DIAMOND_CHEST_PLATE(Material.DIAMOND_CHESTPLATE),

    BOOK(Material.BOOK),
    BOOK_AND_QUILL(Material.BOOK_AND_QUILL),
    ENCHANTED_BOOK(Material.ENCHANTED_BOOK),
    PAPER(Material.PAPER),

    COAL(Material.COAL),
    BRICK(Material.CLAY_BRICK),
    IRON_INGOT(Material.IRON_INGOT),
    GOLD_INGOT(Material.GOLD_INGOT),
    EMERALD(Material.EMERALD),
    DIAMOND(Material.DIAMOND),

    POTION(Material.POTION),
    BREWING_STAND(Material.BREWING_STAND_ITEM),

    NETHER_WARTS(Material.NETHER_WARTS),
    MAGMA_CREAM(Material.MAGMA_CREAM),
    FERMENTED_SPIDER_EYE(Material.FERMENTED_SPIDER_EYE),
    RABBIT_FOOT(Material.RABBIT_FOOT),
    BLAZE_POWDER(Material.BLAZE_POWDER),
    GHAST_TEAR(Material.GHAST_TEAR),
    SPECKLED_MELON(Material.SPECKLED_MELON),

    GOLDEN_APPLE(Material.GOLDEN_APPLE),
    APPLE(Material.APPLE),
    BREAD(Material.BREAD),
    COOKED_BEEF(Material.COOKED_BEEF),
    COOKED_FISH(Material.COOKED_FISH),
    BAKED_POTATO(Material.BAKED_POTATO),
    CAKE(Material.CAKE),
    GOLDEN_CARROT(Material.GOLDEN_CARROT),

    STICK(Material.STICK),
    STRING(Material.STRING),
    FEATHER(Material.FEATHER),
    SUGAR(Material.SUGAR),
    NETHER_STAR(Material.NETHER_STAR),

    BUCKET(Material.BUCKET),
    MILK_BUCKET(Material.MILK_BUCKET),
    WATER_BUCKET(Material.WATER_BUCKET),
    LAVA_BUCKET(Material.LAVA_BUCKET),

    SNOWBALL(Material.SNOW_BALL),
    FIREBALL(Material.FIREBALL),
    EGG(Material.EGG),
    EXP_BOTTLE(Material.EXP_BOTTLE),

    CARROT(Material.CARROT),
    POTATO(Material.POTATO),
    WHEAT(Material.WHEAT),
    SUGAR_CANE(Material.SUGAR_CANE),

    ENDER_PEARL(Material.ENDER_PEARL),
    ENDER_EYE(Material.EYE_OF_ENDER),

    SLIME_BALL(Material.SLIME_BALL),
    BONE(Material.BONE),
    ROTTEN_FLESH(Material.ROTTEN_FLESH),
    SPIDER_EYE(Material.SPIDER_EYE),
    BLAZE_ROD(Material.BLAZE_ROD),

    BED(Material.BED),

    CHEST(Material.CHEST),
    ENDER_CHEST(Material.ENDER_CHEST),
    ENCHANTMENT_TABLE(Material.ENCHANTMENT_TABLE),
    BOOKSHELF(Material.BOOKSHELF),
    ANVIL(Material.ANVIL),
    CRAFTING_TABLE(Material.WORKBENCH),
    FURNACE(Material.FURNACE),

    HOPPER(Material.HOPPER),
    TNT(Material.TNT),
    PISTON(Material.PISTON_BASE),
    STICKY_PISTON(Material.PISTON_STICKY_BASE),
    NOTE_BLOCK(Material.NOTE_BLOCK)

    ;

    private final Material material;

    HostConfigIcon(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getValue() {
        return this.material.name();
    }

    public ItemStack asItem() {
        return new ItemStack(this.material);
    }

    public static HostConfigIcon from(IHostConfig config) {
        for (HostConfigIcon icon : values()) {
            if (icon.getMaterial().name().equals(config.getIcon())) {
                return icon;
            }
        }
        return null;
    }

}
