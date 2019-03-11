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
package org.l2j.gameserver.model.events.impl;

import org.l2j.gameserver.model.events.EventType;

/**
 * @author UnAfraid
 */
public class OnDayNightChange implements IBaseEvent {
    private final boolean _isNight;

    public OnDayNightChange(boolean isNight) {
        _isNight = isNight;
    }

    public boolean isNight() {
        return _isNight;
    }

    @Override
    public EventType getType() {
        return EventType.ON_DAY_NIGHT_CHANGE;
    }
}
