package fr.hyriode.hyrame.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:51
 */
public enum HyrameMessage {

    COMMAND_NOT_ENABLED("message.command-not-enabled"),
    COMMAND_INVALID("message.command-invalid"),
    PERMISSION_ERROR("message.permission-error"),
    PLAYER_NOT_FOUND_FULL("message.player-not-found-full"),
    PLAYER_NOT_FOUND_ONLINE("message.player-not-found-online"),
    PLAYER_NOT_FOUND_SERVER("message.player-not-found-server"),
    INVALID_ARGUMENT("message.invalid-argument"),
    INVALID_NUMBER("message.invalid-number"),

    SECOND("message.second"),
    SECONDS("message.seconds"),
    SECOND_SECONDS("message.second-seconds"),

    AVAILABLE_COMMANDS("command.available-commands.message"),

    GAME_SPECTATORS_CHAT("game.spectators.chat"),
    GAME_STARTING_CANCELLED("game.starting.cancelled"),
    GAME_JOIN("game.join"),
    GAME_LEFT("game.left"),
    GAME_DISCONNECTED("game.disconnected"),
    GAME_RECONNECTED("game.reconnected"),
    GAME_VICTORY("game.victory"),
    GAME_DEFEAT("game.defeat"),
    GAME_END_WINNER("game.end.winner"),
    GAME_END_OFFLINE_MESSAGE("game.end.offline-message"),
    GAME_END_REWARDS("game.end.rewards"),
    GAME_GLOBAL_MESSAGE("game.global-message"),
    GAME_TEAM_MESSAGE("game.team-message"),
    GAME_OFFLINE_WIN_MESSAGE("game.offline-win.message"),

    WAITING_ROOM_NPC_DISPLAY("waiting-room.npc.display"),

    TEAM_AUTOMATICALLY_ADDED_MESSAGE("team-chooser.message.automatically-added"),

    HOST_RESET_NAME("host.reset.name"),
    HOST_RESET_LORE("host.reset.lore"),
    HOST_MULTIPLE_MODIFIERS_LORE("host.multiple-modifiers.item.lore"),
    HOST_ADVERT_NAME("host.advert.name"),
    HOST_ADVERT_LORE("host.advert.lore"),
    HOST_ADVERT_TIMER_LORE("host.advert-timer.lore"),
    HOST_CONFIGURATIONS_NAME("host.configurations.name"),
    HOST_CONFIGURATIONS_LORE("host.configurations.lore"),
    HOST_START_GAME_NAME("host.start-game.name"),
    HOST_START_GAME_LORE("host.start-game.lore"),
    HOST_CANCEL_START_GAME_NAME("host.cancel-start-game.name"),
    HOST_CANCEL_START_GAME_LORE("host.cancel-start-game.lore"),

    HOST_CATEGORY_TEAM_DESCRIPTION("host.category.team.description"),

    HOST_OPTION_INVENTORY_SAVE("host.option.inventory.save"),
    HOST_OPTION_INVENTORY_CANCEL("host.option.inventory.cancel"),
    HOST_OPTION_INVENTORY_ENCHANT("host.option.inventory.enchant"),
    HOST_OPTION_INVENTORY_ENCHANT_LORE("host.option.inventory.enchant.lore"),
    HOST_OPTION_INVENTORY_ENCHANT_UNBREAKABLE_NAME("host.option.inventory.enchant.unbreakable.name"),
    HOST_OPTION_INVENTORY_ENCHANT_UNBREAKABLE_LORE("host.option.inventory.enchant.unbreakable.lore"),

    HOST_OPTION_BOOLEAN_FORMATTER("host.option.boolean.formatter"),
    HOST_OPTION_STRING_FORMATTER("host.option.string.formatter"),
    HOST_OPTION_NUMBER_FORMATTER("host.option.number.formatter"),
    HOST_OPTION_PLAYERS_FORMATTER("host.option.players.formatter"),
    HOST_OPTION_TEAM_NAME_FORMATTER("host.option.team-name.formatter"),
    HOST_OPTION_NAME_TAG_VISIBILITY_FORMATTER("host.option.name-tag-visibility.formatter"),
    HOST_OPTION_COLOR_FORMATTER("host.option.color.formatter"),
    HOST_OPTION_MAP_FORMATTER("host.option.map.formatter"),
    HOST_OPTION_BORDER_SPEED_FORMATTER("host.option.border-speed.formatter"),
    HOST_OPTION_BORDER_SPEED_VALUE_FORMATTER("host.option.border-speed.value-formatter"),

    HOST_MAP_ITEM_LORE("host.map-item.lore"),

    HOST_PREFIX("host.prefix"),
    HOST_ADVERTISEMENT_ERROR_MESSAGE("host.advertisement-error.message", HOST_PREFIX),
    HOST_GAME_ERROR_MESSAGE("host.game-error.message", HOST_PREFIX),
    HOST_HIMSELF_ERROR_MESSAGE("host.himself-error.message", HOST_PREFIX),
    HOST_KICK_MESSAGE("host.kick.message", HOST_PREFIX),
    HOST_BAN_MESSAGE("host.ban.message", HOST_PREFIX),
    HOST_ALREADY_BANNED_MESSAGE("host.already-banned.message", HOST_PREFIX),
    HOST_UNBAN_MESSAGE("host.unban.message", HOST_PREFIX),
    HOST_NOT_BANNED_MESSAGE("host.not-banned.message", HOST_PREFIX),
    HOST_BAN_LIST_MESSAGE("host.ban-list.message"),
    HOST_OP_MESSAGE("host.op.message", HOST_PREFIX),
    HOST_DEOP_MESSAGE("host.deop.message", HOST_PREFIX),
    HOST_NOT_OP_MESSAGE("host.not-op.message", HOST_PREFIX),
    HOST_ALREADY_OP_MESSAGE("host.already-op.message", HOST_PREFIX),
    HOST_OP_LIST_MESSAGE("host.op-list.message"),
    HOST_WHITELISTED_MESSAGE("host.whitelisted.message", HOST_PREFIX),
    HOST_UN_WHITELISTED_MESSAGE("host.un-whitelisted.message", HOST_PREFIX),
    HOST_NOT_WHITELISTED_MESSAGE("host.not-whitelisted.message", HOST_PREFIX),
    HOST_ALREADY_WHITELISTED_MESSAGE("host.already-whitelisted.message", HOST_PREFIX),
    HOST_WHITELIST_CLEARED_MESSAGE("host.whitelist-cleared.message", HOST_PREFIX),
    HOST_WHITELIST_LIST_MESSAGE("host.whitelist-list.message"),
    HOST_HEAL_MESSAGE("host.heal.message", HOST_PREFIX),
    HOST_NOT_HOST_MESSAGE("host.not-host.message", HOST_PREFIX),
    HOST_NOT_ENOUGH_PLAYERS_MESSAGE("host.not-enough-players.message", HOST_PREFIX),

    HOST_CONFIG_ITEM_LORE("host.config.item.lore"),
    HOST_CONFIG_OWN_CONFIGS_ITEM_NAME("host.config.own-configs.item.name"),
    HOST_CONFIG_OWN_CONFIGS_ITEM_LORE("host.config.own-configs.item.lore"),
    HOST_CONFIG_ALL_CONFIGS_ITEM_NAME("host.config.all-configs.item.name"),
    HOST_CONFIG_ALL_CONFIGS_ITEM_LORE("host.config.all-configs.item.lore"),
    HOST_CONFIG_FAVORITE_CONFIGS_ITEM_NAME("host.config.favorite-configs.item.name"),
    HOST_CONFIG_FAVORITE_CONFIGS_ITEM_LORE("host.config.favorite-configs.item.lore"),
    HOST_CONFIG_CREATE_ITEM_NAME("host.config.create-item.name"),
    HOST_CONFIG_CREATE_ITEM_LORE("host.config.create-item.lore"),
    HOST_CONFIG_RESET_ITEM_NAME("host.config.reset-item.name"),
    HOST_CONFIG_RESET_ITEM_LORE("host.config.reset-item.lore"),
    HOST_CONFIG_SEARCH_ITEM_NAME("host.config.search-item.name"),
    HOST_CONFIG_SEARCH_ITEM_LORE("host.config.search-item.lore"),
    HOST_CONFIG_EMPTY_ITEM_NAME("host.config.empty-item.name"),
    HOST_CONFIG_CREATION_SAVE_ITEM_NAME("host.config.creation.save-item.name"),
    HOST_CONFIG_CREATION_NAME_ITEM_NAME("host.config.creation.name-item.name"),
    HOST_CONFIG_CREATION_NAME_ITEM_LORE("host.config.creation.name-item.lore"),
    HOST_CONFIG_CREATION_ICON_ITEM_NAME("host.config.creation.icon-item.name"),
    HOST_CONFIG_CREATION_ICON_ITEM_LORE("host.config.creation.icon-item.lore"),
    HOST_CONFIG_CREATION_PRIVATE_ITEM_NAME("host.config.creation.private-item.name"),
    HOST_CONFIG_CREATION_PRIVATE_ITEM_LORE("host.config.creation.private-item.lore"),
    HOST_CONFIG_SELECT_ICON_ITEM_NAME("host.config.select-icon-item.name"),
    HOST_CONFIG_DELETE_CONFIG_ITEM_NAME("host.config.delete-config-item.name"),

    HOST_CONFIG_SAVED_MESSAGE("host.config.saved.message", HOST_PREFIX),
    HOST_CONFIG_DELETED_MESSAGE("host.config.deleted.message", HOST_PREFIX),
    HOST_CONFIG_LOADED_MESSAGE("host.config.loaded.message", HOST_PREFIX),
    HOST_CONFIG_RESET_MESSAGE("host.config.reset.message", HOST_PREFIX),
    HOST_CONFIG_FAVORITE_ADD_MESSAGE("host.config.favorite-add.message", HOST_PREFIX),
    HOST_CONFIG_FAVORITE_REMOVE_MESSAGE("host.config.favorite-remove.message", HOST_PREFIX),
    HOST_CONFIG_SEARCH_INVALID_ID_MESSAGE("host.config.search.invalid-id.message", HOST_PREFIX),

    HOST_CONFIG_SEARCH_INPUT("host.config.search.input"),

    HOST_CONFIG_ALREADY_LOADED_LINE("host.config.already-loaded.line"),
    HOST_CONFIG_INCOMPATIBLE_LINE("host.config.incompatible.line"),

    HOST_CLICK_TO_LOAD("host.click.to-load"),
    HOST_CLICK_TO_EDIT("host.click.to-edit"),
    HOST_CLICK_TO_ADD_TO_FAVORITES("host.click.to-add-to-favorites"),
    HOST_CLICK_TO_REMOVE_FROM_FAVORITES("host.click.to-remove-from-favorites"),
    HOST_CLICK_TO_INCREASE("host.click.to-increase"),
    HOST_CLICK_TO_DECREASE("host.click.to-decrease"),

    PAGINATION_PREVIOUS_PAGE_ITEM_NAME("pagination.previous-page.item.name"),
    PAGINATION_PREVIOUS_PAGE_ITEM_LORE("pagination.previous-page.item.lore"),
    PAGINATION_NEXT_PAGE_ITEM_NAME("pagination.next-page.item.name"),
    PAGINATION_NEXT_PAGE_ITEM_LORE("pagination.next-page.item.lore"),

    NAME_TAG_VISIBILITY_ALWAYS("name-tag-visibility.always"),
    NAME_TAG_VISIBILITY_HIDE_FOR_OTHER_TEAMS("name-tag-visibility.hide-for-other-teams"),
    NAME_TAG_VISIBILITY_HIDE_FOR_OWN_TEAM("name-tag-visibility.hide-for-own-team"),
    NAME_TAG_VISIBILITY_NEVER("name-tag-visibility.never"),

    GO_BACK("go-back.display"),

    CLICK_TO_EDIT("click.to-edit"),
    CLICK_TO_SEE("click.to-see"),

    COLOR_RED("color.red"),
    COLOR_ORANGE("color.orange"),
    COLOR_YELLOW("color.yellow"),
    COLOR_DARK_GREEN("color.dark-green"),
    COLOR_GREEN("color.green"),
    COLOR_CYAN("color.cyan"),
    COLOR_BLUE("color.blue"),
    COLOR_PINK("color.pink"),
    COLOR_PURPLE("color.purple"),
    COLOR_WHITE("color.white"),
    COLOR_GRAY("color.gray"),
    COLOR_BLACK("color.black"),

    COMMON_YES("common.yes"),
    COMMON_NO("common.no"),

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final HyrameMessage prefix;

    HyrameMessage(String key, HyrameMessage prefix) {
        this.key = key;
        this.prefix = prefix;
    }
    
    HyrameMessage(String key) {
        this(key, null);
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return (this.prefix != null ? this.prefix.asString(account) : "") + this.asLang().getValue(account);
    }

    public String asString(Player player) {
        return (this.prefix != null ? this.prefix.asString(player) : "") + this.asLang().getValue(player);
    }

    public void sendTo(Player player) {
        player.sendMessage(this.asString(player));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return new ArrayList<>(Arrays.asList(this.asString(player).split("\n")));
    }

}
