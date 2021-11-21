package fr.hyriode.tools.reflection.entity;

import fr.hyriode.tools.reflection.Reflection;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public enum EnumItemSlot {

    MAIN_HAND("mainhand", 0),
    BOOTS("feet", 1),
    LEGGINGS("legs", 2),
    CHESTPLATE("chest", 3),
    HELMET("head", 4);

    private final String name;
    private final int slot;

    EnumItemSlot(String name, int slot) {
        this.name = name;
        this.slot = slot;
    }

    public Object getEnum() {
        return Reflection.invokeStaticMethod(Reflection.getNMSClass("EnumItemSlot"), "a", name);
    }

    public int getSlot() {
        return slot;
    }
    
}
