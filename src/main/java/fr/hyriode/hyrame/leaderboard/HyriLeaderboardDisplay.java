package fr.hyriode.hyrame.leaderboard;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.leaderboard.HyriLeaderboardScope;
import fr.hyriode.api.leaderboard.HyriLeaderboardScore;
import fr.hyriode.api.leaderboard.IHyriLeaderboard;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fr.hyriode.hyrame.hologram.Hologram.Line;

/**
 * Created by AstFaster
 * on 12/06/2022 at 13:14
 */
public class HyriLeaderboardDisplay {

    private static final Set<HyriLeaderboardDisplay> DISPLAYS = new HashSet<>();

    private static final String LINE_FORMAT = ChatColor.GRAY + "%position%. %prefix%" + ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "%score%";

    private BukkitTask updateTask;

    private final Map<UUID, Hologram> holograms;

    private boolean showing;

    protected Map<UUID, HyriLeaderboardScope> currentScopes;
    protected IHyriLeaderboard leaderboard;

    protected final JavaPlugin plugin;
    protected final String leaderboardType;
    protected final String leaderboardName;
    protected final Function<Player, String> header;
    protected final BiFunction<Player, Double, String> scoreFormatter;
    /** The time to wait between each update of the leaderboard */
    protected final long updateTime;
    protected final Location location;

    protected final List<HyriLeaderboardScope> scopes;

    public HyriLeaderboardDisplay(JavaPlugin plugin, String leaderboardType, String leaderboardName, Function<Player, String> header, BiFunction<Player, Double, String> scoreFormatter, long updateTime, Location location, List<HyriLeaderboardScope> scopes) {
        this.plugin = plugin;
        this.leaderboardType = leaderboardType;
        this.leaderboardName = leaderboardName;
        this.header = header;
        this.scoreFormatter = scoreFormatter;
        this.updateTime = updateTime;
        this.location = location;
        this.scopes = scopes;
        this.holograms = new HashMap<>();
        this.currentScopes = new HashMap<>();
        this.leaderboard = HyriAPI.get().getLeaderboardProvider().getLeaderboard(leaderboardType, leaderboardName);
    }

    public void show() {
        if (this.showing) {
            throw new IllegalStateException("Display is already showing!");
        }

        this.showing = true;

        if (this.updateTime != -1) {
            this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::update, this.updateTime, this.updateTime);
        }

        DISPLAYS.add(this);
    }

    public void handleLogin(Player player) {
        final UUID playerId = player.getUniqueId();

        final List<ScopeWrapper> wrappers = this.scopes.stream().map(ScopeWrapper::get).sorted(Comparator.comparingInt(scopeWrapper -> scopeWrapper != null ? scopeWrapper.getIndex() : 0)).collect(Collectors.toList());

        this.currentScopes.put(playerId, wrappers.get(0).getInitial());

        final Hologram hologram = this.createHologram(player);

        if (hologram == null) {
            return;
        }

        hologram.addReceiver(player);

        this.holograms.put(playerId, hologram);
    }

    public void handleLogout(Player player) {
        final UUID playerId = player.getUniqueId();
        final Hologram hologram = this.holograms.remove(playerId);

        this.currentScopes.remove(playerId);

        if (hologram == null) {
            return;
        }

        hologram.removeReceiver(player);
        hologram.destroy();
    }

    private Hologram createHologram(Player player) {
        final Supplier<HyriLeaderboardScope> scoreSupplier = () -> this.currentScopes.get(player.getUniqueId());

        final Map<Integer, Line> lines = new HashMap<>();

        if (this.header != null) {
            lines.put(0, new Line(this.header));
        }

        final Supplier<List<HyriLeaderboardScore>> scoresSupplier = () -> this.leaderboard.getScores(scoreSupplier.get(), 0, 9);

        for (int i = 1; i <= 10; i++) {
            final int position = i;
            final Line line = new Line(target -> {
                final List<HyriLeaderboardScore> scores = scoresSupplier.get();
                final HyriLeaderboardScore score = scores.size() > position - 1 ? scores.get(position - 1) : null;

                return score == null ? this.createPositionLine(position, "************", this.scoreFormatter.apply(player, 0.0D)) : this.createPositionLine(position, HyriAPI.get().getPlayerManager().getPrefix(score.getId()), this.scoreFormatter.apply(player, score.getValue()));
            });

            lines.put(i, line);
        }

        lines.put(11, new Line(target -> {
            final HyriLeaderboardScope scope = scoreSupplier.get();
            final UUID targetId = target.getUniqueId();
            final long position = this.leaderboard.getPosition(scope, targetId);

            return ChatColor.DARK_GRAY + "▶ " + this.createPositionLine(position == -1 ? position : position + 1, HyriAPI.get().getPlayerManager().getPrefix(targetId), this.scoreFormatter.apply(target, this.leaderboard.getScore(scope, targetId))) + ChatColor.DARK_GRAY + " ◀";
        }));

        if (this.scopes.size() != 1) {
            final List<ScopeWrapper> wrappers = this.scopes.stream().map(ScopeWrapper::get).sorted(Comparator.comparingInt(scopeWrapper -> scopeWrapper != null ? scopeWrapper.getIndex() : 0)).collect(Collectors.toList());
            final Pagination<ScopeWrapper> pagination = new Pagination<>(3, wrappers);

            int line = 13;
            for (int i = 0; i < pagination.totalPages(); i++) {
                final int page = i;

                lines.put(line + i, new Line(target -> {
                    final StringBuilder builder = new StringBuilder();

                    for (ScopeWrapper wrapper : pagination.getPageContent(page)) {
                        final HyriLeaderboardScope scope = wrapper.getInitial();

                        builder.append(this.currentScopes.get(target.getUniqueId()) == scope ? ChatColor.AQUA + "" + ChatColor.BOLD : ChatColor.GRAY)
                                .append(wrapper.getDisplay().getForPlayer(target))
                                .append(ChatColor.RESET)
                                .append(wrapper.isLast() ? "" : " ");
                    }
                    return builder.toString();
                }));
            }
        }

        return new Hologram.Builder(this.plugin, this.location)
                .withLines(lines)
                .withInteractionCallback(target -> {
                    if (this.scopes.size() == 1) {
                        return;
                    }

                    final UUID playerId = target.getUniqueId();
                    final HyriLeaderboardScope currentScope = this.currentScopes.remove(playerId);
                    final ScopeWrapper wrapper = ScopeWrapper.get(currentScope);

                    if (wrapper == null) {
                        return;
                    }

                    ScopeWrapper nextScope = wrapper.next();
                    while (!this.scopes.contains(nextScope.getInitial())) {
                        nextScope = nextScope.next();
                    }

                    this.currentScopes.put(target.getUniqueId(), nextScope.getInitial());

                    target.playSound(player.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                    this.update(target);
                })
                .build();
    }

    private String createPositionLine(long position, String prefix, String score) {
        return LINE_FORMAT
                .replace("%position%", position <= 0 ? "?" : String.valueOf(position))
                .replace("%prefix%", prefix)
                .replace("%score%", score);
    }

    public void hide() {
        if (!this.showing) {
            throw new IllegalStateException("Display is not showing!");
        }

        this.showing = false;
        this.updateTask.cancel();
        this.holograms.values().forEach(Hologram::destroy);
        this.holograms.clear();

        DISPLAYS.remove(this);
    }

    public void update() {
        for (Hologram hologram : this.holograms.values()) {
            hologram.updateLines();
        }
    }

    public void update(Player player) {
        final Hologram hologram = this.holograms.get(player.getUniqueId());

        if (hologram == null) {
            return;
        }

        hologram.updateLines();
    }

    public static Set<HyriLeaderboardDisplay> getAll() {
        return DISPLAYS;
    }

    private enum ScopeWrapper {

        TOTAL(HyriLeaderboardScope.TOTAL, 0, "total"),
        MONTHLY(HyriLeaderboardScope.MONTHLY, 1, "monthly"),
        WEEKLY(HyriLeaderboardScope.WEEKLY, 2, "weekly"),
        DAILY(HyriLeaderboardScope.DAILY, 3, "daily");

        private final HyriLeaderboardScope initial;
        private final int index;
        private final Supplier<HyriLanguageMessage> display;

        ScopeWrapper(HyriLeaderboardScope initial, int index, String languageKey) {
            this.initial = initial;
            this.index = index;
            this.display = () -> IHyriLanguageManager.Provider.get().getMessage("leaderboard.scope." + languageKey);
        }

        public HyriLeaderboardScope getInitial() {
            return this.initial;
        }

        public int getIndex() {
            return this.index;
        }

        public HyriLanguageMessage getDisplay() {
            return this.display.get();
        }

        public boolean isLast() {
            return get(this.index + 1) == null;
        }

        public ScopeWrapper next() {
            final ScopeWrapper scope = get(this.index + 1);

            if (scope == null) {
                return ScopeWrapper.TOTAL;
            }
            return scope;
        }

        public static ScopeWrapper get(int index) {
            for (ScopeWrapper scope : values()) {
                if (scope.getIndex() == index) {
                    return scope;
                }
            }
            return null;
        }

        public static ScopeWrapper get(HyriLeaderboardScope scope) {
            for (ScopeWrapper wrapper : values()) {
                if (wrapper.getInitial() == scope) {
                    return wrapper;
                }
            }
            return null;
        }

    }

    public static class Builder {

        private final JavaPlugin plugin;

        private String leaderboardType;
        private String leaderboardName;
        private List<HyriLeaderboardScope> scopes = Collections.singletonList(HyriLeaderboardScope.TOTAL);

        private Location location;

        private Function<Player, String> header;
        private BiFunction<Player, Double, String> scoreFormatter = (target, score) -> String.valueOf(score).split("\\.")[0];

        private long updateTime = -1;

        public Builder(JavaPlugin plugin, String leaderboardType, String leaderboardName, Location location) {
            this.plugin = plugin;
            this.leaderboardType = leaderboardType;
            this.leaderboardName = leaderboardName;
            this.location = location;
        }

        public String getLeaderboardType() {
            return this.leaderboardType;
        }

        public Builder withLeaderboardType(String leaderboardType) {
            this.leaderboardType = leaderboardType;
            return this;
        }

        public String getLeaderboardName() {
            return this.leaderboardName;
        }

        public Builder withLeaderboardName(String leaderboardName) {
            this.leaderboardName = leaderboardName;
            return this;
        }

        public List<HyriLeaderboardScope> getScopes() {
            return this.scopes;
        }

        public Builder withScopes(List<HyriLeaderboardScope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder withScopes(HyriLeaderboardScope... scopes) {
            this.scopes = Arrays.asList(scopes);
            return this;
        }

        public Location getLocation() {
            return this.location;
        }

        public Builder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Function<Player, String> getHeader() {
            return this.header;
        }

        public Builder withHeader(Function<Player, String> header) {
            this.header = header;
            return this;
        }

        public BiFunction<Player, Double, String> getScoreFormatter() {
            return this.scoreFormatter;
        }

        public Builder withScoreFormatter(BiFunction<Player, Double, String> scoreFormatter) {
            this.scoreFormatter = scoreFormatter;
            return this;
        }

        public long getUpdateTime() {
            return this.updateTime;
        }

        public Builder withUpdateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public HyriLeaderboardDisplay build() {
            return new HyriLeaderboardDisplay(this.plugin, this.leaderboardType, this.leaderboardName, this.header, this.scoreFormatter, this.updateTime, this.location, this.scopes);
        }

    }

}
