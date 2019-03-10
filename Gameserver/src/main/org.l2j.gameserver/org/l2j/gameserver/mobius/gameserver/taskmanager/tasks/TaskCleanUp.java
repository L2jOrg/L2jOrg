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
package org.l2j.gameserver.mobius.gameserver.taskmanager.tasks;

import org.l2j.gameserver.mobius.gameserver.taskmanager.Task;
import org.l2j.gameserver.mobius.gameserver.taskmanager.TaskManager.ExecutedTask;

/**
 * @author Tempy
 */
public final class TaskCleanUp extends Task {
    private static final String NAME = "clean_up";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutedTask task) {
        System.runFinalization();
        System.gc();
    }
}
