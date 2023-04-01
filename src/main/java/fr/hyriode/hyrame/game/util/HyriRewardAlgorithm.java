package fr.hyriode.hyrame.game.util;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.leveling.IHyriLeveling;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/04/2022 at 12:54
 */
public class HyriRewardAlgorithm {

    public static long getPlayedTimeHyris(long playedTime) {
        return (int) ((playedTime / 1000) / 20);
    }

    public static long getHyris(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST) {
            return 0L;
        }

        return kills * 10L + getPlayedTimeHyris(playedTime) + (victory ? 50 : 0);
    }

    public static double getPlayedTimeXP(long playedTime) {
        return (double) (playedTime / 1000) / 5;
    }

    public static double getXP(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST) {
            return 0L;
        }

        return kills * 10.0D + getPlayedTimeXP(playedTime) + (victory ? 100 : 0);
    }

}
