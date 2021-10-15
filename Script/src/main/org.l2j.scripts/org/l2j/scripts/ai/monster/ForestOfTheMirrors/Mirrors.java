/*
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
package org.l2j.scripts.ai.monster.ForestOfTheMirrors;

import io.github.joealisson.primitive.*;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * @author Thoss
 * @author JoeAlisson
 */
public class Mirrors extends AbstractNpcAI {

    private static final int MIRROR_NPC_ID = 20639;
    private static final int DESPAWN_TIME = 600000;
    private static final int MIRROR_COUNT = 4;

    private static final IntIntMap leadersStages = new CHashIntIntMap();
    private static final IntMap<IntList> leaderMinions = new CHashIntMap<>();
    private static final IntMap<Boolean> MinionsState = new CHashIntMap<>(); // <Minion ObjectID, isAlive>

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
                leadersStages.put(leaderObjectId, 0);
                leaderMinions.put(leaderObjectId, new ArrayIntList());
            } else {
                MinionsState.put(npc.getObjectId(), false);
            }

            notifyDeathToLeader(npc, killer, leaderObjectId);
        }
        return super.onKill(npc, killer, isSummon);
    }

    private void notifyDeathToLeader(Npc npc, Player killer, int leaderObjectId) {
        if(getAliveMinions(leaderObjectId) % MIRROR_COUNT == 0) {
            var stage = leadersStages.get(leaderObjectId);
            if(stage < 4) {
                spawnMinions(npc, killer, leaderObjectId);
            } else {
                despawnMinions(leaderObjectId);
            }
        }
    }

    private void despawnMinions(int leaderObjectId) {
        leaderMinions.forEach(MinionsState::remove);
        leaderMinions.remove(leaderObjectId);
        leadersStages.remove(leaderObjectId);
    }

    private void spawnMinions(Npc npc, Player killer, int leaderObjectId) {
        for (int i = 0; i < MIRROR_COUNT; i++) {
            Npc mirror = addSpawn(MIRROR_NPC_ID, npc, true, DESPAWN_TIME);
            leaderMinions.get(leaderObjectId).add(mirror.getObjectId());
            MinionsState.put(mirror.getObjectId(), true);
            addAttackPlayerDesire(mirror, killer);
        }
        leadersStages.computeIfPresent(leaderObjectId, (key, stage) -> stage + 1);
    }

    private int getLeader(int npcObjectId) {
        for(var leader : leaderMinions.entrySet())
            if(leader.getValue().contains(npcObjectId))
                return leader.getKey();

        return -1;
    }

    private int getAliveMinions(int leaderObjectId) {
        int count = 0;
        var it = leaderMinions.get(leaderObjectId).iterator();
        while (it.hasNext()) {
            if(MinionsState.get(it.nextInt()) == Boolean.TRUE) {
                count++;
            }
        }
        return count;
    }

    public static AbstractNpcAI provider() {
        return new Mirrors();
    }
}
