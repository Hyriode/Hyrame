package fr.hyriode.hyrame.impl.host.category;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.game.scoreboard.HyriWaitingScoreboard;
import fr.hyriode.hyrame.host.option.BooleanOption;
import fr.hyriode.hyrame.host.option.StringOption;
import fr.hyriode.hyrame.impl.host.gui.HostMainGUI;
import fr.hyriode.hyrame.impl.host.option.MapOption;
import fr.hyriode.hyrame.impl.host.option.SlotsOption;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.HyrameHead;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by AstFaster
 * on 01/08/2022 at 00:50
 */
public class HostMainCategory extends HostDefaultCategory {

    public static final String PLAYERS_KEY = "max-players";
    public static final String NAME = "main";

    public HostMainCategory() {
        super(NAME, Material.NETHER_STAR);
        this.guiProvider = player -> new HostMainGUI(player, this);

        // Host name
        this.addOption(20, new StringOption(this.createOptionDisplay("host-name", Material.NAME_TAG), this.getHostData().getName(), 28).onChanged(name -> {
            this.getHostData().setName(name);

            HyriWaitingScoreboard.updateAll();

            HyriAPI.get().getHyggdrasilManager().sendData();
        }).notSavable().notResettable());

        // Map selector
        this.addOption(29, new MapOption(this.createOptionDisplay("map", Material.MAP)).notResettable());

        // Spectators enabled/disabled
        this.addOption(22, new BooleanOption(this.createOptionDisplay("spectators-allowed", Material.EYE_OF_ENDER), true).onChanged(spectatorsAllowed -> {
            this.getHostData().setSpectatorsAllowed(spectatorsAllowed);

            HyriAPI.get().getHyggdrasilManager().sendData();
        }));

        // Players
        this.addOption(23, new SlotsOption(this.createOptionDisplay(PLAYERS_KEY, ItemBuilder.asHead(HyrameHead.SIMPLISTIC_STEVE).build())));

        // Whitelist enabled/disabled
        this.addOption(31, new BooleanOption(this.createOptionDisplay("whitelist", Material.BOOK_AND_QUILL), true).onChanged(whitelisted -> {
            this.getHostData().setWhitelisted(whitelisted);

            HyriAPI.get().getHyggdrasilManager().sendData();
        }).notSavable().notResettable());
    }

    @Override
    public ItemStack createItem(Player player) {
        return new ItemBuilder(super.createItem(player))
                .withLore(new ArrayList<>(Arrays.asList(this.description.getValue(player).split("\n"))))
                .build();
    }
}
