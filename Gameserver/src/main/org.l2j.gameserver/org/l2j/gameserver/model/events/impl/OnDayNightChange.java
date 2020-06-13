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
