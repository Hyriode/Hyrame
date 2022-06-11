package fr.hyriode.hyrame.generator;

import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.generator.event.HyriGeneratorCreatedEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorDropEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorRemovedEvent;
import fr.hyriode.hyrame.generator.event.HyriGeneratorUpgradedEvent;
import fr.hyriode.hyrame.hologram.Hologram;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Function;

import static fr.hyriode.hyrame.hologram.Hologram.Builder;
import static fr.hyriode.hyrame.hologram.Hologram.Line;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/01/2022 at 15:57
 */
class HyriGeneratorDefaultHeader extends HyriGenerator.Header implements Listener {

    private static final HyriLanguageMessage SECONDS = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "Drop in %seconds% seconds")
            .addValue(HyriLanguage.FR, "Génération dans %seconds% secondes");

    private static final int TIER_LINE = 1;

    private long timer;
    private BukkitTask timerTask;

    private IHyriGeneratorTier tier;

    private final JavaPlugin plugin;
    private final Location location;
    private final Function<Player, String> generatorName;

    public HyriGeneratorDefaultHeader(JavaPlugin plugin, Location location, ItemStack itemStack, Function<Player, String> generatorName, IHyriGeneratorTier tier) {
        this.plugin = plugin;
        this.location = location;
        this.generatorName = generatorName;
        this.tier = tier;

        this.withItem(itemStack);
        this.withHologram(this.createHologram());

        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
    
    private Hologram createHologram() {
        final Function<Player, String> timerLine = target -> {
            final String sparkles = ChatColor.DARK_AQUA + Symbols.SPARKLES;
            final String result = sparkles + " " + SECONDS.getForPlayer(target).replace("%seconds%", ChatColor.WHITE + "" + this.timer + ChatColor.DARK_AQUA);

            return this.timer == 1 ? result.substring(0, result.length() - 1) + ChatColor.DARK_AQUA + " " + sparkles : result + ChatColor.DARK_AQUA + " " + sparkles;
        };

        return new Builder(this.plugin, this.location)
                .withLine(0, new Line(this.generatorName))
                .withLine(TIER_LINE, new Line(target -> ChatColor.DARK_AQUA + "Tier " + Symbols.LINE_VERTICAL_BOLD + ChatColor.WHITE + " " + this.tier.getName().apply(target)))
                .withLine(2, new Line(timerLine).withUpdate(20, line -> line.withValue(timerLine))).build();
    }

    @EventHandler
    public void onGeneratorCreated(HyriGeneratorCreatedEvent event) {
        final HyriGenerator generator = event.getGenerator();

        if (this.isSameHeader(generator)) {
            this.timerTask = new BukkitRunnable() {
                @Override
                public void run() {
                    timer--;
                }
            }.runTaskTimerAsynchronously(this.plugin, 0L, 20L);

            this.timer = generator.getFirstSpawnTime() == 0 ? generator.getTier().getTimeBetweenSpawns() / 20 + 1 : generator.getFirstSpawnTime() / 20 + 1;
        }
    }

    @EventHandler
    public void onGeneratorDrop(HyriGeneratorDropEvent event) {
        final HyriGenerator generator = event.getGenerator();

        if (this.isSameHeader(generator)) {
            this.timer = generator.getTier().getTimeBetweenSpawns() / 20 + 1;
        }
    }

    @EventHandler
    public void onGeneratorUpgraded(HyriGeneratorUpgradedEvent event) {
        final HyriGenerator generator = event.getGenerator();

        if (this.isSameHeader(generator)) {
            this.tier = event.getTier();

            this.hologram.updateLine(TIER_LINE);
        }
    }

    @EventHandler
    public void onGeneratorRemoved(HyriGeneratorRemovedEvent event) {
        final HyriGenerator generator = event.getGenerator();

        if (this.isSameHeader(generator)) {
            HandlerList.unregisterAll(this);

            this.timerTask.cancel();
        }
    }

    private boolean isSameHeader(HyriGenerator generator) {
        final HyriGenerator.Header header = generator.getHeader();

        if (header != null) {
            return header.getId() == this.id;
        }
        return false;
    }

}
