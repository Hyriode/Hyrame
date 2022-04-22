package fr.hyriode.hyrame.impl.module.nickname;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.util.Skin;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.language.HyriCommonMessages;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.signgui.SignGUI;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/04/2022 at 08:03
 */
public class NicknameGUI extends HyriInventory {

    private static final String DICE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg4MWNjMjc0N2JhNzJjYmNiMDZjM2NjMzMxNzQyY2Q5ZGUyNzFhNWJiZmZkMGVjYjE0ZjFjNmE4YjY5YmM5ZSJ9fX0=";
    private static final Function<Player, ItemStack> DICE = player -> ItemBuilder.asHead()
            .withName(HyriLanguageMessage.get("gui.nickname.random").getForPlayer(player))
            .withHeadTexture(DICE_TEXTURE)
            .build();

    private final String edit;

    private String currentNickname;
    private String currentSkin;
    private HyriPlayerRankType currentRank;

    private boolean randomSkin;
    private boolean canValidate = true;

    private int rankIndex;

    private final NicknameModule nicknameModule;

    public NicknameGUI(Player owner, NicknameModule nicknameModule, String currentNickname, String currentSkin, HyriPlayerRankType currentRank) {
        super(owner, name(owner, "gui.nickname.title"), 6 * 9);
        this.nicknameModule = nicknameModule;
        this.currentNickname = currentNickname;
        this.currentSkin = currentSkin;
        this.currentRank = currentRank;
        this.randomSkin = this.currentSkin == null;
        this.rankIndex = NicknameModule.AVAILABLE_RANKS.indexOf(this.currentRank);
        this.edit = HyriLanguageMessage.get("gui.nickname.edit").getForPlayer(this.owner);

        final ItemStack fill = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName("").build();

        this.setHorizontalLine(0, 8, fill);
        this.setHorizontalLine(45, 53, fill);
        this.setVerticalLine(9, 36, fill);
        this.setVerticalLine(17, 45, fill);

        this.setItem(49, new ItemBuilder(Material.STAINED_GLASS, 1, 5).withName(this.owner, "gui.nickname.apply").build(), event -> {
            if (this.canValidate) {
                this.nicknameModule.processNickname(this.owner, this.currentNickname, this.currentSkin, this.randomSkin ? this.nicknameModule.getRandomSkin() : this.nicknameModule.getSkinFromPlayer(this.currentSkin), this.currentRank);
            }
        });

        this.addItems();
    }

    public NicknameGUI(Player owner, NicknameModule nicknameModule, String currentSkin, HyriPlayerRankType currentRank) {
        this(owner, nicknameModule, "Loading random nickname...", currentSkin, currentRank);
        this.canValidate = false;

        ThreadUtil.ASYNC_EXECUTOR.execute(() -> {
            this.currentNickname = this.nicknameModule.getRandomNickname(true);

            this.addNameItems();

            this.canValidate = true;
        });
    }

    private void addItems() {
        this.addNameItems();
        this.addSkinItems();
        this.addRankItems();
    }

    private void addNameItems() {
        final ItemStack paper = new ItemBuilder(Material.PAPER)
                .withName(ChatColor.AQUA + "Nickname")
                .withLore(HyriLanguageMessage.get("gui.nickname.current").getForPlayer(this.owner) + ChatColor.WHITE + this.currentNickname, "", this.edit)
                .build();
        this.setItem(20, paper, event -> this.openNameGUI());

        this.setItem(29, DICE.apply(this.owner), event -> {
            this.currentNickname = this.nicknameModule.getRandomNickname(true);
            this.addNameItems();
        });
    }

    private void openNameGUI() {
        new SignGUI((player, lines) -> {
            final String first = lines[0];

            if (first.length() > 3 && !first.contains(" ")) {
                final NicknameModule.Result result = this.nicknameModule.checkNickname(this.owner.getUniqueId(), first);

                if (result == NicknameModule.Result.FINE) {
                    this.currentNickname = first;

                    this.addNameItems();
                } else {
                    this.owner.sendMessage(result.getMessage(this.owner));
                }
            } else {
                player.sendMessage(ChatColor.RED + HyriCommonMessages.INVALID_INPUT.getForPlayer(this.owner));
            }

            this.open();
        }).withLines("", "^^^^^^^^", "Nick", "").open(this.owner);
    }

    private void addSkinItems() {
        final ItemStack paper = new ItemBuilder(Material.PAPER)
                .withName(ChatColor.AQUA + "Skin")
                .withLore(HyriLanguageMessage.get("gui.nickname.current").getForPlayer(this.owner) + ChatColor.WHITE + (this.currentSkin != null ? this.currentSkin : ChatColor.RED + Symbols.CROSS_STYLIZED_BOLD), "", this.edit)
                .build();
        this.setItem(22, paper, event -> this.openSkinGUI());

        this.setItem(31, DICE.apply(this.owner), event -> {
            this.randomSkin = true;

            this.currentSkin = null;

            this.addSkinItems();
        });
    }

    private void openSkinGUI() {
        new SignGUI((player, lines) -> {
            final String first = lines[0];

            if (first.length() > 0 && !first.contains(" ")) {
                this.currentSkin = first;
                this.randomSkin = false;

                this.addSkinItems();
            } else {
                player.sendMessage(ChatColor.RED + HyriCommonMessages.INVALID_INPUT.getForPlayer(this.owner));
            }

            this.open();
        }).withLines("", "^^^^^^^^", "Skin", "").open(this.owner);
    }

    private void addRankItems() {
        final List<String> lore = new ArrayList<>();

        for (HyriPlayerRankType rankType : NicknameModule.AVAILABLE_RANKS) {
            if (rankType == this.currentRank) {
                final String prefix = rankType == HyriPlayerRankType.PLAYER ? ChatColor.GRAY + "Default" : rankType.getDefaultPrefix();

                lore.add(ChatColor.WHITE + "▶ " + prefix);
                continue;
            }

            lore.add(rankType == HyriPlayerRankType.PLAYER ? ChatColor.GRAY + "Default" : rankType.getDefaultPrefix());
        }

        lore.add("");
        lore.add(this.edit);

        final ItemStack paper = new ItemBuilder(Material.PAPER)
                .withName(ChatColor.AQUA + "Rank")
                .withLore(lore)
                .build();
        this.setItem(24, paper, event -> {
            if (this.rankIndex == NicknameModule.AVAILABLE_RANKS.size() - 1) {
                this.rankIndex = 0;
            } else {
                this.rankIndex++;
            }

            this.currentRank = NicknameModule.AVAILABLE_RANKS.get(this.rankIndex);

            this.addRankItems();
        });

        this.setItem(33, DICE.apply(this.owner), event -> {
            HyriPlayerRankType rank = this.generateRandomRank();

            while (rank == this.currentRank) {
                rank = this.generateRandomRank();
            }

            this.currentRank = rank;

            this.rankIndex = NicknameModule.AVAILABLE_RANKS.indexOf(this.currentRank);

            this.addRankItems();
        });
    }

    private HyriPlayerRankType generateRandomRank() {
        return NicknameModule.AVAILABLE_RANKS.get(ThreadLocalRandom.current().nextInt(NicknameModule.AVAILABLE_RANKS.size()));
    }

}
