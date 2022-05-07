package fr.hyriode.hyrame.impl.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.HyriConstants;
import fr.hyriode.api.settings.HyriLanguage;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
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

    private final Player player;

    public HyriDefaultTab(Player player) {
        this.player = player;

        this.update();
    }

    public void update() {
        this.addLines();
        this.send(this.player);
    }

    private void addLines() {
        this.addHeaderLines();
        this.addFooterLines();
    }

    private void addHeaderLines() {
        final int ping = HyriAPI.get().getPlayerManager().getPing(this.player.getUniqueId());
        final String server = HyriAPI.get().getServer().getName();
        final int players = HyriAPI.get().getNetworkManager().getNetwork().getPlayerCount().getPlayers();
        double tps = HyriAPI.get().getServer().getTPS();

        if (tps > 20) {
            tps = 20;
        }

        final String formattedTps = String.format("%.2f", tps).replace(",", ".");
        final String informationLine = HyriLanguageMessage.get("tab.information-line").getForPlayer(this.player)
                .replace("%tps%", formattedTps)
                .replace("%ping%", String.valueOf(ping))
                .replace("%server%", server)
                .replace("%players%", String.valueOf(players));

        this.setBlankHeaderLine(0);
        this.setHeaderLine(1, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + HyriConstants.SERVER_NAME + ChatColor.AQUA + ChatColor.ITALIC + ".fr");
        this.setBlankHeaderLine(2);
        this.setHeaderLine(3, informationLine);
        this.setBlankHeaderLine(4);
    }

    private void addFooterLines() {
        final IHyriLanguageManager languageManager = IHyriLanguageManager.Provider.get();
        final String websiteAndForum = languageManager.getValue(this.player, "tab.website.and.forum");
        final String store = languageManager.getValue(this.player, "tab.store");
        final String discord = languageManager.getValue(this.player, "tab.discord");

        this.setBlankFooterLine(0);
        this.setFooterLine(1, this.createFooterLine(websiteAndForum, HyriConstants.WEBSITE_URL));
        this.setFooterLine(2, this.createFooterLine(store, HyriConstants.STORE_WEBSITE_URL));
        this.setFooterLine(3, this.createFooterLine(discord, HyriConstants.DISCORD_URL));
        this.setBlankFooterLine(4);
    }

    private String createFooterLine(String content, String url) {
        return " " + ChatColor.GRAY + content + ChatColor.AQUA + url + " ";
    }

}