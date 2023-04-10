package fr.hyriode.hyrame.utils.block;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 29/04/2022 at 21:18
 */
public class BlockUtil {

    public static boolean isPressurePlate(Material material) {
        return material == Material.STONE_PLATE || material == Material.WOOD_PLATE || material == Material.IRON_PLATE || material == Material.GOLD_PLATE;
    }

    public static void setBlockFaster(org.bukkit.block.Block bukkitBlock, int blockId, int data) {
        final WorldServer world = ((CraftWorld) bukkitBlock.getWorld()).getHandle();
        final Chunk chunk = world.getChunkAt(bukkitBlock.getX() >> 4, bukkitBlock.getZ() >> 4);
        final BlockPosition position = new BlockPosition(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        final IBlockData blockData = net.minecraft.server.v1_8_R3.Block.getByCombinedId(blockId + (data << 12));

        world.setTypeAndData(position, blockData, 3);
        chunk.a(position, blockData);
    }

    @SuppressWarnings("deprecation")
    public static void setBlocksFaster(List<Block> blocks) {
        Bukkit.getScheduler().runTaskAsynchronously(IHyrame.get().getPlugin(), () -> {
            final HashMap<Chunk, List<Block>> chunksBlocks = new HashMap<>();
            final World world = ((CraftWorld) IHyrame.WORLD.get()).getHandle();

            for (Block block : blocks) {
                final Chunk chunk = world.getChunkAt(block.getHandle().getX() >> 4, block.getHandle().getZ() >> 4);
                final List<Block> list = chunksBlocks.getOrDefault(chunk, new ArrayList<>());

                list.add(block);

                chunksBlocks.put(chunk, list);
            }

            for (Chunk chunk : chunksBlocks.keySet()) {
                Bukkit.getScheduler().runTask(IHyrame.get().getPlugin(), () -> {
                    final List<Block> chunkBlocks = chunksBlocks.get(chunk);

                    for (Block block : chunkBlocks) {
                        final org.bukkit.block.Block handle = block.getHandle();
                        final IBlockData blockData = net.minecraft.server.v1_8_R3.Block.getByCombinedId(block.getMaterialId() + (block.getData() << 12));

                        ChunkSection section = chunk.getSections()[handle.getY() >> 4];
                        if (section == null) {
                            section = new ChunkSection(handle.getY() >> 4 << 4, !chunk.world.worldProvider.o());

                            chunk.getSections()[handle.getY() >> 4] = section;
                        }

                        section.setType(handle.getX() & 15, handle.getY() & 15, handle.getZ() & 15, blockData);

                        chunk.e(new BlockPosition(handle.getX(), handle.getY(), handle.getZ())); // It updates tile entities
                    }

                    IHyrame.WORLD.get().refreshChunk(chunk.locX, chunk.locZ);
                });
            }
        });
    }

    @SuppressWarnings("deprecation")
    public static void setBlocksFaster(List<org.bukkit.block.Block> blocks, int materialId, int data) {
        final List<Block> converted = new ArrayList<>();

        for (org.bukkit.block.Block block : blocks) {
            if (block.getType().getId() == materialId && block.getData() == data) {
                continue;
            }

            converted.add(new Block(block, materialId, data));
        }

        setBlocksFaster(converted);
    }

    public static class Block {

        private final org.bukkit.block.Block handle;
        private final int materialId;
        private final int data;

        public Block(org.bukkit.block.Block handle, int materialId, int data) {
            this.handle = handle;
            this.materialId = materialId;
            this.data = data;
        }

        public org.bukkit.block.Block getHandle() {
            return this.handle;
        }

        public int getMaterialId() {
            return this.materialId;
        }

        public int getData() {
            return this.data;
        }

    }

}
