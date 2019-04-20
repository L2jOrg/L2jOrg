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
package org.l2j.gameserver.taskmanager;

import org.l2j.gameserver.taskmanager.TaskManager.ExecutableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;


/**
 * @author Layane
 */
public abstract class Task {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());

    public void initializate() {
    }

    public ScheduledFuture<?> launchSpecial(ExecutableTask instance) {
        return null;
    }

    public abstract String getName();

    public abstract void onTimeElapsed(ExecutableTask task);

    public void onDestroy() {
    }
}
