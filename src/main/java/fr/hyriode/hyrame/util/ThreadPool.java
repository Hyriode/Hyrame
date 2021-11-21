package fr.hyriode.hyrame.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/09/2021 at 09:07
 */
public class ThreadPool {

    /** Useful executors: async and scheduler */
    public static final ExecutorService ASYNC_EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Hyrame Async %1$d").build());
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(32);

}
