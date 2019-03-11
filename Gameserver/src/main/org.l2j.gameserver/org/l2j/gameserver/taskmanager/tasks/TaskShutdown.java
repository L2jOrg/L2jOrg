package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.taskmanager.Task;
import org.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author Layane
 */
public class TaskShutdown extends Task {
    private static final String NAME = "shutdown";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutedTask task) {
        final Shutdown handler = new Shutdown(Integer.parseInt(task.getParams()[2]), false);
        handler.start();
    }
}
