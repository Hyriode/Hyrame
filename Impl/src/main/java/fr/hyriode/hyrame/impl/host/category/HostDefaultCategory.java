package fr.hyriode.hyrame.impl.host.category;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.host.HostCategory;
import fr.hyriode.hyrame.host.HostDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 01/08/2022 at 00:49
 */
public abstract class HostDefaultCategory extends HostCategory {

    public HostDefaultCategory(String name, Material material) {
        super(new HostDisplay.Builder()
                .withName(name)
                .withIcon(material)
                .withDisplayName(HyriLanguageMessage.get("host.category." + name + ".name"))
                .withDescription(HyriLanguageMessage.get("host.category." + name + ".description"))
                .build());
    }

    protected HostDisplay createOptionDisplay(String name, Material material) {
        return this.createOptionDisplay(name, new ItemStack(material));
    }

    protected HostDisplay createOptionDisplay(String name, ItemStack itemStack) {
        return new HostDisplay.Builder()
                .withName(name)
                .withIcon(itemStack)
                .withDisplayName(HyriLanguageMessage.get("host.option." + name + ".name"))
                .withDescription(HyriLanguageMessage.get("host.option." + name + ".description"))
                .build();
    }

    protected HostDisplay createOptionDisplay(String name, String langName, ItemStack itemStack) {
        return new HostDisplay.Builder()
                .withName(name)
                .withIcon(itemStack)
                .withDisplayName(HyriLanguageMessage.get("host.option." + langName + ".name"))
                .withDescription(HyriLanguageMessage.get("host.option." + langName + ".description"))
                .build();
    }

    protected HostDisplay createOptionDisplay(String name, String langName, Material material) {
        return this.createOptionDisplay(name, langName, new ItemStack(material));
    }

}
