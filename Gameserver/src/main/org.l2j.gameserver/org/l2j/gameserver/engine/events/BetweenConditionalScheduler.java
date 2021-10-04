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

import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public record BetweenConditionalScheduler(AbstractEventManager<?> eventManager, String name, String scheduler1, String scheduler2) implements IConditionalEventScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BetweenConditionalScheduler.class);

    @Override
    public boolean test() {
        var before = eventManager.getScheduler(scheduler1);
        var after = eventManager.getScheduler(scheduler2);

        final long previousStart = before.getPrevSchedule();
        final long previousEnd = after.getPrevSchedule();
        return previousStart > previousEnd;
    }

    @Override
    public void run() {
        final EventScheduler mainScheduler = eventManager.getScheduler(name);
        mainScheduler.run();
        LOGGER.info("Event " + eventManager.getClass().getSimpleName() + " will resume because is within the event period");
    }
}
