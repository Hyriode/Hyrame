package fr.hyriode.hyrame.impl.host.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.host.HostData;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.host.IHostController;
import fr.hyriode.hyrame.impl.HyramePlugin;
import fr.hyriode.hyrame.impl.host.item.HostSettingsItem;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Function;

/**
 * Created by AstFaster
 * on 04/08/2022 at 09:39
 */
public class HostCommand extends HyriCommand<HyramePlugin> {

    private static final Function<Player, BaseComponent[]> HELP = player -> new HelpCommandCreator("h", "host", player)
            .addArgumentsLine("say", "say <message>")
            .addArgumentsLine("op-deop", "op/deop <player>")
            .addArgumentsLine("op-list", "oplist")
            .addArgumentsLine("kick", "kick <player>")
            .addArgumentsLine("ban-unban", "ban/unban <player>")
            .addArgumentsLine("ban-list", "banlist")
            .addArgumentsLine("whitelist-add-remove", "wl add/remove <player>")
            .addArgumentsLine("whitelist-list-clear", "wl list/clear")
            .addArgumentsLine("give", "give <item> <amount>")
            .addArgumentsLine("heal", "heal")
            .create();

    private final IHostController controller;

    public HostCommand(HyramePlugin plugin) {
        super(plugin, new HyriCommandInfo("host")
                .withAliases("h")
                .withDescription("Command used to interact with host system")
                .withType(HyriCommandType.PLAYER)
                .withUsage(sender -> HELP.apply((Player) sender), false)
                .withPermission(account -> {
                    final HostData hostData = HyriAPI.get().getServer().getHostData();

                    return hostData != null && (hostData.getOwner().equals(account.getUniqueId()) || hostData.getSecondaryHosts().contains(account.getUniqueId()));
                })
                .asynchronous());
        this.controller = this.plugin.getHyrame().getHostController();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final HostData hostData = this.controller.getHostData();
        final boolean secondaryHost = hostData.getSecondaryHosts().contains(player.getUniqueId());
        final HyriGame<?> game = this.plugin.getHyrame().getGameManager().getCurrentGame();

        this.handleArgument(ctx, "kick %player_server%", output -> {
            final Player target = output.get(Player.class);

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            HyriAPI.get().getLobbyAPI().sendPlayerToLobby(target.getUniqueId());

            player.sendMessage(HyrameMessage.HOST_KICK_MESSAGE.asString(account).replace("%player%", IHyriPlayer.get(target.getUniqueId()).getNameWithRank()));
        });

        this.handleArgument(ctx, "ban %player%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (target.getPlayersBannedFromHost().contains(targetId)) {
                player.sendMessage(HyrameMessage.HOST_ALREADY_BANNED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
                return;
            }

            account.addPlayerBannedFromHost(targetId);
            account.update();

            player.sendMessage(HyrameMessage.HOST_BAN_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
        });

        this.handleArgument(ctx, "unban %player%", output -> {
            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (!target.getPlayersBannedFromHost().contains(targetId)) {
                player.sendMessage(HyrameMessage.HOST_NOT_BANNED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
                return;
            }

            account.removePlayerBannedFromHost(targetId);
            account.update();

            player.sendMessage(HyrameMessage.HOST_UNBAN_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
        });

        this.handleArgument(ctx, "banlist", output -> {
            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                    .append("\n").strikethrough(false);

            builder.append(HyrameMessage.HOST_BAN_LIST_MESSAGE.asString(account)).append("\n");

            for (UUID bannedPlayer : account.getPlayersBannedFromHost()) {
                builder.append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY).append(" ").append(IHyriPlayer.get(bannedPlayer).getNameWithRank()).append("\n");
            }

            builder.append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

            player.spigot().sendMessage(builder.create());
        });

        this.handleArgument(ctx, "op %player_server%", output -> {
            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final Player target = output.get(Player.class);
            final UUID targetId = target.getUniqueId();
            final IHyriPlayer targetAccount = IHyriPlayer.get(targetId);

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (hostData.getSecondaryHosts().contains(targetId)) {
                player.sendMessage(HyrameMessage.HOST_ALREADY_OP_MESSAGE.asString(account).replace("%player%", targetAccount.getNameWithRank()));
                return;
            }

            hostData.addSecondaryHost(targetId);

            this.plugin.getHyrame().getItemManager().giveItem(target, 7, HostSettingsItem.class);

            player.sendMessage(HyrameMessage.HOST_OP_MESSAGE.asString(account).replace("%player%", targetAccount.getNameWithRank()));

            HyriAPI.get().getHyggdrasilManager().sendData();
        });

        this.handleArgument(ctx, "deop %player%", output -> {
            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final IHyriPlayer targetAccount = output.get(IHyriPlayer.class);

            if (targetAccount.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (!hostData.getSecondaryHosts().remove(targetAccount.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_NOT_OP_MESSAGE.asString(account).replace("%player%", targetAccount.getNameWithRank()));
            } else {
                player.sendMessage(HyrameMessage.HOST_DEOP_MESSAGE.asString(account).replace("%player%", targetAccount.getNameWithRank()));

                final Player target = Bukkit.getPlayer(targetAccount.getUniqueId());

                if (target != null) {
                    target.getInventory().setItem(7, null);
                    target.closeInventory();
                }

                HyriAPI.get().getHyggdrasilManager().sendData();
            }
        });

        this.handleArgument(ctx, "oplist", output -> {
            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                    .append("\n").strikethrough(false);

            builder.append(HyrameMessage.HOST_OP_LIST_MESSAGE.asString(account)).append("\n");

            for (UUID host : hostData.getSecondaryHosts()) {
                builder.append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY).append(" ").append(IHyriPlayer.get(host).getNameWithRank()).append("\n");
            }

            builder.append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

            player.spigot().sendMessage(builder.create());
        });

        this.handleArgument(ctx, "wl add %player%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (hostData.getWhitelistedPlayers().contains(targetId)) {
                player.sendMessage(HyrameMessage.HOST_ALREADY_WHITELISTED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
                return;
            }

            hostData.addWhitelistedPlayer(targetId);

            player.sendMessage(HyrameMessage.HOST_WHITELISTED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));

            HyriAPI.get().getHyggdrasilManager().sendData();
        });

        this.handleArgument(ctx, "wl remove %player%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final boolean result = hostData.getWhitelistedPlayers().remove(target.getUniqueId());

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(HyrameMessage.HOST_HIMSELF_ERROR_MESSAGE.asString(account));
                return;
            }

            if (!result) {
                player.sendMessage(HyrameMessage.HOST_NOT_WHITELISTED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
            } else {
                player.sendMessage(HyrameMessage.HOST_UN_WHITELISTED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));

                HyriAPI.get().getHyggdrasilManager().sendData();
            }
        });

        this.handleArgument(ctx, "wl clear", output -> {
            hostData.getWhitelistedPlayers().clear();

            player.sendMessage(HyrameMessage.HOST_WHITELIST_CLEARED_MESSAGE.asString(account));

            HyriAPI.get().getHyggdrasilManager().sendData();
        });

        this.handleArgument(ctx, "wl list", output -> {
            final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                    .append("\n").strikethrough(false);

            builder.append(HyrameMessage.HOST_WHITELIST_LIST_MESSAGE.asString(player)).append("\n");

            for (UUID whitelistedPlayer : hostData.getWhitelistedPlayers()) {
                builder.append(" ").append(Symbols.DOT_BOLD).color(ChatColor.DARK_GRAY).append(" ").append(IHyriPlayer.get(whitelistedPlayer).getNameWithRank()).append("\n");
            }

            builder.append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

            player.spigot().sendMessage(builder.create());
        });

        this.handleArgument(ctx, "say %sentence%", output -> this.controller.sendHostMessage(player, output.get(String.class)));
        this.handleArgument(ctx, "give %input% %integer%", output -> {
            if (game.getState() != HyriGameState.PLAYING) {
                player.sendMessage(HyrameMessage.HOST_GAME_ERROR_MESSAGE.asString(player));
                return;
            }

            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            final String materialName = output.get(String.class);
            final int amount = output.get(Integer.class);

            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                material = Bukkit.getUnsafe().getMaterialFromInternalName(materialName);
            }

            if (material == null) {
                player.sendMessage(HyrameMessage.INVALID_ARGUMENT.asString(player).replace("%arg%", materialName));
                return;
            }

            final ItemStack itemStack = new ItemStack(material, amount);

            for (HyriGamePlayer gamePlayer : game.getPlayers()) {
                if (!gamePlayer.isOnline() || gamePlayer.isSpectator() || gamePlayer.isDead()) {
                    continue;
                }

                gamePlayer.getPlayer().getInventory().addItem(itemStack);
            }
        });
        this.handleArgument(ctx, "heal", output -> {
            if (game.getState() != HyriGameState.PLAYING) {
                player.sendMessage(HyrameMessage.HOST_GAME_ERROR_MESSAGE.asString(player));
                return;
            }

            if (secondaryHost) {
                player.sendMessage(ChatColor.RED + HyrameMessage.PERMISSION_ERROR.asString(player));
                return;
            }

            for (HyriGamePlayer gamePlayer : game.getPlayers()) {
                gamePlayer.getPlayer().setHealth(20.0D);
            }

            player.sendMessage(HyrameMessage.HOST_HEAL_MESSAGE.asString(player));
        });
        this.handleArgument(ctx, "help", output -> player.spigot().sendMessage(HELP.apply(player)));
        this.handleArgument(ctx, "", output -> player.spigot().sendMessage(HELP.apply(player)));
    }

}
