package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.taskmanager.Task;
import org.l2j.gameserver.taskmanager.TaskManager;

/**
 * @author Layane
 */
public final class TaskRestart extends Task {
    private static final String NAME = "restart";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(TaskManager.ExecutedTask task) {
        final Shutdown handler = new Shutdown(Integer.parseInt(task.getParams()[2]), true);
        handler.start();
    }
}
