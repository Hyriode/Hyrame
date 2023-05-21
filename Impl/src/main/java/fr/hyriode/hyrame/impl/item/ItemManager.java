package fr.hyriode.hyrame.impl.item;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.IItemManager;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.item.event.HyriItemGiveEvent;
import fr.hyriode.hyrame.plugin.IPluginProvider;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 14/11/2021 at 17:54
 */
public class ItemManager implements IItemManager {

    public static final String ITEM_NBT_KEY = "HyriItem";

    private final Map<String, HyriItem<?>> items = new HashMap<>();

    private final Hyrame hyrame;

    public ItemManager(Hyrame hyrame) {
        this.hyrame = hyrame;
    }

    @Override
    public void registerItems() {
        for (IPluginProvider pluginProvider : this.hyrame.getPluginProviders()) {
            this.registerItems(pluginProvider);
        }
    }

    @Override
    public void registerItems(IPluginProvider pluginProvider) {
        for (String packageName : pluginProvider.getItemsPackages()) {
            this.registerItems(pluginProvider, packageName);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerItems(IPluginProvider pluginProvider, String packageName) {
        final Set<Class<?>> classes = this.hyrame.getScanner().scan(pluginProvider.getClass().getClassLoader(), packageName, HyriItem.class);

        HyrameLogger.providerLog(pluginProvider, "Searching for items in '" + packageName + "' package");

        if (classes == null) {
            return;
        }

        for (Class<?> clazz : classes) {
            if (Reflection.hasConstructorWithParameters(clazz, pluginProvider.getPlugin().getClass())) {
                this.registerItem(pluginProvider, (Class<? extends HyriItem<?>>) clazz);
            }
        }
    }

    @Override
    public void registerItem(IPluginProvider pluginProvider, Class<? extends HyriItem<?>> itemClass) {
        try {
            final HyriItem<?> item = itemClass.getConstructor(pluginProvider.getPlugin().getClass()).newInstance(pluginProvider.getPlugin());
            final String itemId = pluginProvider.getId() + ":" + item.getName();

            this.items.put(itemId, item);

            HyrameLogger.providerLog(pluginProvider, "Registered '" + itemId + "' item");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void giveItem(Player player, int slot, Class<? extends HyriItem<?>> itemClass) {
        final HyriItem<?> item = this.getItem(itemClass);

        if (item != null) {
            this.giveItem(player, slot, item.getName());
        }
    }

    @Override
    public void giveItem(Player player, int slot, String name) {
        if (this.isItemExisting(name)) {
            final HyriItem<?> item = this.items.get(this.getFullItemId(name));
            final HyriItemGiveEvent event = new HyriItemGiveEvent(player.getUniqueId(), item);

            HyriAPI.get().getEventBus().publish(event);

            if (event.isCancelled()) {
                return;
            }

            final ItemStack itemStack = item.onPreGive(this.hyrame, player, slot, this.toItemStack(player, item));

            player.getInventory().setItem(slot, itemStack);

            item.onGive(this.hyrame, player, slot, itemStack);
        }
    }

    @Override
    public boolean giveItem(Player player, Class<? extends HyriItem<?>> itemClass) {
        final HyriItem<?> item = this.getItem(itemClass);

        if (item != null) {
            return this.giveItem(player, item.getName());
        }
        return false;
    }

    @Override
    public boolean giveItem(Player player, String name) {
        if (this.isItemExisting(name)) {
            final HyriItem<?> item = this.items.get(this.getFullItemId(name));
            final HyriItemGiveEvent event = new HyriItemGiveEvent(player.getUniqueId(), item);

            HyriAPI.get().getEventBus().publish(event);

            if (event.isCancelled()) {
                return false;
            }

            final ItemStack itemStack = item.onPreGive(this.hyrame, player, -1, this.toItemStack(player, item));

            return ItemUtil.addItemInPlayerInventory(itemStack, player);
        }
        return false;
    }

    @Override
    public <T extends HyriItem<?>> T getItem(Class<T> itemClass) {
        for (HyriItem<?> item : this.items.values()) {
            if (item.getClass() == itemClass) {
                return itemClass.cast(item);
            }
        }
        return null;
    }

    @Override
    public HyriItem<?> getItem(String name) {
        return this.items.get(this.getFullItemId(name));
    }

    private ItemStack toItemStack(Player player, HyriItem<?> item) {
        final HyriLanguageMessage description = item.getDescription() == null ? null : item.getDescription().get();

        return new ItemBuilder(item.getItem().clone())
                .withName(item.getDisplay().get().getValue(player))
                .withLore(description == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(description.getValue(player).split("\n"))))
                .nbt().setString(ITEM_NBT_KEY, item.getName())
                .build();
    }

    private boolean isItemExisting(String name) {
        for (HyriItem<?> item : this.items.values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private String getFullItemId(String name) {
        for (HyriItem<?> item : this.items.values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return this.items.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), item)).map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
            } else {
                for (String itemId : this.items.keySet()) {
                    if (itemId.equalsIgnoreCase(name)) {
                        return itemId;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, HyriItem<?>> getItems() {
        return this.items;
    }

}
