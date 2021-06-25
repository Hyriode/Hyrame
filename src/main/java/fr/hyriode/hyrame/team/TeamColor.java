package fr.hyriode.hyrame.team;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum TeamColor {

    WHITE(Color.fromRGB(255, 255, 255), 0, ChatColor.WHITE),
    ORANGE(Color.fromRGB(216, 127, 51), 1, ChatColor.GOLD),
    MAGENTA(Color.fromRGB(178, 76, 216), 2, ChatColor.LIGHT_PURPLE),
    LBLUE(Color.fromRGB(102, 153, 216), 3, ChatColor.AQUA),
    YELLOW(Color.fromRGB(229, 227, 51), 4, ChatColor.YELLOW),
    LIME(Color.fromRGB(127, 204, 25), 5, ChatColor.GREEN),
    PINK(Color.fromRGB(242, 127, 165), 6, ChatColor.LIGHT_PURPLE),
    GRAY(Color.fromRGB(76, 76, 76), 7, ChatColor.DARK_GRAY),
    LGRAY(Color.fromRGB(153, 153, 153), 8, ChatColor.GRAY),
    CYAN(Color.fromRGB(76, 125, 153), 9, ChatColor.DARK_AQUA),
    PURPLE(Color.fromRGB(127, 63, 178), 10, ChatColor.DARK_PURPLE),
    BLUE(Color.fromRGB(51, 76, 190), 11, ChatColor.BLUE),
    BROWN(Color.fromRGB(102, 76, 51), 12, ChatColor.GOLD),
    GREEN(Color.fromRGB(102, 127, 216), 13, ChatColor.GREEN),
    RED(Color.RED, 14, ChatColor.RED),
    BLACK(Color.BLACK, 15, ChatColor.BLACK);

    private final Color color;
    private final int ID;
    private final ChatColor chatColor;

    TeamColor(Color color, int ID, ChatColor chatColor) {
        this.color = color;
        this.ID = ID;
        this.chatColor = chatColor;
    }

    public static TeamColor getValueByID(int i) {
        for (TeamColor teamColor : values()) {
            if(teamColor.ID == i) {
                return teamColor;
            }
        }
        throw new IllegalArgumentException(String.valueOf(i));
    }

    public Color getColor() {
        return color;
    }

    public int getID() {
        return ID;
    }

    public ItemStack getColoredWool(int number, String name) {
        ItemStack itemStack = new ItemStack(Material.WOOL, number, (byte)getID());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getColoredClay(int number, String name) {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, number, (byte)getID());
        itemStack.getItemMeta().setDisplayName(name);
        return itemStack;
    }

    public ItemStack getColoredGlass(int number, String name) {
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS, number, (byte)getID());
        itemStack.getItemMeta().setDisplayName(name);
        return itemStack;
    }

    public ItemStack getColoredGlassPane(int number, String name) {
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, number, (byte)getID());
        itemStack.getItemMeta().setDisplayName(name);
        return itemStack;
    }

    public ItemStack getColoredLeatherHelmet(int number, String name) {
        ItemStack is = new ItemStack(Material.LEATHER_HELMET, number);
        LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
        lam.setDisplayName(name);
        lam.setColor(getColor());
        return is;
    }

    public ItemStack getColoredLeatherChesplate(int number, String name) {
        ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE, number);
        LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
        lam.setDisplayName(name);
        lam.setColor(getColor());
        return is;
    }

    public ItemStack getColoredLeatherLeggings(int number, String name) {
        ItemStack is = new ItemStack(Material.LEATHER_LEGGINGS, number);
        LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
        lam.setDisplayName(name);
        lam.setColor(getColor());
        return is;
    }

    public ItemStack getColoredLeatherBoots(int number, String name) {
        ItemStack is = new ItemStack(Material.LEATHER_BOOTS, number);
        LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
        lam.setColor(getColor());
        lam.setDisplayName(name);
        return is;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
}
