package fr.hyriode.hyrame.impl.module.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.party.HyriPartyDisbandReason;
import fr.hyriode.api.party.HyriPartyRank;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.party.IHyriPartyManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerManager;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.TimeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static fr.hyriode.hyrame.impl.module.party.PartyModule.createMessage;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class PartyCommand extends HyriCommand<HyramePlugin> {

    private final IHyriPartyManager partyManager;

    public PartyCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("party")
                .withAliases("p", "groupe", "group", "partie")
                .withDescription("The command used to create a party and interact with it")
                .withType(HyriCommandType.PLAYER)
                .withUsage(sender -> getHelp((Player) sender), false)
                .asynchronous());
        this.partyManager = HyriAPI.get().getPartyManager();
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
        final IHyriParty party = HyriAPI.get().getPartyManager().getParty(account.getParty());

        this.handleArgument(ctx, "leave", this.partyOutput(ctx, party, output -> {
            if (party.isLeader(playerId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.cant-leave-leader").getForPlayer(account))));
                return;
            }

            party.removeMember(playerId);
        }));

        this.handleArgument(ctx, "disband", this.partyOutput(ctx, party, output -> {
            if (!party.getRank(playerId).canDisband()) {
                this.dontHavePermission(player);
                return;
            }

            party.disband(HyriPartyDisbandReason.NORMAL);
        }));

        this.handleArgument(ctx, "kick %player_online%", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (!this.isSameParty(player, party, target)) {
                return;
            }

            final HyriPartyRank playerRank = party.getRank(playerId);

            if (!playerRank.canKick()) {
                this.dontHavePermission(player);
                return;
            }

            if (party.getRank(targetId).isSuperior(playerRank)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.cant-kick").getForPlayer(account))));
                return;
            }

            party.kickMember(targetId, playerId);
        }));

        this.handleArgument(ctx, "lead %player_online%", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (!this.isSameParty(player, party, target)) {
                return;
            }

            if (!party.isLeader(playerId)) {
                this.dontHavePermission(player);
                return;
            }

            party.setLeader(targetId);
        }));

        this.handleArgument(ctx, "promote %player_online%", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (this.editRank(player, party, target)) {
                if (party.promoteMember(targetId) != null) {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.cant-promote").getForPlayer(player).replace("%target%", target.getNameWithRank()))));
                }
            }
        }));

        this.handleArgument(ctx, "demote %player_online%", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (this.editRank(player, party, target)) {
                if (party.demoteMember(targetId) != null) {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.cant-demote").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
                }
            }
        }));

        this.handleArgument(ctx, "chat %sentence%", this.partyOutput(ctx, party, output -> this.plugin.getHyrame().getPartyModule().sendPartyMessage(party, player, output.get(String.class))));

        this.handleArgument(ctx, "mute %input%", this.partyOutput(ctx, party, output -> {
            final String mutedInput = output.get(String.class);

            if (!mutedInput.equalsIgnoreCase("on") && !mutedInput.equalsIgnoreCase("off")) {
                player.sendMessage(HyriCommonMessages.INVALID_INPUT.getForPlayer(account));
                return;
            }

            final boolean muted = mutedInput.equalsIgnoreCase("on");

            if (muted == party.isChatEnabled() && muted) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.chat-already-enabled").getForPlayer(player))));
                return;
            }

            if (muted == party.isChatEnabled() && !muted) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.chat-already-disabled").getForPlayer(player))));
                return;
            }

            party.setChatEnabled(muted);
        }));

        this.handleArgument(ctx, "stream", this.partyOutput(ctx, party, output -> {
            if (!party.isLeader(playerId)) {
                this.dontHavePermission(player);
                return;
            }

            if (!account.getRank().isSuperior(HyriPlayerRankType.PARTNER)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.partner-feature").getForPlayer(account).replace("%rank%", HyriPlayerRankType.PARTNER.getDefaultPrefix()))));
                return;
            }

            party.setPrivate(!party.isPrivate());
        }));

        this.handleArgument(ctx, "warp", this.partyOutput(ctx, party, output -> {
            if (!party.isLeader(playerId)) {
                this.dontHavePermission(player);
                return;
            }

            party.warp(account.getCurrentServer());
        }));

        this.handleArgument(ctx, "list", this.partyOutput(ctx, party, this.listParty(player, account, party)));
        this.handleArgument(ctx, "info", this.partyOutput(ctx, party, this.listParty(player, account, party)));

        if (ctx.getArgs().length == 0) {
            ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.ERROR, getHelp(player)));
            return;
        }

        this.handleArgument(ctx, "invite %player%", this.invitePlayer(player, account));
        this.handleArgument(ctx, "add %player%", this.invitePlayer(player, account));

        this.handleArgument(ctx, "accept %player_online%", this.joinParty(player, account));
        this.handleArgument(ctx, "join %player_online%", this.joinParty(player, account));

        this.handleArgument(ctx, "deny %player_online%", output -> {
            final IHyriPlayer requester = output.get(IHyriPlayer.class);
            final UUID partyId = requester.getParty();

            this.partyManager.removeInvitation(partyId, playerId);

            if (party == null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-have-target").getForPlayer(account).replace("%player%", requester.getNameWithRank()))));
                return;
            }

            if (!this.partyManager.hasInvitation(partyId, playerId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.no-invitation").getForPlayer(account).replace("%player%", requester.getNameWithRank()))));
                return;
            }

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.deny-target").getForPlayer(account).replace("%player%", requester.getNameWithRank()))));

            final HyriLanguageMessage message = HyriLanguageMessage.get("message.party.deny-sender");

            for (UUID member : party.getMembers().keySet()) {
                PlayerUtil.sendComponent(member, createMessage(builder -> builder.append(message.getForPlayer(HyriAPI.get().getPlayerManager().getPlayer(member)).replace("%player%", account.getNameWithRank()))));
            }
        });

        this.handleArgument(ctx, "help", output -> player.spigot().sendMessage(getHelp(player)));
        this.handleArgument(ctx, "%player_online%", this.invitePlayer(player, account));
    }

    private Consumer<HyriCommandOutput> partyOutput(HyriCommandContext ctx, IHyriParty party, Consumer<HyriCommandOutput> action) {
        return output -> {
            final Player player = (Player) ctx.getSender();

            if (party != null) {
                action.accept(output);
            } else {
                ctx.setResult(new HyriCommandResult(HyriCommandResult.Type.SUCCESS));
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-have").getForPlayer(player))));
            }
        };
    }

    private Consumer<HyriCommandOutput> listParty(Player player, IHyriPlayer account, IHyriParty party) {
        return output -> {
            final HyriLanguage language = account.getSettings().getLanguage();
            final IHyriPlayerManager playerManager = HyriAPI.get().getPlayerManager();

            player.spigot().sendMessage(createMessage(builder -> {
                builder.append(HyriLanguageMessage.get("message.party.creation-date").getValue(language).replace("%date%", TimeUtil.formatDate(party.getCreationDate())))
                        .append("\n")
                        .append(HyriLanguageMessage.get("message.party.members-amount").getValue(language).replace("%amount%", String.valueOf(party.getMembers().size())))
                        .append("\n")
                        .append(HyriLanguageMessage.get("message.party.current-server").getValue(language).replace("%server%", party.getServer()))
                        .append("\n")
                        .append(HyriPartyRank.LEADER.getDisplay(language) + ": ").color(ChatColor.DARK_AQUA)
                        .append(playerManager.getPlayer(party.getLeader()).getNameWithRank())
                        .append("\n")
                        .append(HyriPartyRank.OFFICER.getDisplay(language) + ": ").color(ChatColor.DARK_AQUA);

                final List<UUID> officers = party.getMembers(HyriPartyRank.OFFICER);
                for (UUID officer : officers) {
                    builder.append(playerManager.getPlayer(officer).getNameWithRank()).append(" ");
                }

                builder.append("\n")
                        .append(HyriPartyRank.MEMBER.getDisplay(language) + ": ").color(ChatColor.DARK_AQUA);

                final List<UUID> members = party.getMembers(HyriPartyRank.MEMBER);
                for (UUID member : members) {
                    builder.append(playerManager.getPlayer(member).getNameWithRank()).append(" ");
                }
            }));
        };
    }

    private boolean editRank(Player player, IHyriParty party, IHyriPlayer target) {
        final UUID targetId = target.getUniqueId();

        if (!this.isSameParty(player, party, target)) {
            return false;
        }

        final HyriPartyRank playerRank = party.getRank(player.getUniqueId());

        if (!playerRank.canEditRank() || party.getRank(targetId).isSuperior(playerRank)) {
            this.dontHavePermission(player);
            return false;
        }
        return true;
    }

    private boolean isSameParty(Player player, IHyriParty party, IHyriPlayer target) {
        if (!target.getParty().equals(party.getId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.not-in-party").getForPlayer(player).replace("%player%",target.getNameWithRank()))));
            return false;
        }
        return true;
    }

    private void dontHavePermission(Player player) {
        player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.no-permission").getForPlayer(player))));
    }

    private Consumer<HyriCommandOutput> invitePlayer(Player player, IHyriPlayer account) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (!target.isOnline()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.not-online").getForPlayer(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            IHyriParty party = this.partyManager.getParty(account.getParty());
            if (party == null) {
                party = this.partyManager.createParty(playerId);

                account.setParty(party.getId());
                account.update();
            }

            if (playerId.equals(targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.yourself").getForPlayer(account))));
                return;
            }

            if (HyriAPI.get().getPartyManager().hasInvitation(party.getId(), targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.already-invited").getForPlayer(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            if (party.hasMember(targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.already-in").getForPlayer(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            if (target.hasNickname() && !account.getRank().isStaff()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-accept").getForPlayer(account).replace("%player%", target.getNickname().getName()))));
                return;
            }

            if (!target.getSettings().isPartyRequestsEnabled() && !account.getRank().isStaff()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-accept").getForPlayer(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            if (!party.getRank(playerId).canSendInvitations()) {
                this.dontHavePermission(player);
                return;
            }

            if (party.getMembers().size() >= PartyLimit.getMaxSlots(account)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.no-space").getForPlayer(account))));
                return;
            }

            this.partyManager.sendInvitation(party.getId(), playerId, targetId);

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.invitation-sent").getForPlayer(account).replace("%player%", target.getNameWithRank()))));
        };
    }

    private Consumer<HyriCommandOutput> joinParty(Player player, IHyriPlayer account) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer requester = output.get(IHyriPlayer.class);
            final UUID partyId = requester.getParty();
            final IHyriParty party = this.partyManager.getParty(partyId);

            if (HyriAPI.get().getPartyManager().getParty(account.getParty()) != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.already-in-other").getForPlayer(account))));
                this.partyManager.removeInvitation(partyId, playerId);
                return;
            }

            if (party == null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.doesnt-have-target").getForPlayer(account).replace("%player%", requester.getNameWithRank()))));
                this.partyManager.removeInvitation(partyId, playerId);
                return;
            }

            if (!this.partyManager.hasInvitation(partyId, playerId) && party.isPrivate()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.no-invitation").getForPlayer(account).replace("%player%", requester.getNameWithRank()))));
                this.partyManager.removeInvitation(partyId, playerId);
                return;
            }

            if (party.getMembers().size() >= PartyLimit.getMaxSlots(account)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.no-space").getForPlayer(account))));
                this.partyManager.removeInvitation(partyId, playerId);
                return;
            }

            this.partyManager.removeInvitation(partyId, playerId);

            party.addMember(playerId, HyriPartyRank.MEMBER);
        };
    }

    private static BaseComponent[] getHelp(Player player) {
        return createMessage(builder -> {
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

            addCommandLine(builder, account, "invite", "invite <player>", true);
            addCommandLine(builder, account, "kick", "kick <player>", true);
            addCommandLine(builder, account, "accept", "accept <player>", true);
            addCommandLine(builder, account, "deny", "deny <player>", true);
            addCommandLine(builder, account, "promote", "promote <player>", true);
            addCommandLine(builder, account, "demote", "demote <player>", true);
            addCommandLine(builder, account, "lead", "lead <player>", true);
            addCommandLine(builder, account, "disband", "disband", true);
            addCommandLine(builder, account, "warp", "warp", true);
            addCommandLine(builder, account, "info", "info", true);
            addCommandLine(builder, account, "chat", "chat <message>", true);
            addCommandLine(builder, account, "mute", "mute <on|off>", true);
            addCommandLine(builder, account, "stream", "stream", false);
        });
    }

    private static void addCommandLine(ComponentBuilder builder, IHyriPlayer player, String suggest, String arguments, boolean newLine) {
        builder.append("/p " + arguments).color(ChatColor.DARK_AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p " + suggest + " "))
                .append(" - ").color(ChatColor.GRAY).event((ClickEvent) null)
                .append(HyriLanguageMessage.get("message.party.command." + suggest).getForPlayer(player)).color(ChatColor.AQUA)
                .append(newLine ? "\n" : "");
    }

}
