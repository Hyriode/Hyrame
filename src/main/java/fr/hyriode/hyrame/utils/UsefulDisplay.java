package fr.hyriode.hyrame.utils;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.host.HostDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 26/08/2022 at 09:12
 */
public class UsefulDisplay {

    public static HostDisplay optionDisplay(String name, ItemStack icon) {
        return new HostDisplay.Builder().withName(name)
                .withDisplayName(HyriLanguageMessage.get("host.option." + name + ".name"))
                .withDescription(HyriLanguageMessage.get("host.option." + name + ".description"))
                .withIcon(icon)
                .build();
    }

    public static HostDisplay optionDisplay(String name, Material icon) {
        return optionDisplay(name, new ItemStack(icon));
    }

    public static HostDisplay optionDisplay(String name, String langName, ItemStack itemStack) {
        return new HostDisplay.Builder()
                .withName(name)
                .withIcon(itemStack)
                .withDisplayName(HyriLanguageMessage.get("host.option." + langName + ".name"))
                .withDescription(HyriLanguageMessage.get("host.option." + langName + ".description"))
                .build();
    }

    public static HostDisplay optionDisplay(String name, String langName, Material material) {
        return optionDisplay(name, langName, new ItemStack(material));
    }

    public static HostDisplay categoryDisplay(String name, ItemStack icon) {
        return new HostDisplay.Builder().withName(name)
                .withDisplayName(HyriLanguageMessage.get("host.category." + name + ".name"))
                .withDescription(HyriLanguageMessage.get("host.category." + name + ".description"))
                .withIcon(icon)
                .build();
    }

    public static HostDisplay categoryDisplay(String name, Material icon) {
        return categoryDisplay(name, new ItemStack(icon));
    }

}
