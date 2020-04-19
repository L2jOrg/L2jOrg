package org.l2j.gameserver.datatables.drop;

import org.l2j.commons.util.DateRange;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class manage drop of Special Events created by GM for a defined period.<br>
 * During a Special Event all Attackable can drop extra Items.<br>
 * Those extra Items are defined in the table <b>allNpcDateDrops</b>.<br>
 * Each Special Event has a start and end date to stop to drop extra Items automatically.
 *
 * @author JoeAlisson
 */
public class EventDropList {
    /**
     * The table containing all DataDrop object
     */
    private final Collection<DateDrop> allNpcEventsDrops = ConcurrentHashMap.newKeySet();

    private EventDropList() {}

    public void addGlobalDrop(EventDropHolder drop, DateRange period) {
        allNpcEventsDrops.add(new DateDrop(period, drop));
    }

    /**
     * @return all DateDrop of EventDropList allNpcDateDrops within the date range.
     */
    public List<DateDrop> getAllDrops() {
        final var currentDate = LocalDate.now();
        return allNpcEventsDrops.stream().filter(d -> d.dateRange.isWithinRange(currentDate)).collect(Collectors.toList());
    }

    public static class DateDrop {

        private final DateRange dateRange;
        private final EventDropHolder dropHolder;

        private DateDrop(DateRange dateRange, EventDropHolder eventDrop) {
            this.dateRange = dateRange;
            dropHolder = eventDrop;
        }

        public boolean monsterCanDrop(int id, int level) {
            return dropHolder.checkLevel(level) && dropHolder.hasMonster(id);
        }

        public double getChance() {
            return dropHolder.getChance();
        }

        public int getItemId() {
            return dropHolder.getItemId();
        }

        public long getMin() {
            return dropHolder.getMin();
        }

        public long getMax() {
            return dropHolder.getMax();
        }
    }

    public static EventDropList getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final EventDropList INSTANCE = new EventDropList();
    }
}
