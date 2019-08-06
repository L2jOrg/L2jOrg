package org.l2j.gameserver.world.zone;

import java.util.concurrent.Future;

/**
 * Basic task zone settings implementation.
 *
 * @author UnAfraid
 */
public class TaskZoneSettings extends AbstractZoneSettings {
    private Future<?> _task;

    /**
     * Gets the task.
     *
     * @return the task
     */
    public Future<?> getTask() {
        return _task;
    }

    /**
     * Sets the task.
     *
     * @param task the new task
     */
    public void setTask(Future<?> task) {
        _task = task;
    }

    @Override
    public void clear() {
        if (_task != null) {
            _task.cancel(true);
            _task = null;
        }
    }
}
