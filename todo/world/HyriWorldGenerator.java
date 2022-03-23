package fr.hyriode.hyrame.world;

import fr.hyriode.hyrame.reflection.Reflection;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/03/2022 at 15:31
 */
public class HyriWorldGenerator {

    public static final List<BiomeBase> COMMON_PATCHED_BIOMES = Arrays.asList(
            BiomeBase.DEEP_OCEAN, BiomeBase.FROZEN_OCEAN, BiomeBase.OCEAN,
            BiomeBase.COLD_BEACH, BiomeBase.BEACH, BiomeBase.STONE_BEACH,
            BiomeBase.JUNGLE, BiomeBase.JUNGLE_EDGE, BiomeBase.JUNGLE_HILLS,
            BiomeBase.SWAMPLAND, BiomeBase.EXTREME_HILLS_PLUS
    );

    private final List<BiomeBase> patchedBiomes;

    private final HyriWorldSettings worldSettings;

    public HyriWorldGenerator(HyriWorldSettings worldSettings) {
        this.worldSettings = worldSettings;
        this.patchedBiomes = new ArrayList<>();
    }

    public World start() {
        this.patchBiomes();

        final World world = new WorldCreator(this.worldSettings.getWorldName()).type(this.worldSettings.getWorldType())
                .environment(this.worldSettings.getWorldEnvironment())
                .createWorld();

        return world;
    }

    private void patchBiomes() {
        final BiomeBase[] biomes = BiomeBase.getBiomes();
        final Map<String, BiomeBase> biomesMap = BiomeBase.o;
        final BiomeBase defaultBiome = BiomeBase.FOREST;

        Reflection.setStaticFinalField("ad", BiomeBase.class, defaultBiome);

        for (BiomeBase patchedBiome : this.patchedBiomes) {
            biomesMap.remove(patchedBiome.ah);
        }

        for (int i = 0; i < biomes.length; i++) {
            final BiomeBase biome = biomes[i];

            if (biome != null) {
                if (!biomesMap.containsKey(biome.ah)) {
                    biomes[i] = defaultBiome;
                }

                Reflection.setField("F", biome.as, 64);
            }
        }

        Reflection.setStaticFinalField("biomes", BiomeBase.class, biomes);
    }

    public HyriWorldGenerator patchBiome(BiomeBase biome) {
        this.patchedBiomes.add(biome);
        return this;
    }

    public HyriWorldGenerator patchBiomes(List<BiomeBase> biomes) {
        patchedBiomes.addAll(biomes);
        return this;
    }

}
