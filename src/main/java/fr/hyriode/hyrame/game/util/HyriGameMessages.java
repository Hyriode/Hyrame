package fr.hyriode.hyrame.game.util;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.event.player.HyriGameDeathEvent;
import fr.hyriode.hyrame.game.protocol.HyriLastHitterProtocol;
import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 15/04/2022 at 09:12
 */
public class HyriGameMessages {

    private static final String PLAYER_PLACEHOLDER = "%player%";
    private static final String KILLER_PLACEHOLDER = "%killer%";
    private static final String SECOND_KILLER_PLACEHOLDER = "%second_killer%";

    private static final String MESSAGE_PREFIX = "message.death.";
    private static final BiFunction<String, Player, String> PROVIDER = (key, player) -> HyriLanguageMessage.get(MESSAGE_PREFIX + key).getValue(player);

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

    private static BaseComponent[] createFramedMessage(HyriGame<?> game, Consumer<ComponentBuilder> builderConsumer) {
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE)
                .color(net.md_5.bungee.api.ChatColor.DARK_AQUA)
                .strikethrough(true)
                .append("\n")
                .reset();

        for (int i = 0; i <= (Symbols.HYPHENS_LINE.length() - game.getDisplayName().length()) / 2; i++) {
            builder.append("  ");
        }

        builder.append(game.getDisplayName()).color(ChatColor.AQUA).bold(true).append("\n\n");

        builderConsumer.accept(builder);

        return builder.append("\n")
                .reset()
                .append(Symbols.HYPHENS_LINE)
                .color(net.md_5.bungee.api.ChatColor.DARK_AQUA)
                .strikethrough(true)
                .create();
    }

    public static BaseComponent[] createDescription(HyriGame<?> game, Player target) {
        return createFramedMessage(game, builder -> builder.reset().append(game.getDescription().getValue(target)));
    }

    public static BaseComponent[] createWinMessage(HyriGame<?> game, Player target, HyriGameTeam winner, List<String> stats, List<String> rewards) {
        return createFramedMessage(game, builder -> {
            final String winnerLine = ChatColor.GOLD + HyriLanguageMessage.get("message.game.end.winner").getValue(target) + ChatColor.GRAY + " - " + winner.getFormattedDisplayName(target);

            for (int i = 0; i <= (Symbols.HYPHENS_LINE.length() - winnerLine.length()) / 2 + 2; i++) {
                builder.append("  ");
            }

            builder.append(winnerLine);
            builder.append("\n");

            if (stats != null && stats.size() > 0) {
                builder.append("\n");

                final int space = (Symbols.HYPHENS_LINE.length() - ChatColor.stripColor(stats.get(0)).length()) / 2;

                for (String statistic : stats) {
                    for (int i = 0; i <= space; i++) {
                        builder.append("  ");
                    }

                    builder.append(statistic).append("\n");
                }
            }

            if (rewards != null && rewards.size() > 0) {
                builder.append("\n");

                final String rewardsLine = HyriLanguageMessage.get("message.game.end.rewards").getValue(target);
                final int space = (Symbols.HYPHENS_LINE.length() - ChatColor.stripColor(rewardsLine).length()) / 2 - 3;

                for (int i = 0; i <= space; i++) {
                    builder.append("  ");
                }

                builder.append(rewardsLine)
                        .color(ChatColor.GREEN)
                        .bold(true)
                        .append("")
                        .reset()
                        .append("\n");

                for (String reward : rewards) {
                    for (int i = 0; i <= space - 2; i++) {
                        builder.append("  ");
                    }

                    builder.append("- ").color(ChatColor.WHITE).append(reward).reset().append("\n");
                }
            }
        });
    }

}

