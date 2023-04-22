package fr.hyriode.hyrame.impl.join;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.server.join.HyriJoinResponse;
import fr.hyriode.api.server.join.IHyriJoinHandler;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGamePlayer;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.player.HyriGameReconnectEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 20/04/2022 at 19:53
 */
public class JoinHandler implements IHyriJoinHandler {

    private static final String RESPONSE_KEY = "message.join.deny";
    private static final BiFunction<IHyriPlayer, String, String> RESPONSE = (player, key) -> HyriLanguageMessage.get(RESPONSE_KEY + "." + key).getValue(player);

    private final Set<UUID> expectedPlayers = new HashSet<>();

    private final IHyrame hyrame;

    public JoinHandler(IHyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public HyriJoinResponse requestJoin(UUID player, HyriJoinResponse response) {
        final IHyriServer server = HyriAPI.get().getServer();
        final HyggServer.State state = server.getState();

        if (state != HyggServer.State.READY && state != HyggServer.State.PLAYING) {
            return HyriJoinResponse.DENY_STATE;
        }

        final IHyriPlayerSession session = IHyriPlayerSession.get(player);
        final HyriGame<?> game = this.hyrame.getGame();
        final int slots = server.getSlots();

        if (session != null && session.isModerating()) { // Moderators bypass checks
            return HyriJoinResponse.ALLOW;
        } else if (game != null) { // Game processing
            final HyriGamePlayer gamePlayer = game.getPlayer(player);

            if (gamePlayer != null && game.isReconnectionAllowed()) { // Check for reconnection
                final HyriGameReconnectEvent event = new HyriGameReconnectEvent(game, gamePlayer);

                HyriAPI.get().getEventBus().publish(event);

                if (event.isAllowed()) {
                    this.expectedPlayers.add(player);

                    return HyriJoinResponse.ALLOW;
                }
                return HyriJoinResponse.DENY_OTHER;
            } else if (game.getState().isAccessible() && game.getPlayers().size() + this.expectedPlayers.size() >= slots) { // Check if server is full
                return HyriJoinResponse.DENY_FULL;
            } else if (game.getState() == HyriGameState.PLAYING && !game.isSpectatorsAllowed()) { // Check if spectators are allowed
                return HyriJoinResponse.DENY_OTHER;
            } else if (game.getState() == HyriGameState.PLAYING && server.getPlayersPlaying().size() + game.getSpectators().size() >= 75) { // Check for spectators slots
                return HyriJoinResponse.DENY_FULL;
            }
        } else if (server.getPlayersPlaying().size() + this.expectedPlayers.size() >= slots) { // Check slots for normal servers (not game)
            return HyriJoinResponse.DENY_FULL;
        }

        this.expectedPlayers.add(player);

        return HyriJoinResponse.ALLOW;
    }

    @Override
    public String createResponseMessage(UUID player, HyriJoinResponse response) {
        final IHyriPlayer account = IHyriPlayer.get(player);

        if (account == null) {
            return null;
        }

        String reason = null;
        if (response == HyriJoinResponse.DENY_FULL) {
            reason = RESPONSE.apply(account, "full");
        } else if (response == HyriJoinResponse.DENY_STATE) {
            reason = RESPONSE.apply(account, "state");;
        }

        return ChatColor.RED + HyriLanguageMessage.get(RESPONSE_KEY).getValue(account)
                .replace("%server%", HyriAPI.get().getServer().getName())
                .replace("%reason%", reason == null ? "" : "(" + reason + ")");
    }

    @Override
    public void onJoin(UUID playerId) {
        final HyriGame<?> game = this.hyrame.getGame();
        final Player player = Bukkit.getPlayer(playerId);
        final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);

        this.expectedPlayers.remove(playerId);

        if (game != null && !session.isModerating()) {
            final HyriGameState state = game.getState();

            if (state.isAccessible() || game.getPlayer(player) != null) {
                System.out.println("Handle login");
                game.handleLogin(player);
            } else if (state == HyriGameState.PLAYING) {
                System.out.println("Handle spectator login");
                game.handleSpectatorLogin(player);
            }
            return;
        }

        final IHyriPlayer account = IHyriPlayer.get(playerId);

        if (session.isVanished()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                final IHyriPlayer targetAccount = IHyriPlayer.get(target.getUniqueId());

                if (!targetAccount.getRank().isStaff()) {
                    target.hidePlayer(player);
                }
            }
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            final IHyriPlayerSession targetSession = IHyriPlayerSession.get(target.getUniqueId());

            if (targetSession == null) {
                continue;
            }

            if (targetSession.isVanished() && !account.getRank().isStaff()) {
                player.hidePlayer(target);
            }
        }
    }

    @Override
    public void onLogout(UUID playerId) {
        final HyriGame<?> game = this.hyrame.getGameManager().getCurrentGame();

        if (game != null) {
            if (game.getPlayer(playerId) != null) {
                game.handleLogout(Bukkit.getPlayer(playerId));
            } else if (game.getSpectator(playerId) != null) {
                game.handleSpectatorLogout(Bukkit.getPlayer(playerId));
            }
        }
    }

    @Override
    public boolean isExpected(UUID playerId) {
        final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);

        return this.expectedPlayers.contains(playerId) || (session != null && session.isModerating());
    }

}
