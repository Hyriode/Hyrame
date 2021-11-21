package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.impl.util.References;
import fr.hyriode.hyrame.impl.util.Symbols;
import fr.hyriode.hyrame.language.IHyriLanguageManager;
import fr.hyriode.tools.tab.Tab;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HyriDefaultTab extends Tab {

    private final Hyrame hyrame;
    private final Player player;

    public HyriDefaultTab(Hyrame hyrame, Player player) {
        this.hyrame = hyrame;
        this.player = player;

        this.addLines();
        this.send(this.player);
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
        final String websiteAndForum = languageManager.getMessageValueForPlayer(player, "tab.website.and.forum");
        final String store = languageManager.getMessageValueForPlayer(player, "tab.store");
        final String discord = languageManager.getMessageValueForPlayer(player, "tab.discord");

        this.setBlankFooterLine(0);
        this.setFooterLine(1, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK +  websiteAndForum + ChatColor.DARK_AQUA + References.WEBSITE_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setFooterLine(2, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK + store + ChatColor.DARK_AQUA + References.STORE_WEBSITE_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setFooterLine(3, " " + ChatColor.GRAY + Symbols.LEFT_QUOTE_MARK + discord + ChatColor.DARK_AQUA + References.DISCORD_URL + ChatColor.GRAY + " " + Symbols.RIGHT_QUOTE_MARK + " ");
        this.setBlankFooterLine(4);
    }

}
