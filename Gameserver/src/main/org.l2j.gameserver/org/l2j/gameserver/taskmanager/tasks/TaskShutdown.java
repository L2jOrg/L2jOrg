package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.Shutdown;
import org.l2j.gameserver.taskmanager.Task;
import org.l2j.gameserver.taskmanager.TaskManager.ExecutableTask;

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
    public void onTimeElapsed(ExecutableTask task) {
        final Shutdown handler = new Shutdown(Integer.parseInt(task.getParam3()), false);
        handler.start();
    }
}
