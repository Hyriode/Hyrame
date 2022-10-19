package fr.hyriode.hyrame.host.option;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.hyrame.HyrameLoader;
import fr.hyriode.hyrame.armorstand.ArmorStandInteraction;
import fr.hyriode.hyrame.enchantment.EnchantmentWrapper;
import fr.hyriode.hyrame.host.HostDisplay;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedInventory;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.event.HyriItemGiveEvent;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.HyrameHead;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.list.ListReplacer;
import fr.hyriode.hyrame.utils.player.SavablePlayerInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by AstFaster
 * on 24/08/2022 at 15:55
 */
public class InventoryOption extends HostOption<SavablePlayerInventory> {

    private final List<UUID> currentPlayers;

    public InventoryOption(HostDisplay display, SavablePlayerInventory defaultValue) {
        super(display, defaultValue);
        this.currentPlayers = new ArrayList<>();

        Bukkit.getServer().getPluginManager().registerEvents(new Handler(), HyrameLoader.getHyrame().getPlugin());
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        this.currentPlayers.add(player.getUniqueId());

        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack[] contents = playerInventory.getContents();
        final ItemStack[] amor = playerInventory.getArmorContents();

        player.closeInventory();
        player.setGameMode(GameMode.CREATIVE);

        this.value.setTo(playerInventory);

        final ArmorStandInteraction interaction = new ArmorStandInteraction(player);
        final Runnable onQuit = () -> {
            final PlayerInventory inventory = player.getInventory();

            inventory.setContents(contents);
            inventory.setArmorContents(amor);

            this.currentPlayers.remove(player.getUniqueId());

            this.categoryGUIProvider.apply(player).open();

            interaction.remove();
        };


        interaction.addData(new ArmorStandInteraction.Data()
                .withItem(ItemBuilder.asHead(HyrameHead.LIME_GREEN).build())
                .withText(HyrameMessage.HOST_OPTION_INVENTORY_SAVE.asLang())
                .withAngle(40.0F)
                .withInteraction(p -> {
                    final SavablePlayerInventory inventory = new SavablePlayerInventory(p.getInventory());

                    this.setValue(inventory);

                    onQuit.run();
                }));

        interaction.addData(new ArmorStandInteraction.Data()
                .withItem(ItemBuilder.asHead(HyrameHead.ENCHANTED_BOOKS).build())
                .withText(HyrameMessage.HOST_OPTION_INVENTORY_ENCHANT.asLang())
                .withAngle(0.0F)
                .withInteraction(p -> {
                    final ItemStack itemStack = p.getItemInHand();

                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        return;
                    }

                    new EnchantmentsGUI(p, itemStack).open();
                }));

        interaction.addData(new ArmorStandInteraction.Data()
                .withItem(ItemBuilder.asHead(HyrameHead.RED).build())
                .withText(HyrameMessage.HOST_OPTION_INVENTORY_CANCEL.asLang())
                .withAngle(-40.0F)
                .withInteraction(p -> onQuit.run()));

        interaction.create();

    }

    public class Handler implements Listener {

        public Handler() {
            HyriAPI.get().getEventBus().register(this);
        }

        @HyriEventHandler
        public void onGive(HyriItemGiveEvent event) {
            if (!currentPlayers.contains(event.getPlayer().getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onClick(InventoryClickEvent event) {
            if (!currentPlayers.contains(event.getWhoClicked().getUniqueId()) || event.getClickedInventory().getHolder() instanceof HyriInventory) {
                return;
            }

            event.setCancelled(false);
        }

    }

    private static class EnchantmentsGUI extends PaginatedInventory {

        private final ItemStack itemStack;

        public EnchantmentsGUI(Player owner, ItemStack itemStack) {
            super(owner, name(owner, "gui.host.inventory.enchantments.name"), 5 * 9);
            this.itemStack = itemStack;
            this.paginationManager.setArea(new PaginationArea(9, 35));

            this.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
            this.setHorizontalLine(36, 44, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());

            this.addItemStack();
            this.addUnbreakableItem();
            this.addEnchantments();
        }

        private void addItemStack() {
            this.setItem(4, this.itemStack);
        }

        private void addUnbreakableItem() {
            final boolean unbreakable = this.itemStack.hasItemMeta() && this.itemStack.getItemMeta().spigot().isUnbreakable();
            final List<String> lore = ListReplacer.replace(HyrameMessage.HOST_OPTION_INVENTORY_ENCHANT_UNBREAKABLE_LORE.asList(this.owner), "%enabled_color%", String.valueOf(unbreakable ? ChatColor.AQUA : ChatColor.GRAY))
                    .replace("%disabled_color%", String.valueOf(unbreakable ? ChatColor.GRAY : ChatColor.AQUA))
                    .list();

            this.setItem(7, new ItemBuilder(Material.ANVIL, 1, 2)
                    .withName(HyrameMessage.HOST_OPTION_INVENTORY_ENCHANT_UNBREAKABLE_NAME.asString(this.owner))
                    .withLore(lore)
                    .build(),
                    event -> {
                        final ItemMeta meta = this.itemStack.getItemMeta();

                        meta.spigot().setUnbreakable(!unbreakable);

                        this.itemStack.setItemMeta(meta);
                        this.owner.playSound(this.owner.getLocation(), Sound.CLICK, 0.5F, 2.0F);

                        this.addItemStack();
                        this.addUnbreakableItem();
                    });
        }

        private void addEnchantments() {
            final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

            pagination.clear();

            for (EnchantmentWrapper enchantment : EnchantmentWrapper.values()) {
                final Supplier<ItemStack> itemStack = () -> {
                    final int enchantLevel = this.itemStack.hasItemMeta() ? this.itemStack.getItemMeta().getEnchantLevel(enchantment.asBukkit()) : 0;

                    return new ItemBuilder(enchantLevel != 0 ? Material.ENCHANTED_BOOK : Material.BOOK)
                            .withName(ChatColor.AQUA + enchantment.getDisplay().getValue(this.owner))
                            .withLore(ListReplacer.replace(HyrameMessage.HOST_OPTION_INVENTORY_ENCHANT_LORE.asList(this.owner), "%level%", String.valueOf(enchantLevel)).list())
                            .build();
                };

                pagination.add(PaginatedItem.from(itemStack.get(), event -> {
                    final ItemBuilder builder = new ItemBuilder(this.itemStack);

                    if (event.isLeftClick()) {
                        builder.incrementEnchant(enchantment.asBukkit());
                    } else if (event.isRightClick()) {
                        builder.decrementEnchant(enchantment.asBukkit());
                    }

                    builder.build();

                    event.setCurrentItem(itemStack.get());

                    this.addItemStack();
                }));
            }

            this.paginationManager.updateGUI();
        }

        @Override
        public void updatePagination(int page, List<PaginatedItem> items) {}

    }

}
