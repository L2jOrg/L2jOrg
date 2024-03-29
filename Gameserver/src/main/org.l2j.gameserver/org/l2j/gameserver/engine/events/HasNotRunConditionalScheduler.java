/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.events;

import org.l2j.gameserver.data.database.dao.EventDAO;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventScheduler;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public record HasNotRunConditionalScheduler(AbstractEventManager<?> eventManager, String name) implements IConditionalEventScheduler {

    @Override
    public boolean test() {
        final EventScheduler mainScheduler = eventManager.getScheduler(name);
        if (isNull(mainScheduler)) {
            throw new NullPointerException("Scheduler not found: " + name);
        }

        long lastRun = getDAO(EventDAO.class).findLastRun(eventManager.getName(), mainScheduler.getName());
        final long lastPossibleRun = mainScheduler.getPrevSchedule();
        return (lastPossibleRun > lastRun) && (Math.abs(lastPossibleRun - lastRun) > 1000);
    }

    @Override
    public void run() {
        final EventScheduler mainScheduler = eventManager.getScheduler(name);
        if (mainScheduler.updateLastRun()) {
            mainScheduler.run();
        }
    }
}
