package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.utils.References;
import fr.hyriode.hyrame.impl.utils.Symbols;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.hyrame.tab.Tab;
import fr.hyriode.hyriapi.settings.HyriLanguage;
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
        this.setHeaderLine(1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + References.SERVER_NAME + ChatColor.AQUA + ChatColor.ITALIC + ".fr");
        this.setBlankHeaderLine(2);
    }

    private void addFooterLines() {
        final IHyriLanguageManager languageManager = this.hyrame.getLanguageManager();
        final String websiteAndForum = languageManager.getValue(language, "tab.website.and.forum");
        final String store = languageManager.getValue(language, "tab.store");
        final String discord = languageManager.getValue(language, "tab.discord");

        this.setBlankFooterLine(0);
        this.setFooterLine(1, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK +  websiteAndForum + ChatColor.DARK_AQUA + References.WEBSITE_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setFooterLine(2, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK + store + ChatColor.DARK_AQUA + References.STORE_WEBSITE_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setFooterLine(3, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK + discord + ChatColor.DARK_AQUA + References.DISCORD_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setBlankFooterLine(4);
    }

}
