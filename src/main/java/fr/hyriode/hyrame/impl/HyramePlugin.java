package fr.hyriode.hyrame.impl;

import fr.hyriode.hyrame.HyrameLoader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:54
 */
public class HyramePlugin extends JavaPlugin {

    private Hyrame hyrame;

    @Override
    public void onEnable() {
        this.hyrame = HyrameLoader.register(() -> new Hyrame(this));

        HyrameLoader.load(new HyrameProvider(this));

        final World world = Bukkit.getWorld("world");

        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("announceAdvancements", "false");
    }

    @Override
    public void onDisable() {
        this.hyrame.disable();
    }

    public Hyrame getHyrame() {
        return this.hyrame;
    }

}
