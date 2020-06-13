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
package org.l2j.gameserver.instancemanager;

/**
 * @author Mobius
 */
public final class EventShrineManager {
    private static boolean ENABLE_SHRINES = false;

    private EventShrineManager() { }

    public static EventShrineManager getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean areShrinesEnabled() {
        return ENABLE_SHRINES;
    }

    public void setEnabled(boolean enabled) {
        ENABLE_SHRINES = enabled;
    }

    private static class Singleton {
        private static final EventShrineManager INSTANCE = new EventShrineManager();
    }
}
