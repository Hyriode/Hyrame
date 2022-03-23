package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.world.HyriWorldSettings;
import fr.hyriode.hyrame.world.generator.HyriWorldGenerator;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:54
 */
public class HyramePlugin extends JavaPlugin {

    private Hyrame hyrame;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.register(() -> new Hyrame(this));

        HyrameLoader.load(new HyrameProvider(this));

        final World world = Bukkit.getWorld("world");

        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("announceAdvancements", "false");

        final HyriWorldSettings settings = new HyriWorldSettings("test-world");
        final HyriWorldGenerator generator = new HyriWorldGenerator(this, settings, 1000, generatedWorld -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(GameMode.CREATIVE);
                player.teleport(new Location(generatedWorld, 0, 150, 0));
            }
        });
        generator.patchBiome(BiomeBase.DEEP_OCEAN, BiomeBase.FOREST);
        generator.patchBiome(BiomeBase.FROZEN_OCEAN, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.OCEAN, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.COLD_BEACH, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.BEACH, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.STONE_BEACH, BiomeBase.FOREST);
        generator.patchBiome(BiomeBase.JUNGLE, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.JUNGLE_EDGE, BiomeBase.FOREST);
        generator.patchBiome(BiomeBase.JUNGLE_HILLS, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.SWAMPLAND, BiomeBase.PLAINS);
        generator.patchBiome(BiomeBase.EXTREME_HILLS_PLUS, BiomeBase.PLAINS);
        generator.start();
    }

    @Override
    public void onDisable() {
        this.hyrame.disable();
    }

    public Hyrame getHyrame() {
        return this.hyrame;
    }

}
