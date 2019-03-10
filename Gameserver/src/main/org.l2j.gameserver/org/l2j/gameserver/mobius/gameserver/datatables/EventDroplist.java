/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.datatables;

import org.l2j.gameserver.mobius.gameserver.script.DateRange;
import org.l2j.gameserver.mobius.gameserver.script.EventDrop;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class manage drop of Special Events created by GM for a defined period.<br>
 * During a Special Event all L2Attackable can drop extra Items.<br>
 * Those extra Items are defined in the table <b>allNpcDateDrops</b>.<br>
 * Each Special Event has a start and end date to stop to drop extra Items automatically.
 */
public class EventDroplist {
    /**
     * The table containing all DataDrop object
     */
    private static final List<DateDrop> ALL_NPC_DATE_DROPS = new CopyOnWriteArrayList<>();

    public static EventDroplist getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Create and Init a new DateDrop then add it to the allNpcDateDrops of EventDroplist .
     *
     * @param itemIdList The table containing all item identifier of this DateDrop
     * @param count      The table containing min and max value of this DateDrop
     * @param chance     The chance to obtain this drop
     * @param dateRange  The DateRange object to add to this DateDrop
     */
    public void addGlobalDrop(int[] itemIdList, int[] count, int chance, DateRange dateRange) {
        ALL_NPC_DATE_DROPS.add(new DateDrop(dateRange, new EventDrop(itemIdList, count[0], count[1], chance)));
    }

    /**
     * @param itemId    the item Id for the drop
     * @param min       the minimum drop count
     * @param max       the maximum drop count
     * @param chance    the drop chance
     * @param dateRange the event drop rate range
     */
    public void addGlobalDrop(int itemId, long min, long max, int chance, DateRange dateRange) {
        ALL_NPC_DATE_DROPS.add(new DateDrop(dateRange, new EventDrop(itemId, min, max, chance)));
    }

    /**
     * Adds an event drop for a given date range.
     *
     * @param dateRange the date range.
     * @param eventDrop the event drop.
     */
    public void addGlobalDrop(DateRange dateRange, EventDrop eventDrop) {
        ALL_NPC_DATE_DROPS.add(new DateDrop(dateRange, eventDrop));
    }

    /**
     * @return all DateDrop of EventDroplist allNpcDateDrops within the date range.
     */
    public List<DateDrop> getAllDrops() {
        final List<DateDrop> list = new LinkedList<>();
        final Date currentDate = new Date();
        for (DateDrop drop : ALL_NPC_DATE_DROPS) {
            if (drop._dateRange.isWithinRange(currentDate)) {
                list.add(drop);
            }
        }
        return list;
    }

    public static class DateDrop {
        protected final DateRange _dateRange;
        private final EventDrop _eventDrop;

        public DateDrop(DateRange dateRange, EventDrop eventDrop) {
            _dateRange = dateRange;
            _eventDrop = eventDrop;
        }

        /**
         * @return the _eventDrop
         */
        public EventDrop getEventDrop() {
            return _eventDrop;
        }

        /**
         * @return the _dateRange
         */
        public DateRange getDateRange() {
            return _dateRange;
        }
    }

    private static class SingletonHolder {
        protected static final EventDroplist _instance = new EventDroplist();
    }
}
