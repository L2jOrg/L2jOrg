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
package org.l2j.gameserver.model.eventengine;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.EventDAO;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.util.cron4j.PastPredictor;
import org.l2j.gameserver.util.cron4j.Predictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author UnAfraid
 */
public class EventScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventScheduler.class);
    private final AbstractEventManager<?> eventManager;
    private final String name;
    private final String _pattern;
    private final boolean _repeat;
    private List<EventMethodNotification> notifications;
    private ScheduledFuture<?> _task;
    private long lastRun = 0;

    public EventScheduler(AbstractEventManager<?> manager, StatsSet set) {
        eventManager = manager;
        name = set.getString("name", "");
        _pattern = set.getString("minute", "*") + " " + set.getString("hour", "*") + " " + set.getString("dayOfMonth", "*") + " " + set.getString("month", "*") + " " + set.getString("dayOfWeek", "*");
        _repeat = set.getBoolean("repeat", false);
    }

    public String getName() {
        return name;
    }

    public long getNextSchedule() {
        final Predictor predictor = new Predictor(_pattern);
        return predictor.nextMatchingTime();
    }

    public long getNextSchedule(long fromTime) {
        final Predictor predictor = new Predictor(_pattern, fromTime);
        return predictor.nextMatchingTime();
    }

    public long getPrevSchedule() {
        final PastPredictor predictor = new PastPredictor(_pattern);
        return predictor.prevMatchingTime();
    }

    public long getPrevSchedule(long fromTime) {
        final PastPredictor predictor = new PastPredictor(_pattern, fromTime);
        return predictor.prevMatchingTime();
    }

    public boolean isRepeating() {
        return _repeat;
    }

    public void addEventNotification(EventMethodNotification notification) {
        if (notifications == null) {
            notifications = new ArrayList<>();
        }
        notifications.add(notification);
    }

    public List<EventMethodNotification> getEventNotifications() {
        return notifications;
    }

    public void startScheduler() {
        if (notifications == null) {
            LOGGER.info("Scheduler without notificator manager: " + eventManager.getClass().getSimpleName() + " pattern: " + _pattern);
            return;
        }

        final Predictor predictor = new Predictor(_pattern);
        final long nextSchedule = predictor.nextMatchingTime();
        final long timeSchedule = nextSchedule - System.currentTimeMillis();
        if (timeSchedule <= (30 * 1000)) {
            LOGGER.warn("Wrong reschedule for " + eventManager.getClass().getSimpleName() + " end up run in " + (timeSchedule / 1000) + " seconds!");
            ThreadPool.schedule(this::startScheduler, timeSchedule + 1000);
            return;
        }

        if (_task != null) {
            _task.cancel(false);
        }

        _task = ThreadPool.schedule(() ->
        {
            run();
            updateLastRun();

            if (_repeat) {
                ThreadPool.schedule(this::startScheduler, 1000);
            }
        }, timeSchedule);
    }

    public boolean updateLastRun() {
        lastRun = System.currentTimeMillis();
        getDAO(EventDAO.class).updateLastRun(eventManager.getName(), name, lastRun);
        return true;
    }

    public void stopScheduler() {
        if (_task != null) {
            _task.cancel(false);
            _task = null;
        }
    }

    public long getRemainingTime(TimeUnit unit) {
        return (_task != null) && !_task.isDone() ? _task.getDelay(unit) : 0;
    }

    public long getLastRun() {
        return lastRun;
    }

    public void run() {
        for (EventMethodNotification notification : notifications) {
            try {
                notification.execute();
            } catch (Exception e) {
                LOGGER.warn("Failed to notify to event manager: {} method: {}", notification.getManager().getClass().getSimpleName(),  notification.getMethod().getName());
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }
}
