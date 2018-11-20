package org.l2j.authserver.controller;

import java.util.concurrent.*;

import static java.util.Objects.isNull;

public class ThreadPoolManager  {

    private static ThreadPoolManager _instance;
    private final ScheduledThreadPoolExecutor scheduledExecutor;
    private final ThreadPoolExecutor executor;

    public static ThreadPoolManager getInstance() {
        if(isNull(_instance)) {
            _instance = new ThreadPoolManager();
        }
        return _instance;
    }

    private ThreadPoolManager() {
        scheduledExecutor = new ScheduledThreadPoolExecutor(2);
        executor = new ThreadPoolExecutor(4, 8, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    private long validate(final long delay) {
        return Math.max(0L, delay);
    }

    public void execute(final Runnable r) {
        executor.execute(r);
    }

    public ScheduledFuture<?> schedule(final Runnable r, final long delay) {
        return scheduledExecutor.schedule(r, validate(delay), TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable r, final long initial, final long delay) {
        return scheduledExecutor.scheduleAtFixedRate(r, validate(initial), validate(delay), TimeUnit.MILLISECONDS);
    }
}