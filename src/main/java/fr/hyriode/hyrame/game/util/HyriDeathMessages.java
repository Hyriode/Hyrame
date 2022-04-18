package fr.hyriode.hyrame.game.util;

import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 15/04/2022 at 09:12
 */
public class HyriDeathMessages {

    private static final String PLAYER_PLACEHOLDER = "%player%";
    private static final String KILLER_PLACEHOLDER = "%killer%";
    private static final String SECOND_KILLER_PLACEHOLDER = "%second_killer%";

    private static final String MESSAGE_PREFIX = "message.death.";
    private static final BiFunction<String, Player, String> PROVIDER = (key, player) -> HyriLanguageMessage.get(MESSAGE_PREFIX + key).getForPlayer(player);

    /**
     * Create a death message from the reason of the death and the list of killers
     *
     * @param deadGamePlayer The dead player
     * @param target The target of the message
     * @param deathReason The reason of the death
     * @param lastHitters The last hitters
     * @return THe created death message
     */
    public static String createDeathMessage(HyriGamePlayer deadGamePlayer, Player target, HyriGameDeathEvent.Reason deathReason, List<HyriLastHitterProtocol.LastHitter> lastHitters) {
        String result = null;
        if (lastHitters != null && lastHitters.size() > 0) {
            if (deathReason == HyriGameDeathEvent.Reason.VOID) {
                result = PROVIDER.apply("player-and-void", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.FALL) {
                result = PROVIDER.apply("player-and-fall", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.BLOCK_EXPLOSION || deathReason == HyriGameDeathEvent.Reason.ENTITY_EXPLOSION) {
                result = PROVIDER.apply("player-and-explosion", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.LAVA) {
                result = PROVIDER.apply("player-and-lava", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.FIRE) {
                result = PROVIDER.apply("player-and-fire", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.LIGHTNING) {
                result = PROVIDER.apply("player-and-lightning", target);
            } else {
                final int size = lastHitters.size();

                if (size == 1) {
                    result = PROVIDER.apply("player", target);
                } else {
                    result = PROVIDER.apply("players", target).replace(SECOND_KILLER_PLACEHOLDER, lastHitters.get(1).asGamePlayer().formatNameWithTeam());
                }
            }

            result = result.replace(KILLER_PLACEHOLDER, lastHitters.get(0).asGamePlayer().formatNameWithTeam());
        } else {
            if (deathReason == HyriGameDeathEvent.Reason.VOID) {
                result = PROVIDER.apply("void", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.FALL) {
                result = PROVIDER.apply("fall", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.BLOCK_EXPLOSION || deathReason == HyriGameDeathEvent.Reason.ENTITY_EXPLOSION) {
                result = PROVIDER.apply("explosion", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.LAVA) {
                result = PROVIDER.apply("lava", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.FIRE) {
                result = PROVIDER.apply("fire", target);
            } else if (deathReason == HyriGameDeathEvent.Reason.LIGHTNING) {
                result = PROVIDER.apply("lightning", target);
            }
        }

        if (result == null) {
            result = PROVIDER.apply("unknown", target);
        }

        return result.replace(PLAYER_PLACEHOLDER, deadGamePlayer.formatNameWithTeam());
    }

}

