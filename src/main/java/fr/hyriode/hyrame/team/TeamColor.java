package fr.hyriode.hyrame.team;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum TeamColor {

    WHITE(Color.fromRGB(255, 255, 255), 0),
    ORANGE(Color.fromRGB(216, 127, 51), 1),
    MAGENTA(Color.fromRGB(178, 76, 216), 2),
    LBLUE(Color.fromRGB(102, 153, 216), 3),
    YELLOW(Color.fromRGB(229, 227, 51), 4),
    LIME(Color.fromRGB(127, 204, 25), 5),
    PINK(Color.fromRGB(242, 127, 165), 6),
    GRAY(Color.fromRGB(76, 76, 76), 7),
    LGRAY(Color.fromRGB(153, 153, 153), 8),
    CYAN(Color.fromRGB(76, 125, 153), 9),
    PURPLE(Color.fromRGB(127, 63, 178), 10),
    BLUE(Color.fromRGB(51, 76, 190), 11),
    BROWN(Color.fromRGB(102, 76, 51), 12),
    GREEN(Color.fromRGB(102, 127, 216), 13);

    private final Color color;
    private final int ID;

    TeamColor(Color color, int ID) {
        this.color = color;
        this.ID = ID;
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
        itemStack.getItemMeta().setDisplayName(name);
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
}
