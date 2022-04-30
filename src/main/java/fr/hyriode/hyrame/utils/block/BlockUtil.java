package fr.hyriode.hyrame.utils.block;

import org.bukkit.Material;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 29/04/2022 at 21:18
 */
public class BlockUtil {

    public static boolean isPressurePlate(Material material) {
        return material == Material.STONE_PLATE || material == Material.WOOD_PLATE || material == Material.IRON_PLATE || material == Material.GOLD_PLATE;
    }

}
