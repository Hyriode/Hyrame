package fr.hyriode.hyrame.impl.module.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.friend.IHyriFriend;
import fr.hyriode.api.friend.IHyriFriendHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

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
        this.handleArgument(ctx, "add %player_online%", this.addFriend(player, friendHandler));
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

            if (target.hasNickname() && !account.getRank().isStaff()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(HyriLanguageMessage.get("message.friend.doesnt-accept").getForPlayer(player).replace("%player%", target.getNickname().getName()))));
                return;
            }

            if (!target.getSettings().isFriendRequestsEnabled() && !account.getRank().isStaff()) {
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

        final Pagination<ListedFriend> showingFriends = new Pagination<>(FRIENDS_LIST_SIZE);

        for (IHyriFriend friend : friends) {
            final UUID playerId = friend.getUniqueId();
            final IHyriPlayer cachedAccount = HyriAPI.get().getPlayerManager().getPlayerFromRedis(playerId);
            final boolean online = cachedAccount != null && cachedAccount.isOnline();
            final String prefix = HyriAPI.get().getPlayerManager().getPrefix(playerId);

            showingFriends.add(new ListedFriend(playerId, online, prefix));
        }

        showingFriends.sort(Comparator.comparing(friend -> !friend.isOnline()));

        player.spigot().sendMessage(createMessage(builder -> {
            for (ListedFriend friend : showingFriends.getPageContent(page)) {
                final UUID uuid = friend.getUniqueId();

                if (friend.isOnline()) {
                    final IHyriPlayer account = IHyriPlayer.get(uuid);
                    final String server = account.getCurrentServer();

                    builder.append(HyriLanguageMessage.get("message.friend.list-player").getForPlayer(player)
                            .replace("%player%", account.getNameWithRank())
                            .replace("%server%", server != null ? server : "?"));
                } else {
                    builder.append(HyriLanguageMessage.get("message.friend.list-player-offline").getForPlayer(player)
                            .replace("%player%", friend.getPrefix()));
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

            addCommandLine(builder, account, "add", "add <player>", true);
            addCommandLine(builder, account, "remove", "remove <player>", true);
            addCommandLine(builder, account, "list", "list <page>", false);
        });
    }

    private static void addCommandLine(ComponentBuilder builder, IHyriPlayer player, String suggest, String arguments, boolean newLine) {
        builder.append("/f " + arguments).color(ChatColor.DARK_PURPLE).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/f " + suggest + " "))
                .append(" - ").color(ChatColor.GRAY).event((ClickEvent) null)
                .append(HyriLanguageMessage.get("message.friend." + suggest).getForPlayer(player)).color(ChatColor.LIGHT_PURPLE)
                .append(newLine ? "\n" : "");
    }

    private static class ListedFriend {

        private final UUID uuid;
        private final boolean online;
        private final String prefix;

        public ListedFriend(UUID uuid, boolean online, String prefix) {
            this.uuid = uuid;
            this.online = online;
            this.prefix = prefix;
        }

        public UUID getUniqueId() {
            return this.uuid;
        }

        public boolean isOnline() {
            return this.online;
        }

        public String getPrefix() {
            return this.prefix;
        }

    }

}
