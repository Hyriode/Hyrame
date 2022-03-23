package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.tab.Tab;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HyriDefaultTab extends Tab {

    private final Hyrame hyrame;
    private final HyriLanguage language;

    public HyriDefaultTab(Hyrame hyrame, HyriLanguage language) {
        this.hyrame = hyrame;
        this.language = language;
    }

    @Override
    public void send(Player player) {
        this.addLines();
        super.send(player);
    }

    private void addLines() {
        this.addHeaderLines();
        this.addFooterLines();
    }

    private void addHeaderLines() {
        this.setBlankHeaderLine(0);
        this.setHeaderLine(1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + HyriConstants.SERVER_NAME + ChatColor.AQUA + ChatColor.ITALIC + ".fr");
        this.setBlankHeaderLine(2);
    }

    private void addFooterLines() {
        final IHyriLanguageManager languageManager = this.hyrame.getLanguageManager();
        final String websiteAndForum = languageManager.getValue(language, "tab.website.and.forum");
        final String store = languageManager.getValue(language, "tab.store");
        final String discord = languageManager.getValue(language, "tab.discord");

        this.setBlankFooterLine(0);
        this.setFooterLine(1, this.createFooterLine(websiteAndForum, HyriConstants.WEBSITE_URL));
        this.setFooterLine(2, this.createFooterLine(store, HyriConstants.STORE_WEBSITE_URL));
        this.setFooterLine(3, this.createFooterLine(discord, HyriConstants.DISCORD_URL));
        this.setBlankFooterLine(4);
    }

    private String createFooterLine(String content, String value) {
        return " " + ChatColor.GRAY + Symbols.QUOTE_MARK_LEFT +  content + ChatColor.DARK_AQUA + value + ChatColor.GRAY + " " + Symbols.QUOTE_MARK_RIGHT + " ";
    }

}
