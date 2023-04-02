package fr.hyriode.hyrame.game.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameSpectator;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectedEvent;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.game.util.HyriGameItems;
import fr.hyriode.hyrame.packet.IPacketContainer;
import fr.hyriode.hyrame.packet.IPacketHandler;
import fr.hyriode.hyrame.packet.PacketType;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.VoidPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/02/2022 at 20:23
 */
public class HyriSpectatorProtocol extends HyriGameProtocol implements Listener {

    private IPacketHandler handler;

    public HyriSpectatorProtocol(IHyrame hyrame) {
        super(hyrame, "spectator");
    }

    @SuppressWarnings("unchecked")
    @Override
    void enable() {
        this.handler = new IPacketHandler() {
            @Override
            public void onSend(IPacketContainer container) {
                final Player receiver = container.getPlayer();
                final List<PacketPlayOutPlayerInfo.PlayerInfoData> allData = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) container.getValue("b");

                for (PacketPlayOutPlayerInfo.PlayerInfoData data : allData) {
                    final UUID playerId = data.a().getId();
                    HyriGameSpectator spectator = getGame().getPlayer(playerId);

                    if (spectator == null) {
                        spectator = getGame().getSpectator(playerId);
                    }

                    if (spectator == null) {
                        continue;
                    }

                    if (spectator.isSpectator()) {
                        Reflection.setField("c", data, playerId.equals(receiver.getUniqueId()) ? WorldSettings.EnumGamemode.ADVENTURE : WorldSettings.EnumGamemode.SPECTATOR);
                    }
                }
            }
        };
        this.hyrame.getPacketInterceptor().addHandler(PacketType.Play.Server.PLAYER_INFO, this.handler);

        HyriAPI.get().getEventBus().register(this);
    }

    @Override
    void disable() {
        this.hyrame.getPacketInterceptor().removeHandler(this.handler);

        HyriAPI.get().getEventBus().unregister(this);
    }

    @HyriEventHandler
    public void onPlayerJoin(HyriGameReconnectedEvent event) {
        for (HyriGamePlayer gamePlayer : this.hyrame.getGameManager().getCurrentGame().getPlayers()) {
            if (gamePlayer.isSpectator()) {
                gamePlayer.setSpectator(true);
            }
        }
    }

    @HyriEventHandler
    public void onSpectator(HyriGameSpectatorEvent event) {
        final HyriGameSpectator spectator = event.getSpectator();
        final Player player = spectator.getPlayer();

        if (spectator.getPlayer() instanceof VoidPlayer) {
            return;
        }

        if (event.getAction() == Action.ADD) {
            PlayerUtil.resetPlayer(player, true);
            PlayerUtil.addSpectatorAbilities(player);

            spectator.hide();
            player.spigot().setCollidesWithEntities(false);

            HyriGameItems.SPECTATOR_TELEPORTER.give(this.hyrame, player, 0);
            HyriGameItems.SPECTATOR_SETTINGS.give(this.hyrame, player, 1);
            HyriGameItems.LEAVE.give(this.hyrame, player, 8);

            if (HyriAPI.get().getServer().getAccessibility() != HyggServer.Accessibility.HOST) {
                HyriGameItems.RESTART_GAME.give(this.hyrame, player, 4);
            }
        } else {
            PlayerUtil.resetPlayer(player, true);

            spectator.show();
            player.spigot().setCollidesWithEntities(true);
        }
    }


}
