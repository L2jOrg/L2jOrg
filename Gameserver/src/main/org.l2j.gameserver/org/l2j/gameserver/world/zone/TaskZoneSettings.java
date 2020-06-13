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
package org.l2j.gameserver.world.zone;

import java.util.concurrent.Future;

/**
 * Basic task zone settings implementation.
 *
 * @author UnAfraid
 */
public class TaskZoneSettings extends AbstractZoneSettings {
    private Future<?> _task;

    /**
     * Gets the task.
     *
     * @return the task
     */
    public Future<?> getTask() {
        return _task;
    }

    /**
     * Sets the task.
     *
     * @param task the new task
     */
    public void setTask(Future<?> task) {
        _task = task;
    }

    @Override
    public void clear() {
        if (_task != null) {
            _task.cancel(true);
            _task = null;
        }
    }
}
