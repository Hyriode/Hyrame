package fr.hyriode.hyrame.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/09/2021 at 09:07
 */
public class ThreadUtil {

    /** Useful executors: async and scheduler */
    public static final ExecutorService ASYNC_EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Hyrame Async %1$d").build());
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(32);

    /**
     * Get back on the main Bukkit thread
     *
     * @param plugin A {@link JavaPlugin} instance
     * @param runnable The runnable to execute
     */
    public static void backOnMainThread(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

}
