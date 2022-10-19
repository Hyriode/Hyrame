package fr.hyriode.hyrame.impl.module.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.booster.IHyriBoosterManager;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 19/10/2022 at 14:02
 */
public class BoosterModule {

    public BoosterModule() {
        HyriAPI.get().getNetworkManager().getEventBus().register(new BoosterListener(this));
    }

    public void onBoosterEnabled(IHyriBooster booster) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final String message = (booster.getType().equals(IHyriBoosterManager.GLOBAL_TYPE) ? HyriLanguageMessage.get("message.booster.global.new").getValue(player) : HyriLanguageMessage.get("message.booster.game.new").getValue(player).replace("%game%", HyriAPI.get().getGameManager().getGameInfo(booster.getType()).getDisplayName()))
                    .replace("%player%", IHyriPlayer.get(booster.getOwner()).getNameWithRank())
                    .replace("%multiplier%", String.valueOf((int) booster.getMultiplier() * 100));

            player.sendMessage(message);
        }
    }

}
