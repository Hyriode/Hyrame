package fr.hyriode.hyrame.signgui;

import fr.hyriode.hyrame.utils.PacketUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class HyriSignGUI {

    /** Sign's lines */
    private String[] lines;

    /** Sign's complete callback */
    private final Callback completeCallback;

    /**
     * Constructor of {@link HyriSignGUI}
     *
     * @param completeCallback - Callback
     */
    public HyriSignGUI(Callback completeCallback) {
        this.completeCallback = completeCallback;
    }

    /**
     * Set default gui lines
     *
     * @param lines - Default lines
     * @return - Instance of this (useful for inline pattern)
     */
    public HyriSignGUI withLines(String... lines) {
        if (lines.length == 4) {
            this.lines = lines;
        } else {
            throw new IllegalArgumentException("Signs must have 4 lines !");
        }
        return this;
    }

    /**
     * Open the gui for a player
     *
     * @param player - Player
     */
    public void open(Player player) {
        final Location location = player.getLocation();
        final BlockPosition blockPosition = new BlockPosition(location.getBlockX(), 1, location.getBlockZ());
        final PacketPlayOutBlockChange blockChangePacket = new PacketPlayOutBlockChange(((CraftWorld) player.getWorld()).getHandle(), blockPosition);

        blockChangePacket.block = Blocks.STANDING_SIGN.getBlockData();

        PacketUtil.sendPacket(player, blockChangePacket);

        final IChatBaseComponent[] components = CraftSign.sanitizeLines(this.lines);
        final TileEntitySign sign = new TileEntitySign();

        sign.a(new BlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()));

        System.arraycopy(components, 0, sign.lines, 0, sign.lines.length);

        PacketUtil.sendPacket(player, sign.getUpdatePacket());

        final PacketPlayOutOpenSignEditor openSignEditorPacket = new PacketPlayOutOpenSignEditor(blockPosition);

        PacketUtil.sendPacket(player, openSignEditorPacket);

        HyriSignGUIManager.get().addGUI(player.getUniqueId(), this);
    }

    /**
     * Get sign callback
     *
     * @return - Sign callback
     */
    public Callback getCompleteCallback() {
        return this.completeCallback;
    }

    @FunctionalInterface
    public interface Callback {

        /**
         * Fired when sign gui is closed
         *
         * @param player - Player
         * @param lines - Lines provided
         */
        void call(Player player, String[] lines);

    }

}
