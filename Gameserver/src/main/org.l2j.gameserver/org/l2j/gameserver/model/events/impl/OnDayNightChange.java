package org.l2j.gameserver.model.events.impl;

import org.l2j.gameserver.model.events.EventType;

/**
 * @author UnAfraid
 */
public class OnDayNightChange implements IBaseEvent {

    private static final OnDayNightChange NIGHT_CHANGE = new OnDayNightChange(true);
    private static final OnDayNightChange DAY_CHANGE = new OnDayNightChange(false);

    private final boolean _isNight;

    private OnDayNightChange(boolean isNight) {
        _isNight = isNight;
    }

    public static OnDayNightChange of(boolean isNight) {
        return isNight ? NIGHT_CHANGE : DAY_CHANGE;
    }

    public boolean isNight() {
        return _isNight;
    }

    @Override
    public EventType getType() {
        return EventType.ON_DAY_NIGHT_CHANGE;
    }
}
