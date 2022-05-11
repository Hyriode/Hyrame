package fr.hyriode.hyrame.signgui;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.packet.PacketUtil;
import fr.hyriode.hyrame.utils.ThreadUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class SignGUIManager {

    /** Instance of manager */
    private static SignGUIManager instance;

    /** Map of all signs */
    private final Map<UUID, SignGUI> signs;

    /**
     * Constructor of {@link SignGUIManager}
     */
    public SignGUIManager(IHyrame hyrame, JavaPlugin plugin) {
        instance = this;
        this.signs = new HashMap<>();

        hyrame.getPacketInterceptor().addHandler(PacketType.Play.Client.UPDATE_SIGN, new IPacketHandler() {
            @Override
            public void onReceive(IPacketContainer container) {
                final Player player = container.getPlayer();
                final Map<UUID, SignGUI> signs = SignGUIManager.get().getSigns();

                if (signs.containsKey(player.getUniqueId())) {
                    final SignGUI sign = signs.get(player.getUniqueId());
                    final BlockPosition blockPosition = container.getModifier(BlockPosition.class).read(0);
                    final IChatBaseComponent[] components = container.getModifier(IChatBaseComponent[].class).read(0);
                    final List<String> lines = new ArrayList<>();

                    for (IChatBaseComponent component : components) {
                        lines.add(component.getText());
                    }

                    ThreadUtil.backOnMainThread(plugin, () -> {
                        final PacketPlayOutBlockChange blockChangePacket = new PacketPlayOutBlockChange(((CraftWorld) player.getWorld()).getHandle(), blockPosition);

                        blockChangePacket.block = Blocks.AIR.getBlockData();

                        PacketUtil.sendPacket(player, blockChangePacket);

                        sign.getCompleteCallback().call(player, lines.toArray(new String[4]));
                    });

                    signs.remove(player.getUniqueId());
                }
            }
        });
    }

    /**
     * Add a sign gui for a player
     *
     * @param uuid - Player uuid
     * @param sign - Sign
     */
    protected void addGUI(UUID uuid, SignGUI sign) {
        this.signs.put(uuid, sign);
    }

    /**
     * Get all signs
     *
     * @return - A map of signs
     */
    public Map<UUID, SignGUI> getSigns() {
        return this.signs;
    }

    /**
     * Get the instance of this object
     *
     * @return - {@link SignGUIManager} instance
     */
    public static SignGUIManager get() {
        return instance;
    }

}
