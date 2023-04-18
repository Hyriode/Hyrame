package fr.hyriode.hyrame.game.util;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.leveling.IHyriLeveling;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.game.HyriGamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 24/04/2022 at 12:54
 */
public class HyriRewardAlgorithm {

    public static Hyris customHyris(HyriGamePlayer gamePlayer) {
        return new Hyris(gamePlayer);
    }

    public static class Hyris {

        private final HyriGamePlayer gamePlayer;

        private final List<BiFunction<HyriGamePlayer, Long, Long>> modifiers = new ArrayList<>();

        public Hyris(HyriGamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
        }

        public Hyris withModifier(BiFunction<HyriGamePlayer, Long, Long> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public List<BiFunction<HyriGamePlayer, Long, Long>> getModifiers() {
            return this.modifiers;
        }

        public long get() {
            long amount = 0;

            for (BiFunction<HyriGamePlayer, Long, Long> modifier : this.modifiers) {
                amount = modifier.apply(this.gamePlayer, amount);
            }
            return amount;
        }

    }

    public static Experience customXP(HyriGamePlayer gamePlayer) {
        return new Experience(gamePlayer);
    }

    public static class Experience {

        private final HyriGamePlayer gamePlayer;

        private final List<BiFunction<HyriGamePlayer, Double, Double>> modifiers = new ArrayList<>();

        public Experience(HyriGamePlayer gamePlayer) {
            this.gamePlayer = gamePlayer;
        }

        public Experience withModifier(BiFunction<HyriGamePlayer, Double, Double> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public List<BiFunction<HyriGamePlayer, Double, Double>> getModifiers() {
            return this.modifiers;
        }

        public double get() {
            double amount = 0;

            for (BiFunction<HyriGamePlayer, Double, Double> modifier : this.modifiers) {
                amount = modifier.apply(this.gamePlayer, amount);
            }
            return amount;
        }

    }

    public static long getPlayedTimeHyris(long playedTime) {
        return (int) ((playedTime / 1000) / 10);
    }

    public static long getHyris(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST) {
            return 0L;
        }

        return kills * 7L + getPlayedTimeHyris(playedTime) + (victory ? 10 : 0);
    }

    public static double getPlayedTimeXP(long playedTime) {
        return (double) (playedTime / 1000) / 10;
    }

    public static double getXP(int kills, long playedTime, boolean victory) {
        if (HyriAPI.get().getServer().getAccessibility() == HyggServer.Accessibility.HOST) {
            return 0L;
        }

        return (int) Math.ceil(kills * 5.0 + getPlayedTimeXP(playedTime) + (victory ? 15 : 0));
    }

}
