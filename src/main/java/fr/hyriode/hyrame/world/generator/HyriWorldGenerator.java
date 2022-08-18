package fr.hyriode.hyrame.world.generator;

import fr.hyriode.hyrame.HyrameLogger;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 02/03/2022 at 15:31
 */
public class HyriWorldGenerator {

    /** A default list of biomes that can be patched */
    public static final List<BiomeBase> COMMON_PATCHED_BIOMES = Arrays.asList(
            BiomeBase.DEEP_OCEAN, BiomeBase.FROZEN_OCEAN, BiomeBase.OCEAN,
            BiomeBase.COLD_BEACH, BiomeBase.BEACH, BiomeBase.STONE_BEACH,
            BiomeBase.JUNGLE, BiomeBase.JUNGLE_EDGE, BiomeBase.JUNGLE_HILLS,
            BiomeBase.SWAMPLAND, BiomeBase.EXTREME_HILLS_PLUS
    );

    /** The generation task */
    private BukkitTask task;

    /** The list of biomes to patch */
    private final Map<BiomeBase, BiomeBase> patchedBiomes;

    /** A {@link JavaPlugin} instance */
    private final JavaPlugin plugin;
    /** The settings of the world to generate */
    private final HyriWorldSettings worldSettings;
    /** The size of world to load */
    private final int size;
    private final Consumer<World> onComplete;

    /**
     * Constructor of {@link HyriWorldGenerator}
     *  @param plugin A {@link JavaPlugin} instance
     * @param worldSettings The {@link HyriWorldSettings}
     * @param size The size of world to load
     * @param onComplete The {@link Consumer} to call when the generation will be over
     */
    public HyriWorldGenerator(JavaPlugin plugin, HyriWorldSettings worldSettings, int size, Consumer<World> onComplete) {
        this.plugin = plugin;
        this.worldSettings = worldSettings;
        this.size = size;
        this.onComplete = onComplete;
        this.patchedBiomes = new HashMap<>();
    }

    /**
     * Start the generation of the world
     *
     * @return The created {@link World}
     */
    public World start() {
        this.patchBiomes();

        final World world = new WorldCreator(this.worldSettings.getWorldName())
                .type(this.worldSettings.getWorldType())
                .environment(this.worldSettings.getWorldEnvironment())
                .createWorld();

        this.startGeneration(world);

        return world;
    }

    /**
     * Start the generation of the world and the loading of chunks
     */
    private void startGeneration(World world) {
        HyrameLogger.log("Starting generation of '" + this.worldSettings.getWorldName() + "' world...");

        this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, new Runnable() {

            /** The last percentage shown */
            private int lastPercentage;

            private int x = -size;
            private int z = -size;

            /** The amount of chunks to load */
            private final int totalChunks = ((size * 2) * (size * 2)) / 256;
            /** The current amount of loaded chunks */
            private int currentChunks;

            @Override
            public void run() {
                int i = 0;

                while (i < 50) {
                    world.getChunkAt(world.getBlockAt(this.x, 64, this.z)).load(true);

                    final int percentage = this.currentChunks * 100 / this.totalChunks;

                    if (percentage > this.lastPercentage && percentage % 10 == 0) {
                        this.lastPercentage = percentage;

                        HyrameLogger.log("Loading chunks | " + percentage + "%");
                    }

                    this.z += 16;

                    if (this.z >= size) {
                        this.z = -size;
                        this.x += 16;
                    }

                    if (this.x >= size) {
                        task.cancel();

                        HyrameLogger.log("Generation finished.");

                        if (onComplete != null) {
                            onComplete.accept(world);
                        }
                        return;
                    }

                    this.currentChunks++;
                    i++;
                }
            }
        }, 1L, 1L);
    }

    /**
     * Patch all biomes
     */
    private void patchBiomes() {
        HyrameLogger.log("Patching biomes before generating...");

        try {
            final Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            final Field modifiersField = Field.class.getDeclaredField("modifiers");

            biomesField.setAccessible(true);

            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & 0xFFFFFFEF);

            if (biomesField.get(null) instanceof BiomeBase[]) {
                final BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);

                for (Map.Entry<BiomeBase, BiomeBase> entry : this.patchedBiomes.entrySet()) {
                    biomes[entry.getKey().id] = entry.getValue();
                }

                biomesField.set(null, biomes);
            }
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a biome for the generation
     *
     * @param biome The biome to remove
     * @param replace The biome that will replace the removed one
     * @return This {@link HyriWorldGenerator} instance
     */
    public HyriWorldGenerator patchBiome(BiomeBase biome, BiomeBase replace) {
        this.patchedBiomes.put(biome, replace);
        return this;
    }

    /**
     * Remove a biome for the generation
     *
     * @param biomes The biomes to remove
     * @return This {@link HyriWorldGenerator} instance
     */
    public HyriWorldGenerator patchBiomes(BiomeBase... biomes) {
        for (BiomeBase biome : biomes) {
            this.patchedBiomes.put(biome, BiomeBase.PLAINS);
        }
        return this;
    }

    /**
     * Remove biomes for the generation
     *
     * @param biomes The biomes to remove
     * @return This {@link HyriWorldGenerator} instance
     */
    public HyriWorldGenerator patchBiomes(Map<BiomeBase, BiomeBase> biomes) {
        this.patchedBiomes.entrySet().addAll(biomes.entrySet());
        return this;
    }

}
