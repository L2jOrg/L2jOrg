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

import java.util.ArrayList;

/**
 * <p>
 * A {@link TaskCollector} implementation managing a task list in memory.
 * </p>
 *
 * @author Carlo Pelliccia
 * @since 2.0
 */
class MemoryTaskCollector implements TaskCollector {

    /**
     * The inner scheduling pattern list.
     */
    private final ArrayList<SchedulingPattern> patterns = new ArrayList<>();

    /**
     * The inner task list.
     */
    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * IDs for task-pattern couples.
     */
    private final ArrayList<String> ids = new ArrayList<>();

    /**
     * Adds a pattern and a task to the collector.
     *
     * @param pattern The scheduling pattern.
     * @param task    The task.
     * @return An ID for the scheduled operation.
     */
    public synchronized String add(SchedulingPattern pattern, Task task) {
        String id = GUIDGenerator.generate();
        patterns.add(pattern);
        tasks.add(task);
        ids.add(id);
        return id;
    }

    /**
     * Updates a scheduling pattern in the collector.
     *
     * @param id      The ID of the scheduled couple.
     * @param pattern SchedulingPattern
     */
    public synchronized void update(String id, SchedulingPattern pattern) {
        int index = ids.indexOf(id);
        if (index > -1) {
            patterns.set(index, pattern);
        }
    }

    /**
     * Removes a task and its scheduling pattern from the collector.
     *
     * @param id The ID of the scheduled couple.
     * @throws IndexOutOfBoundsException
     */
    public synchronized void remove(String id) throws IndexOutOfBoundsException {
        int index = ids.indexOf(id);
        if (index > -1) {
            tasks.remove(index);
            patterns.remove(index);
            ids.remove(index);
        }
    }

    /**
     * Retrieves a task from the collector.
     *
     * @param id The ID of the scheduled couple.
     * @return The task with the specified assigned ID, or null if it doesn't exist.
     */
    public synchronized Task getTask(String id) {
        int index = ids.indexOf(id);
        if (index > -1) {
            return tasks.get(index);
        }
        return null;
    }

    /**
     * Retrieves a scheduling pattern from the collector.
     *
     * @param id The ID of the scheduled couple.
     * @return The scheduling pattern with the specified assigned ID, or null if it doesn't exist.
     */
    public synchronized SchedulingPattern getSchedulingPattern(String id) {
        int index = ids.indexOf(id);
        if (index > -1) {
            return patterns.get(index);
        }
        return null;
    }

    /**
     * Implements {@link TaskCollector#getTasks()}.
     */
    @Override
    public synchronized TaskTable getTasks() {
        TaskTable ret = new TaskTable();
        int size = tasks.size();
        for (int i = 0; i < size; i++) {
            Task t = tasks.get(i);
            SchedulingPattern p = patterns.get(i);
            ret.add(p, t);
        }
        return ret;
    }
}
