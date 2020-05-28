package org.l2j.gameserver.model.actor.request;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public abstract class AbstractRequest {
    private volatile long timestamp = 0;
    private volatile boolean isProcessing;
    protected final Player player;
    private ScheduledFuture<?> timeOutTask;

    public AbstractRequest(Player player) {
        Objects.requireNonNull(player);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void scheduleTimeout(long delay) {
        timeOutTask = ThreadPool.schedule(this::onTimeout, delay);
    }

    public boolean isTimeout() {
        return (timeOutTask != null) && !timeOutTask.isDone();
    }

    public void cancelTimeout() {
        if(nonNull(timeOutTask)) {
            timeOutTask.cancel(false);
        }
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }

    public boolean canWorkWith(AbstractRequest request) {
        return true;
    }

    public boolean isItemRequest() {
        return false;
    }

    public abstract boolean isUsing(int objectId);

    public void onTimeout() {

    }
}
