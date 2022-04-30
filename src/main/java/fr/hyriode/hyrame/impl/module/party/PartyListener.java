package fr.hyriode.hyrame.impl.module.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.party.event.*;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static fr.hyriode.hyrame.impl.module.party.PartyModule.createMessage;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 29/04/2022 at 20:12
 */
public class PartyListener {

    @HyriEventHandler
    public void onJoin(HyriPartyJoinEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID newMember = event.getMember();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(newMember);

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            BaseComponent[] message;
            if (member.equals(newMember)) {
                message = createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.join-new-member").getForPlayer(player).replace("%player%", HyriAPI.get().getPlayerManager().getPlayer(party.getLeader()).getNameWithRank())));
            } else {
                message = createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.join-member").getForPlayer(player).replace("%player%", account.getNameWithRank())));
            }

            player.spigot().sendMessage(message);
        }
    }

    @HyriEventHandler
    public void onLeave(HyriPartyLeaveEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID oldMemberId = event.getMember();
        final Player oldMember = Bukkit.getPlayer(oldMemberId);
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(oldMemberId);

        if (oldMember != null) {
            oldMember.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.left-old-member").getForPlayer(oldMember).replace("%player%", HyriAPI.get().getPlayerManager().getPlayer(party.getLeader()).getNameWithRank()))));
        }

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.left-member").getForPlayer(player).replace("%player%", account.getNameWithRank()))));
        }
    }

    @HyriEventHandler
    public void onKick(HyriPartyKickEvent event) {
        final UUID kicked = event.getMember();
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer kickedAccount = HyriAPI.get().getPlayerManager().getPlayer(kicked);
        final IHyriPlayer kicker = HyriAPI.get().getPlayerManager().getPlayer(event.getKicker());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.kick-member").getForPlayer(player)
                        .replace("%player%", kicker.getNameWithRank())
                        .replace("%kicked%", kickedAccount.getNameWithRank()))));
            }
        }

        final Player player = Bukkit.getPlayer(kicked);

        if (player != null) {
            final IHyriPlayer leader = HyriAPI.get().getPlayerManager().getPlayer(party.getLeader());

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.kick-kicked").getForPlayer(player).replace("%player%", leader.getNameWithRank()))));
        }
    }

    @HyriEventHandler
    public void onLeader(HyriPartyLeaderEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer oldLeader = HyriAPI.get().getPlayerManager().getPlayer(event.getOldLeader());
        final IHyriPlayer newLeader = HyriAPI.get().getPlayerManager().getPlayer(event.getNewLeader());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.lead-transfer").getForPlayer(player)
                        .replace("%old_leader%", oldLeader.getNameWithRank())
                        .replace("%new_leader%", newLeader.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onDisband(HyriPartyDisbandEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID leaderId = event.getParty().getLeader();
        final IHyriPlayer leader = HyriAPI.get().getPlayerManager().getPlayer(leaderId);

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                if (player.getUniqueId().equals(leaderId)) {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.disband-leader").getForPlayer(player))));
                } else {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.disband-member").getForPlayer(player).replace("%player%", leader.getNameWithRank()))));
                }
            }
        }

        HyriAPI.get().getPartyManager().removeParty(party.getId());
    }

    @HyriEventHandler
    public void onPromote(HyriPartyPromoteEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(event.getMember());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.promote-player").getForPlayer(player).replace("%player%", account.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onDemote(HyriPartyDemoteEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(event.getMember());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.demote-player").getForPlayer(player).replace("%player%", account.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onChat(HyriPartyChatEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final HyriPartyChatEvent.Action action = event.getAction();

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.chat-" + (action == HyriPartyChatEvent.Action.ENABLED ? "enabled" : "disabled")).getForPlayer(player))));
            }
        }
    }

    @HyriEventHandler
    public void onAccess(HyriPartyAccessEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.access-" + (party.isPrivate() ? "private" : "public")).getForPlayer(player))));
            }
        }
    }

    @HyriEventHandler
    public void onWarp(HyriPartyWarpEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer leader = HyriAPI.get().getPlayerManager().getPlayer(party.getLeader());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.party.warp-members").getForPlayer(player).replace("%player%", leader.getNameWithRank()))));
            }
        }
    }

}
