package fr.hyriode.hyrame.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.enchantment.HyriEnchant;
import fr.hyriode.hyrame.reflection.Reflection;
import fr.hyriode.hyrame.utils.player.ProfileLoader;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material, int amount, int data) {
        this(new ItemStack(material, amount, (byte) data));
    }

    public ItemBuilder(Potion potion, int amount) {
        this(potion.toItemStack(amount));
    }

    public ItemBuilder(Potion potion) {
        this(potion, 1);
    }

    public static ItemBuilder asHead() {
        return new ItemBuilder(Material.SKULL_ITEM, 1, 3);
    }

    public static ItemBuilder asHead(ItemHead head) {
        return new ItemBuilder(Material.SKULL_ITEM, 1, 3).withHeadTexture(head);
    }

    public ItemBuilder withName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public String getName() {
        return this.itemMeta.getDisplayName();
    }

    public ItemBuilder withName(Player player, String key) {
        this.itemMeta.setDisplayName(HyriLanguageMessage.get(key).getValue(player));
        return this;
    }

    public ItemBuilder withLore(List<String> inputLore) {
        final List<String> lore = new ArrayList<>();

        for (String line : inputLore) {
            lore.addAll(Arrays.asList(line.split("\n")));
        }

        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder withLore(String... inputLore) {
        return this.withLore(Arrays.asList(inputLore));
    }

    public List<String> getLore() {
        return this.itemMeta.getLore();
    }

    public ItemBuilder appendLore(String... lines) {
        final List<String> lore = this.itemMeta.getLore();

        lore.addAll(Arrays.asList(lines));

        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder removeLoreLines(int number) {
        final List<String> lore = this.itemMeta.getLore();

        for (int i = 0; i < number; i++) {
            lore.remove(lore.size() - 1);
        }

        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder withData(short data) {
        this.itemStack.setDurability(data);
        return this;
    }

    public ItemBuilder withPlayerHead(String name) {
        final Player player = Bukkit.getPlayer(name);

        GameProfile profile;
        if (player != null) {
            profile = ((CraftPlayer) player).getProfile();
        } else {
            profile = new ProfileLoader(name).loadProfile();
        }

        final List<Property> properties = new ArrayList<>(profile.getProperties().get("textures"));

        return this.withHeadTexture(properties.size() > 0 ? properties.get(0).getValue() : null);
    }

    public ItemBuilder withPlayerHead(UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);

        GameProfile profile;
        if (player != null) {
            profile = ((CraftPlayer) player).getProfile();
        } else {
            profile = new ProfileLoader(uuid).loadProfile();
        }

        final List<Property> properties = new ArrayList<>(profile.getProperties().get("textures"));

        return this.withHeadTexture(properties.size() > 0 ? properties.get(0).getValue() : null);
    }

    public ItemBuilder withHeadTexture(String texture) {
        try {
            final SkullMeta skullMeta = (SkullMeta) this.itemMeta;
            final GameProfile profile = new GameProfile(UUID.randomUUID(), "");

            if (texture != null) {
                profile.getProperties().put("textures", new Property("textures", texture));
            }

            final Field profileField = skullMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

            this.itemStack.setItemMeta(skullMeta);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ItemBuilder withHeadTexture(ItemHead head) {
        return this.withHeadTexture(head.getTexture());
    }

    public ItemBuilder withItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder withAllItemFlags() {
        return this.withItemFlags(ItemFlag.values());
    }

    public ItemBuilder withGlow() {
        return this.withEnchant(HyriEnchant.GLOW, 1, true);
    }

    public ItemBuilder withEnchant(Enchantment enchant, int level, boolean show) {
        if (enchant == null) {
            return this;
        }

        this.itemMeta.addEnchant(enchant, level, show);
        return this;
    }

    public ItemBuilder withEnchant(Enchantment enchant, int level) {
        return this.withEnchant(enchant, level, true);
    }

    public ItemBuilder withEnchant(Enchantment enchant) {
        return this.withEnchant(enchant, 1, true);
    }

    public ItemBuilder incrementEnchant(Enchantment enchantment, boolean show) {
        final int level = this.itemMeta.getEnchantLevel(enchantment);

        this.itemMeta.addEnchant(enchantment, level + 1, show);
        return this;
    }

    public ItemBuilder incrementEnchant(Enchantment enchantment) {
        return this.incrementEnchant(enchantment, true);
    }

    public ItemBuilder decrementEnchant(Enchantment enchantment, boolean show) {
        final int level = this.itemMeta.getEnchantLevel(enchantment) - 1;

        if (level <= 0) {
            this.itemMeta.removeEnchant(enchantment);
        } else {
            this.itemMeta.addEnchant(enchantment, level, show);
        }
        return this;
    }

    public ItemBuilder decrementEnchant(Enchantment enchantment) {
        return this.decrementEnchant(enchantment, true);
    }

    public ItemBuilder withHidingEnchantments() {
        return this.withItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder unbreakable() {
        this.itemMeta.spigot().setUnbreakable(true);
        return this;
    }

    public ItemBuilder withLeatherArmorColor(Color color) {
        final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemMeta;

        leatherArmorMeta.setColor(color);

        this.itemStack.setItemMeta(leatherArmorMeta);
        return this;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(this.itemStack);
    }

    public ItemNBT nbt() {
        this.itemStack.setItemMeta(this.itemMeta);

        return new ItemNBT(this.itemStack);
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}