package org.l2j.gameserver.model.actor.request;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @author UnAfraid
 */
public abstract class AbstractRequest {
    private final L2PcInstance _activeChar;
    private volatile long _timestamp = 0;
    private volatile boolean _isProcessing;
    private ScheduledFuture<?> _timeOutTask;

    public AbstractRequest(L2PcInstance activeChar) {
        Objects.requireNonNull(activeChar);
        _activeChar = activeChar;
    }

    public L2PcInstance getActiveChar() {
        return _activeChar;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }

    public void scheduleTimeout(long delay) {
        _timeOutTask = ThreadPoolManager.getInstance().schedule(this::onTimeout, delay);
    }

    public boolean isTimeout() {
        return (_timeOutTask != null) && !_timeOutTask.isDone();
    }

    public boolean isProcessing() {
        return _isProcessing;
    }

    public boolean setProcessing(boolean isProcessing) {
        return _isProcessing = isProcessing;
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
