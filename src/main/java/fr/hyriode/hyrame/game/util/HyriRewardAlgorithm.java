package fr.hyriode.hyrame.game.util;

import fr.hyriode.api.HyriAPI;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/04/2022 at 12:54
 */
public class HyriRewardAlgorithm {

    public static long getHyris(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().isHost()) {
            return 0L;
        }

        final int playedTimeCoins = (int) ((playedTime / 1000) / 30) * 10;

        return kills * 5L + playedTimeCoins + (victory ? 50 : 0);
    }

    public static long getXP(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().isHost()) {
            return 0L;
        }

        final int playedTimeXP = (int) ((playedTime / 1000) / 30) * 20;

        return kills * 5L + playedTimeXP + (victory ? 50 : 0);
    }

}
