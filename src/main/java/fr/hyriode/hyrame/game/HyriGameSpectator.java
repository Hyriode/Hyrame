package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent;
import fr.hyriode.hyrame.packet.PacketUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action.ADD;
import static fr.hyriode.hyrame.game.event.player.HyriGameSpectatorEvent.Action.REMOVE;

/**
 * Created by AstFaster
 * on 18/08/2022 at 11:43
 */
public class HyriGameSpectator {

    /** Player is spectating */
    private boolean spectator;

    /** The player object */
    protected Player player;
    /** The unique id of the player */
    protected final UUID uniqueId;

    /**
     * The constructor of a {@link HyriGameSpectator}
     *
     * @param player Spigot player
     */
    public HyriGameSpectator(Player player) {
        this.player = player;
        this.uniqueId = this.player.getUniqueId();
    }

    /**
     * Hide player to other players
     */
    public void hide() {
        this.hide(false);
    }

    /**
     * Hide player to other players
     *
     * @param removeFromTabList If <code>true</code> the player will not be removed from the tab list
     */
    public void hide(boolean removeFromTabList) {
        if (!(this.player instanceof CraftPlayer)) {
            return;
        }

        for (HyriGamePlayer gamePlayer : IHyrame.get().getGame().getPlayers()) {
            final Player target = gamePlayer.getPlayer();

            if (target == this.player) {
                continue;
            }

            target.hidePlayer(this.player);

            if (!removeFromTabList) {
                PacketUtil.sendPacket(target, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) this.player).getHandle()));
            }
        }
    }

    /**
     * Show player to other players
     */
    public void show() {
        for (HyriGamePlayer gamePlayer : IHyrame.get().getGame().getPlayers()) {
            gamePlayer.getPlayer().showPlayer(this.player);
        }
    }

    /**
     * Get player {@link UUID}
     *
     * @return Player {@link UUID}
     */
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    /**
     * Get Spigot player object
     *
     * @return {@link Player} object
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Check if player is in spectator
     *
     * @return <code>true</code> if player is spectating
     */
    public boolean isSpectator() {
        return this.spectator;
    }

    /**
     * Change player spectator mode
     *
     * @param spectator <code>true</code> to toggle
     */
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;

        HyriAPI.get().getEventBus().publish(new HyriGameSpectatorEvent(IHyrame.get().getGame(), this, spectator ? ADD : REMOVE));
    }

}
