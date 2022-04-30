package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.friend.IHyriFriend;
import fr.hyriode.api.friend.IHyriFriendHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static fr.hyriode.hyrame.impl.module.friend.FriendModule.FRIENDS_LIST_SIZE;
import static fr.hyriode.hyrame.impl.module.friend.FriendModule.createMessage;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class FriendCommand extends HyriCommand<HyramePlugin> {

    private final FriendModule friendModule;

    public FriendCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("friend")
                .withAliases("f", "ami", "friends", "amis")
                .withDescription("The command used to manage friends")
                .withType(HyriCommandType.PLAYER)
                .withUsage(sender -> getHelp((Player) sender), false)
                .asynchronous());
        this.friendModule = this.plugin.getHyrame().getFriendModule();
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
        final IHyriFriendHandler friendHandler = HyriAPI.get().getFriendManager().createHandler(playerId);

        this.handleArgument(ctx, "add %player%", this.addFriend(player, friendHandler));

        this.handleArgument(ctx, "accept %player%", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!this.friendModule.checkRequest(player, sender)) {
                return;
            }

            if (friendHandler.getFriends().size() >= FriendLimit.getMaxFriends(account.getRank().getPlayerType())) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.full").getForPlayer(player))));
                return;
            }

            HyriAPI.get().getFriendManager().removeRequest(playerId, sender.getUniqueId());

            friendHandler.addFriend(sender.getUniqueId());

            final HyriLanguageMessage accept = HyriLanguageMessage.get("message.friend.accept");

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(accept.getForPlayer(sender).replace("%player%", account.getNameWithRank()))));

            player.spigot().sendMessage(createMessage(builder -> builder.append(accept.getForPlayer(sender).replace("%player%", sender.getNameWithRank()))));
        });

        this.handleArgument(ctx, "deny %player%", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!this.friendModule.checkRequest(player, sender)) {
                return;
            }

            HyriAPI.get().getFriendManager().removeRequest(playerId, sender.getUniqueId());

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.deny-sender").getForPlayer(sender).replace("%player%", account.getNameWithRank()))));
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.deny-target").getForPlayer(sender).replace("%player%", sender.getNameWithRank()))));
        });

        this.handleArgument(ctx, "remove %player%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (this.friendModule.checkAreNotFriends(friendHandler, player, target)) {
                return;
            }

            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.no-longer").getForPlayer(player).replace("%player%", target.getNameWithRank()))));

            friendHandler.removeFriend(target.getUniqueId());
        });

        this.handleArgument(ctx, "list %integer%", output -> listFriends(output.get(Integer.class), player, friendHandler));
        this.handleArgument(ctx, "list", output -> listFriends(0, player, friendHandler));
        this.handleArgument(ctx, "help", output -> player.spigot().sendMessage(getHelp(player)));
        this.handleArgument(ctx, "%player_online%", this.addFriend(player, friendHandler));
    }

    private Consumer<HyriCommandOutput> addFriend(Player player, IHyriFriendHandler friendHandler) {
        return output -> {
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (target.getUniqueId().equals(playerId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.yourself").getForPlayer(player))));
                return;
            }

            if (this.friendModule.checkAreFriends(friendHandler, player, target)) {
                return;
            }

            if (!target.isOnline()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.not-online").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
                return;
            }

            if (friendHandler.getFriends().size() >= FriendLimit.getMaxFriends(account.getRank().getPlayerType())) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.no-space").getForPlayer(player))));
                return;
            }

            if (!target.getSettings().isFriendRequestsEnabled() || target.hasNickname()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.doesnt-accept").getForPlayer(player).replace("%player%", target.getNameWithRank()))));
                return;
            }

            this.friendModule.sendRequest(player, target);
        };
    }

    private void listFriends(int page, Player player, IHyriFriendHandler friendHandler) {
        final List<IHyriFriend> friends = friendHandler.getFriends();

        if (friends.size() == 0) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.no-friend").getForPlayer(player))));
            return;
        }

        final List<IHyriPlayer> showingFriends = new ArrayList<>();

        int start = page != 0 ? FRIENDS_LIST_SIZE * (page - 1) : 0;

        if (start > friendHandler.getFriends().size()) {
            start = 0;
        }

        for (int i = start; i < 10; i++) {
            if (friends.size() - 1 < i) {
                break;
            }

            final IHyriPlayer friend = HyriAPI.get().getPlayerManager().getPlayer(friends.get(i).getUniqueId());

            if (friend != null) {
                showingFriends.add(friend);
            }
        }

        showingFriends.sort(Comparator.comparing(friend -> !friend.isOnline()));

        player.spigot().sendMessage(createMessage(builder -> {
            for (IHyriPlayer friend : showingFriends) {
                if (friend == null) {
                    continue;
                }

                if (friend.isOnline()) {
                    builder.append(HyriLanguageMessage.get("message.friend.list-player").getForPlayer(player).replace("%player%", friend.getNameWithRank()).replace("%server%", friend.getCurrentServer()));
                } else {
                    builder.append(HyriLanguageMessage.get("message.friend.list-player-offline").getForPlayer(player).replace("%player%", friend.getNameWithRank()));
                }
                if (showingFriends.indexOf(friend) != showingFriends.size() - 1) {
                    builder.append("\n");
                }
            }
        }));
    }

    private static BaseComponent[] getHelp(Player player) {
        return createMessage(builder -> {
            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

            addCommandLine(builder, account, "add", "add <player>");
            addCommandLine(builder, account, "remove", "remove <player>");
            addCommandLine(builder, account, "list", "list <page>");
        });
    }

    private static void addCommandLine(ComponentBuilder builder, IHyriPlayer player, String suggest, String arguments) {
        builder.append("/f " + arguments).color(ChatColor.DARK_PURPLE).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/f " + suggest + " "))
                .append(" - ").color(ChatColor.GRAY).event((ClickEvent) null)
                .append(HyriLanguageMessage.get("message.friend." + suggest).getForPlayer(player)).color(ChatColor.LIGHT_PURPLE)
                .append("\n");
    }

}