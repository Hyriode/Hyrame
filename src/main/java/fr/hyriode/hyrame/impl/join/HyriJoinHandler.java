package fr.hyriode.hyrame.impl.join;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.server.join.HyriJoinResponse;
import fr.hyriode.api.server.join.IHyriJoinHandler;
import fr.hyriode.api.server.join.IHyriJoinManager;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.title.Title;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 19:53
 */
public class HyriJoinHandler implements IHyriJoinHandler {

    private static final String RESPONSE_KEY = "message.join.deny";
    private static final BiFunction<IHyriPlayer, String, String> RESPONSE = (player, key) -> HyriLanguageMessage.get(RESPONSE_KEY + "." + key).getForPlayer(player);

    private final IHyrame hyrame;

    public HyriJoinHandler(IHyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public HyriJoinResponse requestJoin(UUID player, HyriJoinResponse response) {
        final IHyriServer server = HyriAPI.get().getServer();
        final IHyriServer.State state = server.getState();

        if (!state.isAccessible()) {
            return HyriJoinResponse.DENY_STATE;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player);
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();
        final IHyriJoinManager joinManager = HyriAPI.get().getServerManager().getJoinManager();
        final int slots = server.getSlots();

        if (account.isInModerationMode()) {
            return HyriJoinResponse.ALLOW;
        } else if (game != null){
            if (!game.getState().isAccessible()) {
                return HyriJoinResponse.DENY_STATE;
            } else if (game.getPlayers().size() + joinManager.getExpectedPlayers().size() >= game.getMaxPlayers()) {
                return HyriJoinResponse.DENY_FULL;
            }
        } else if (server.getPlayers().size() + joinManager.getExpectedPlayers().size() >= slots && slots != -1) {
            return HyriJoinResponse.DENY_FULL;
        }
        return HyriJoinResponse.ALLOW;
    }

    @Override
    public HyriJoinResponse requestPartyJoin(UUID partyId, HyriJoinResponse response) {
        final IHyriServer server = HyriAPI.get().getServer();
        final IHyriServer.State state = server.getState();

        if (!state.isAccessible()) {
            return HyriJoinResponse.DENY_STATE;
        }

        final IHyriParty party = HyriAPI.get().getPartyManager().getParty(partyId);
        final int partySize = party.getMembers().size();
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();
        final IHyriJoinManager joinManager = HyriAPI.get().getServerManager().getJoinManager();

        if (game != null){
            final int totalPlayers = game.getPlayers().size() + joinManager.getExpectedPlayers().size();
            final int maxPlayers = game.getMaxPlayers();

            if (!game.getState().isAccessible()) {
                return HyriJoinResponse.DENY_STATE;
            } else if (totalPlayers >= maxPlayers) {
                return HyriJoinResponse.DENY_FULL;
            } else if (totalPlayers + partySize > maxPlayers) {
                return HyriJoinResponse.DENY_SPACE;
            }
        } else {
            final int totalPlayers = server.getPlayers().size() + joinManager.getExpectedPlayers().size();
            final int slots = server.getSlots();

            if (slots != -1) {
                if (totalPlayers >= slots) {
                    return HyriJoinResponse.DENY_FULL;
                } else if (totalPlayers + partySize > slots) {
                    return HyriJoinResponse.DENY_SPACE;
                }
            }
        }
        return HyriJoinResponse.ALLOW;
    }

    @Override
    public String createResponseMessage(UUID player, HyriJoinResponse response) {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player);

        if (account == null) {
            return null;
        }

        String reason = "";
        if (response == HyriJoinResponse.DENY_FULL) {
            reason = RESPONSE.apply(account, "full");
        } else if (response == HyriJoinResponse.DENY_SPACE) {
            reason = RESPONSE.apply(account, "space");
        } else if (response == HyriJoinResponse.DENY_STATE) {
            reason = RESPONSE.apply(account, "state");;
        }

        return ChatColor.RED + HyriLanguageMessage.get(RESPONSE_KEY).getForPlayer(account)
                .replace("%server%", HyriAPI.get().getServer().getName())
                .replace("%reason%", reason == null ? "" : "(" + reason + ")");
    }

    @Override
    public void onPlayerJoin(UUID playerId) {
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();
        final Player player = Bukkit.getPlayer(playerId);

        if (game != null) {
            game.handleLogin(player);
            return;
        }

        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);

        if (account.isInVanishMode()) {
            PlayerUtil.hidePlayer(player, true);
        }
    }

    @Override
    public void onLogout(UUID playerId) {
        final Player player = Bukkit.getPlayer(playerId);
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            final HyriGamePlayer gamePlayer = game.getPlayer(playerId);

            if (gamePlayer != null) {
                game.handleLogout(gamePlayer.getPlayer());
            }
        }

        Title.sendTitle(player, "", "", 0, 0, 0);
    }

}