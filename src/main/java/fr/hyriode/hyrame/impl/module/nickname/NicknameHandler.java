package fr.hyriode.hyrame.impl.module.nickname;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.nickname.HyriNicknameUpdatedEvent;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.hyrame.impl.tab.HyriTabHandler;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 21:20
 */
public class NicknameHandler implements Listener {

    private final HyriTabHandler tabHandler;
    private final NicknameModule nicknameModule;

    public NicknameHandler(HyriTabHandler tabHandler, NicknameModule nicknameModule) {
        this.tabHandler = tabHandler;
        this.nicknameModule = nicknameModule;

        HyriAPI.get().getEventBus().register(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final IHyriNickname nickname = account.getNickname();

        if (nickname != null) {
            ThreadUtil.ASYNC_EXECUTOR.execute(() -> this.nicknameModule.applyNickname(player, nickname.getName(), nickname.getSkin()));
        } else {
            PlayerUtil.setName(player, account.getName());
        }
    }

    @HyriEventHandler
    public void onNicknameUpdated(HyriNicknameUpdatedEvent event) {
        final IHyriPlayer account = event.getPlayer();
        final Player player = Bukkit.getPlayer(account.getUniqueId());

        this.tabHandler.onLogout(player);

        ThreadUtil.EXECUTOR.schedule(() -> this.tabHandler.onLogin(player), 50, TimeUnit.MILLISECONDS);
    }

}
