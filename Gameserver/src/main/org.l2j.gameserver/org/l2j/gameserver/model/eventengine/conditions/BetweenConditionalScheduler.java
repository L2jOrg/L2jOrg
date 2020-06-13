/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.eventengine.conditions;

import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


/**
 * @author UnAfraid
 */
public class BetweenConditionalScheduler implements IConditionalEventScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BetweenConditionalScheduler.class);
    private final AbstractEventManager<?> _eventManager;
    private final String _name;
    private final String _scheduler1;
    private final String _scheduler2;

    public BetweenConditionalScheduler(AbstractEventManager<?> eventManager, String name, String scheduler1, String scheduler2) {
        Objects.requireNonNull(eventManager);
        Objects.requireNonNull(name);
        Objects.requireNonNull(scheduler1);
        Objects.requireNonNull(scheduler2);

        _eventManager = eventManager;
        _name = name;
        _scheduler1 = scheduler1;
        _scheduler2 = scheduler2;
    }

    @Override
    public boolean test() {
        final EventScheduler scheduler1 = _eventManager.getScheduler(_scheduler1);
        final EventScheduler scheduler2 = _eventManager.getScheduler(_scheduler2);
        if (scheduler1 == null) {
            throw new NullPointerException("Scheduler1 not found: " + _scheduler1);
        } else if (scheduler2 == null) {
            throw new NullPointerException("Scheduler2 not found: " + _scheduler2);
        }

        final long previousStart = scheduler1.getPrevSchedule();
        final long previousEnd = scheduler2.getPrevSchedule();
        return previousStart > previousEnd;
    }

    @Override
    public void run() {
        final EventScheduler mainScheduler = _eventManager.getScheduler(_name);
        if (mainScheduler == null) {
            throw new NullPointerException("Main scheduler not found: " + _name);
        }
        mainScheduler.run();
        LOGGER.info("Event " + _eventManager.getClass().getSimpleName() + " will resume because is within the event period");
    }
}
