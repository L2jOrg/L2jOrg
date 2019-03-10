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
package org.l2j.gameserver.mobius.gameserver.instancemanager.events;

import org.l2j.gameserver.mobius.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.mobius.gameserver.model.quest.Event;

/**
 * @author Mobius
 */
public class TeamVsTeamManager extends AbstractEventManager<AbstractEvent<?>> {
    protected TeamVsTeamManager() {
    }

    public static TeamVsTeamManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onInitialized() {
    }

    @ScheduleTarget
    protected void startEvent() {
        final Event event = (Event) QuestManager.getInstance().getQuest("TvT");
        if (event != null) {
            event.eventStart(null);
        }
    }

    private static class SingletonHolder {
        protected static final TeamVsTeamManager INSTANCE = new TeamVsTeamManager();
    }
}
