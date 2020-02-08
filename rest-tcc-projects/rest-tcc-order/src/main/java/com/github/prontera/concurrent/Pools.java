package com.github.prontera.concurrent;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhao Junjian
 * @date 2020/01/29
 */
public final class Pools {

    public static final ExecutorService COMPUTATION;

    public static final ExecutorService IO;

    public static final ScheduledExecutorService HEARTBEAT;

    static {
        final String name = "computation-pool";
        final int processors = Runtime.getRuntime().availableProcessors();
        COMPUTATION = new ThreadPoolExecutor(processors, processors,
            1, TimeUnit.MINUTES,
            Queues.newLinkedBlockingQueue(4096),
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new LoggableAbortPolicy(name));
    }

    static {
        final String name = "io-bound-pool";
        IO = new ThreadPoolExecutor(8, 16,
            1, TimeUnit.MINUTES,
            Queues.newLinkedBlockingQueue(4096),
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new LoggableAbortPolicy(name));
    }

    static {
        final String name = "tcc-heartbeat";
        HEARTBEAT = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new LoggableAbortPolicy(name));
    }

    private Pools() {
    }
}
