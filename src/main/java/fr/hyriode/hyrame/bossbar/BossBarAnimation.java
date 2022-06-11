package fr.hyriode.hyrame.bossbar;

import java.util.function.Consumer;

/**
 * Created by AstFaster
 * on 07/06/2022 at 19:19
 */
public abstract class BossBarAnimation {

    /**
     * Start the animation of the boss bar
     *
     * @param bossBar The {@linkplain BossBar boss bar} to animate
     */
    public abstract void start(BossBar bossBar);

    /**
     * Disable the animation of the boss bar
     */
    public abstract void disable();

    /**
     * Method called at each tick if the animation is running
     */
    public abstract void onTick();

    /**
     * A step animation
     */
    public static class Step extends BossBarAnimation {

        protected int currentTime;
        protected int currentStep;

        protected BossBar bossBar;

        protected final Type type;
        protected final Options options;

        public Step(Type type, Options options) {
            this.type = type;
            this.options = options;
        }

        @Override
        public void start(BossBar bossBar) {
            this.bossBar = bossBar;
            this.bossBar.setProgress(this.type == Type.INCREASING ? 0.0F : 1.0F);
        }

        @Override
        public void disable() {}

        @Override
        public void onTick() {
            if (this.currentTime == this.options.getTime()) {
                this.currentStep++;

                final float currentProgress = this.bossBar.getProgress();
                final float stepProgress = 1F / this.options.getSteps();
                final float progress = this.type == Type.INCREASING ? currentProgress + stepProgress : currentProgress - stepProgress;

                this.options.onStep().accept(this.bossBar);

                if (progress * this.bossBar.getHandle().getMaxHealth() > 1) {
                    this.bossBar.setProgress(progress);
                } else {
                    this.bossBar.setProgress(0.0F);
                }

                if (this.currentStep == this.options.getSteps()) {
                    this.options.onComplete().accept(this.bossBar);
                    this.bossBar.disableAnimation();
                }

                this.currentTime = 0;
            }

            this.currentTime++;
        }

        public static class Options {

            /** Time (in ticks) between each steps */
            private final long time;
            private final int steps;
            private final Consumer<BossBar> onStep;
            private final Consumer<BossBar> onComplete;

            public Options(long time, int steps, Consumer<BossBar> onStep, Consumer<BossBar> onComplete) {
                this.time = time;
                this.steps = steps;
                this.onStep = onStep;
                this.onComplete = onComplete;
            }

            public long getTime() {
                return this.time;
            }

            public int getSteps() {
                return this.steps;
            }

            public Consumer<BossBar> onStep() {
                return this.onStep;
            }

            public Consumer<BossBar> onComplete() {
                return this.onComplete;
            }

        }

        public enum Type {
            INCREASING,
            DECREASING
        }

    }

    /**
     * A basic evolution animation. The boss bar's progress will go from 0 to 1 or 1 to 0 fluidly
     */
    public static class Evolution extends BossBarAnimation {

        private long currentTime;

        protected BossBar bossBar;

        protected final Type type;
        protected final Options options;

        public Evolution(Type type, Options options) {
            this.type = type;
            this.options = options;
        }

        @Override
        public void start(BossBar bossBar) {
            this.bossBar = bossBar;
            this.currentTime = 0;
            this.bossBar.setProgress(this.type == Type.INCREASING ? 0.0F : 1.0F);
        }

        @Override
        public void disable() {}

        @Override
        public void onTick() {
            if (this.currentTime == this.options.getTime()) {
                this.options.whenComplete().accept(this.bossBar);
                this.bossBar.disableAnimation();
                return;
            }

            final float progress = this.type == Type.INCREASING ? this.bossBar.getProgress() + 1F / this.options.getTime() : this.bossBar.getProgress() - 1F / this.options.getTime();

            if (progress * this.bossBar.getHandle().getMaxHealth() > 1) {
                this.bossBar.setProgress(progress);
            }

            this.currentTime++;
        }

        public static class Options {

            /** Time (in ticks) to complete the progress bar */
            private final long time;
            private final Consumer<BossBar> whenComplete;

            public Options(long time, Consumer<BossBar> whenComplete) {
                this.time = time;
                this.whenComplete = whenComplete;
            }

            public long getTime() {
                return this.time;
            }

            public Consumer<BossBar> whenComplete() {
                return this.whenComplete;
            }

        }

        public enum Type {
            INCREASING,
            DECREASING
        }

    }

}
