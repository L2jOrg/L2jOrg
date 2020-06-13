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

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.EventScheduler;
import org.l2j.gameserver.model.eventengine.IConditionalEventScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author UnAfraid
 */
public class HaventRunConditionalScheduler implements IConditionalEventScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HaventRunConditionalScheduler.class);
    private final AbstractEventManager<?> _eventManager;
    private final String _name;

    public HaventRunConditionalScheduler(AbstractEventManager<?> eventManager, String name) {
        _eventManager = eventManager;
        _name = name;
    }

    @Override
    public boolean test() {
        final EventScheduler mainScheduler = _eventManager.getScheduler(_name);
        if (mainScheduler == null) {
            throw new NullPointerException("Scheduler not found: " + _name);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT lastRun FROM event_schedulers WHERE eventName = ? AND schedulerName = ?")) {
            ps.setString(1, _eventManager.getName());
            ps.setString(2, mainScheduler.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final long lastRun = rs.getTimestamp(1).getTime();
                    final long lastPossibleRun = mainScheduler.getPrevSchedule();
                    return (lastPossibleRun > lastRun) && (Math.abs(lastPossibleRun - lastRun) > 1000);
                }
            }
        } catch (SQLException e) {
            LOGGER.warn("Failed to retreive information for scheduled task event manager: " + _eventManager.getClass().getSimpleName() + " scheduler: " + _name, e);
        }
        return false;
    }

    @Override
    public void run() {
        final EventScheduler mainScheduler = _eventManager.getScheduler(_name);
        if (mainScheduler == null) {
            throw new NullPointerException("Scheduler not found: " + _name);
        }

        if (mainScheduler.updateLastRun()) {
            mainScheduler.run();
        }
    }
}
