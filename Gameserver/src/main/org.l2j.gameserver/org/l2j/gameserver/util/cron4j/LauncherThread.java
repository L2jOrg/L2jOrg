/*
 * cron4j - A pure Java cron-like scheduler
 *
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.util.cron4j;

/**
 * LauncherThreads are used by {@link Scheduler} instances. A LauncherThread retrieves a list of task from a set of {@link TaskCollector}s. Then it launches, within a separate {@link TaskExecutor}, every retrieved task whose scheduling pattern matches the given reference time.
 *
 * @author Carlo Pelliccia
 * @since 2.0
 */
class LauncherThread extends Thread {
    /**
     * A GUID for this object.
     */
    private final String guid = GUIDGenerator.generate();

    /**
     * The owner scheduler.
     */
    private final Scheduler scheduler;

    /**
     * Task collectors, used to retrieve registered tasks.
     */
    private final TaskCollector[] collectors;

    /**
     * A reference time for task launching.
     */
    private final long referenceTimeInMillis;

    /**
     * Builds the launcher.
     *
     * @param scheduler             The owner scheduler.
     * @param collectors            Task collectors, used to retrieve registered tasks.
     * @param referenceTimeInMillis A reference time for task launching.
     */
    public LauncherThread(Scheduler scheduler, TaskCollector[] collectors, long referenceTimeInMillis) {
        this.scheduler = scheduler;
        this.collectors = collectors;
        this.referenceTimeInMillis = referenceTimeInMillis;
        // Thread name.
        String name = "cron4j::scheduler[" + scheduler.getGuid() + "]::launcher[" + guid + "]";
        setName(name);
    }

    /**
     * Returns the GUID for this object.
     *
     * @return The GUID for this object.
     */
    public Object getGuid() {
        return guid;
    }

    /**
     * Overrides {@link Thread#run()}.
     */
    @Override
    public void run() {
        outer:
        for (TaskCollector collector : collectors) {
            TaskTable taskTable = collector.getTasks();
            int size = taskTable.size();
            for (int j = 0; j < size; j++) {
                if (isInterrupted()) {
                    break outer;
                }
                SchedulingPattern pattern = taskTable.getSchedulingPattern(j);
                if (pattern.match(scheduler.getTimeZone(), referenceTimeInMillis)) {
                    Task task = taskTable.getTask(j);
                    scheduler.spawnExecutor(task);
                }
            }
        }
        // Notifies completed.
        scheduler.notifyLauncherCompleted(this);
    }
}
