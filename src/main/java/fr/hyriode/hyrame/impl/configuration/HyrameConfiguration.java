package fr.hyriode.hyrame.impl.configuration;

import fr.hyriode.hyrame.configuration.HyriConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 13/11/2021 at 15:38
 */
public class HyrameConfiguration extends HyriConfiguration {

    private boolean tabListRanks;
    private boolean defaultTabLines;
    private boolean defaultChatFormat;

    public HyrameConfiguration(JavaPlugin plugin) {
        super(plugin);
        this.tabListRanks = (boolean) this.set("tabListRanks", true);
        this.defaultTabLines = (boolean) this.set("defaultTabLines", true);
        this.defaultChatFormat = (boolean) this.set("defaultChatFormat", true);
    }

    public boolean isTabListRanks() {
        return this.tabListRanks;
    }

    public void setTabListRanks(boolean tabListRanks) {
        this.tabListRanks = tabListRanks;
    }

    public boolean isDefaultTabLines() {
        return this.defaultTabLines;
    }

    public void setDefaultTabLines(boolean defaultTabLines) {
        this.defaultTabLines = defaultTabLines;
    }

    public boolean isDefaultChatFormat() {
        return this.defaultChatFormat;
    }

    public void setDefaultChatFormat(boolean defaultChatFormat) {
        this.defaultChatFormat = defaultChatFormat;
    }

}
