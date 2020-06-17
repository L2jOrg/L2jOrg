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
package ai.areas.ForestOfTheMirrors;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thoss
 */
public class Mirrors extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(Mirrors.class);

    private static final int MIRROR_NPC_ID = 20639;
    private static final int DESPAWN_TIME = 600000;
    private static final int MIRROR_COUNT = 4;

    private static Map<Integer, Integer> _Leaders_Stages = new ConcurrentHashMap<>(); // <Leader ObjectID, Leader Stage>
    private static Map<Integer, List<Integer>> _Leader_Minions = new ConcurrentHashMap<>(); // <Leader ObjectID, Leader minions ObjectID>
    private static Map<Integer, Boolean> _Minions_State = new ConcurrentHashMap<>(); // <Minion ObjectID, isAlive>

    public Mirrors()
    {
        addKillId(MIRROR_NPC_ID);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(npc.getId() == MIRROR_NPC_ID) {
            int leaderObjectId = getLeader(npc.getObjectId());

            if(leaderObjectId == -1) {
                leaderObjectId = npc.getObjectId();
                _Leaders_Stages.put(leaderObjectId, 0);
                _Leader_Minions.put(leaderObjectId, new ArrayList<>());
            } else
                _Minions_State.put(npc.getObjectId(), false);
            
            if(getAliveMinions(leaderObjectId) % MIRROR_COUNT == 0)
                switch (_Leaders_Stages.get(leaderObjectId)) {
                    case 0, 1, 2, 3 -> {
                        for (int i = 0; i < MIRROR_COUNT; i++) {
                            Npc mirror = addSpawn(MIRROR_NPC_ID, npc, true, DESPAWN_TIME);
                            _Leader_Minions.get(leaderObjectId).add(mirror.getObjectId());
                            _Minions_State.put(mirror.getObjectId(), true);
                            addAttackPlayerDesire(mirror, killer);
                        }
                        _Leaders_Stages.replace(leaderObjectId, _Leaders_Stages.get(leaderObjectId) + 1);

                    }
                    case 4 -> {
                        for (int minion : _Leader_Minions.get(leaderObjectId))
                            _Minions_State.remove(minion);
                        _Leader_Minions.remove(leaderObjectId);
                        _Leaders_Stages.remove(leaderObjectId);
                    }
                }
        }
        return super.onKill(npc, killer, isSummon);
    }

    private int getLeader(int npcObjectId) {
        for(Map.Entry<Integer, List<Integer>> leader : _Leader_Minions.entrySet())
            if(leader.getValue().contains(npcObjectId))
                return leader.getKey();

        return -1;
    }

    private int getAliveMinions(int leaderObjectId) {
        int count = 0;

        for(int minion : _Leader_Minions.get(leaderObjectId))
            if(_Minions_State.get(minion) == true)
                count++;

        return count;
    }

    public static AbstractNpcAI provider()
    {
        return new Mirrors();
    }
}
