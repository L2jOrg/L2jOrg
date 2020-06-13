/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.taskmanager;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.TaskDAO;
import org.l2j.gameserver.data.database.data.TaskData;
import org.l2j.gameserver.taskmanager.tasks.TaskBirthday;
import org.l2j.gameserver.taskmanager.tasks.TaskRestart;
import org.l2j.gameserver.taskmanager.tasks.TaskShutdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Layane
 */
public final class TaskManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class.getName());

    private final IntMap<Task> tasks = new CHashIntMap<>();

    private TaskManager() {
        initializate();
        startAllTasks();
        LOGGER.info("Loaded: {} Tasks", tasks.size());
    }

    public static boolean addUniqueTask(String task, TaskType type, String param1, String param2, String param3) {
        return addUniqueTask(task, type, param1, param2, param3, 0);
    }

    private static boolean addUniqueTask(String task, TaskType type, String param1, String param2, String param3, long lastActivation) {
        var taskDao = getDAO(TaskDAO.class);

        if(!taskDao.existsWithName(task)) {
            var data = new TaskData();
            data.setLastActivation(lastActivation);
            data.setName(task);
            data.setType(type.name());
            data.setParam1(param1);
            data.setParam2(param2);
            data.setParam3(param3);
            return taskDao.save(data);
        }
        return false;
    }

    private void initializate() {
        registerTask(new TaskBirthday());
        registerTask(new TaskRestart());
        registerTask(new TaskShutdown());
    }

    private void registerTask(Task task) {
        tasks.computeIfAbsent(task.getName().hashCode(), k -> {
            task.initializate();
            return task;
        });
    }

    private void startAllTasks() {
        getDAO(TaskDAO.class).findAll().forEach(data -> {
            final var task = tasks.get(data.getName().trim().toLowerCase().hashCode());
            if(isNull(task)) {
                return;
            }

            var type = TaskType.valueOf(data.geType());
            if(TaskType.NONE != type) {
                final ExecutableTask current = new ExecutableTask(task, type, data);
                launchTask(current);
            }
        });
    }

    private boolean launchTask(ExecutableTask task) {
        long delay;
        long interval;
        return switch (task.getType()) {
            case STARTUP -> {
                task.run();
                yield false;
            }
            case SHEDULED -> {
                delay = Long.parseLong(task.getParam1());
                task.scheduled = ThreadPool.schedule(task, delay);
                yield true;
            }
            case FIXED_SHEDULED -> {
                delay = Long.parseLong(task.getParam1());
                interval = Long.parseLong(task.getParam2());
                task.scheduled = ThreadPool.scheduleAtFixedRate(task, delay, interval);
                yield true;
            }
            case TIME -> {
                try {
                    final Date desired = DateFormat.getInstance().parse(task.getParam1());
                    final long diff = desired.getTime() - currentTimeMillis();
                    if (diff >= 0) {
                        task.scheduled = ThreadPool.schedule(task, diff);
                        yield true;
                    }
                    LOGGER.info("Task {} is obsoleted", task.getId());
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
                yield false;
            }
            case SPECIAL -> {
                final ScheduledFuture<?> result = task.getTask().launchSpecial(task);
                if (result != null) {
                    task.scheduled = result;
                    yield true;
                }
                yield false;
            }
            case GLOBAL_TASK -> {
                interval = Long.parseLong(task.getParam1()) * 86400000;
                final String[] hour = task.getParam2().split(":");

                if (hour.length != 3) {
                    LOGGER.warn("Task {} has incorrect parameters {}", task.getId(), task.getParam2());
                    yield false;
                }

                final Calendar check = Calendar.getInstance();
                check.setTimeInMillis(task.getLastActivation() + interval);

                final Calendar min = Calendar.getInstance();
                try {
                    min.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour[0]));
                    min.set(Calendar.MINUTE, Integer.parseInt(hour[1]));
                    min.set(Calendar.SECOND, Integer.parseInt(hour[2]));
                } catch (Exception e) {
                    LOGGER.warn("Bad parameter " + task.getParam2() + " on task " + task.getId() + ": " + e.getMessage(), e);
                    yield   false;
                }

                delay = min.getTimeInMillis() - currentTimeMillis();

                if (check.after(min) || (delay < 0)) {
                    delay += interval;
                }
                task.scheduled = ThreadPool.scheduleAtFixedRate(task, delay, interval);
                yield true;
            }
            default -> false;
        };
    }

    public static TaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TaskManager INSTANCE = new TaskManager();
    }

    public static class ExecutableTask implements Runnable {
        private final Task task;
        private final TaskType type;
        private final TaskData data;
        ScheduledFuture<?> scheduled;

        private ExecutableTask(Task task, TaskType type, TaskData data) {
            this.task = task;
            this.type = type;
            this.data = data;
        }

        @Override
        public void run() {
            task.onTimeElapsed(this);
            var lastActivation = currentTimeMillis();
            data.setLastActivation(lastActivation);
            getDAO(TaskDAO.class).updateLastActivation(data.getId(), lastActivation);

            if ((type == TaskType.SHEDULED) || (type == TaskType.TIME)) {
                stopTask();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExecutableTask that = (ExecutableTask) o;
            return Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }

        public Task getTask() {
            return task;
        }

        public TaskType getType() {
            return type;
        }

        public int getId() {
            return data.getId();
        }

        public long getLastActivation() {
            return data.getLastActivation();
        }

        private void stopTask() {
            task.onDestroy();

            if (scheduled != null) {
                scheduled.cancel(true);
            }
        }

        String getParam1() {
            return data.getParam1();
        }

        String getParam2() {
            return data.getParam2();
        }

        public String getParam3() {
            return data.getparam3();
        }
    }
}
