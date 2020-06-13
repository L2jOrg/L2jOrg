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
package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;

/**
 * @author DS
 */
public final class OlympiadAnnouncer implements Runnable {
    private static final int OLY_MANAGER = 31688;
    private int _currentStadium = 0;

    @Override
    public void run() {
        OlympiadGameTask task;
        for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0; _currentStadium++) {
            if (_currentStadium >= OlympiadGameManager.getInstance().getNumberOfStadiums()) {
                _currentStadium = 0;
            }

            task = OlympiadGameManager.getInstance().getOlympiadTask(_currentStadium);
            if ((task != null) && (task.getGame() != null) && task.needAnnounce()) {
                NpcStringId npcString;
                final String arenaId = String.valueOf(task.getGame().getStadiumId() + 1);
                switch (task.getGame().getType()) {
                    case NON_CLASSED: {
                        npcString = NpcStringId.OLYMPIAD_CLASS_FREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                        break;
                    }
                    case CLASSED: {
                        npcString = NpcStringId.OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                        break;
                    }
                    default: {
                        continue;
                    }
                }

                for (Spawn spawn : SpawnTable.getInstance().getSpawns(OLY_MANAGER)) {
                    final Npc manager = spawn.getLastSpawn();
                    if (manager != null) {
                        manager.broadcastSay(ChatType.NPC_SHOUT, npcString, arenaId);
                    }
                }
                break;
            }
        }
    }
}
