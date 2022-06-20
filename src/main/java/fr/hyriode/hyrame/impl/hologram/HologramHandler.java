package fr.hyriode.hyrame.impl.hologram;

import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.reflection.entity.EntityUseAction;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 16/06/2022 at 21:00
 */
public class HologramHandler {

    public static void init(JavaPlugin plugin, IHyrame hyrame) {
        hyrame.getPacketInterceptor().addHandler(PacketType.Play.Client.USE_ENTITY, new IPacketHandler() {
            @Override
            public void onReceive(IPacketContainer container) {
                final Player player = container.getPlayer();
                final int entityId = container.getIntegers().read(0);

                for (Hologram hologram : Hologram.getAll()) {
                    final Consumer<Player> callback = hologram.getInteractionCallback();

                    if (!hologram.containsEntity(entityId) || !hologram.isDistancedFrom(hologram.getLocation(), 1.0D) || callback == null) {
                        continue;
                    }

                    final Object object = container.getValue("action");

                    if (object != null && object.toString().equals(EntityUseAction.INTERACT_AT.name())) {
                        ThreadUtil.backOnMainThread(plugin, () -> callback.accept(player));
                    }
                }
            }
        });
    }

}
