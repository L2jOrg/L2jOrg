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
package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2j.gameserver.world.World;

/**
 * @reworked by Thoss
 */
public class Bomb extends AbstractNpcAI {
    private static final int[] ITEM_DROP_1 = { 49756, 49762, 49763 };
    private static final int[] ITEM_DROP_2 = { 49760, 49761 };

    public Bomb() {
        addKillId(18003);
        addSpawnId(18003);
    }

    @Override
    public String onSpawn(Npc npc) {
        npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), 1));
        return super.onSpawn(npc);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(Rnd.chance(33)) {
            addSpawn(20139, npc, false, 1800000);
        }
        else {
            World.getInstance().forEachVisibleObjectInRange(npc, Playable.class, 200, creature -> {
                if(creature != null && !creature.isDead())
                    creature.reduceCurrentHp(Rnd.get(300, 400), npc, null, DamageInfo.DamageType.ZONE);
            });

            if(Rnd.chance(50))
                if(Rnd.chance(90))
                    npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_1[Rnd.get(ITEM_DROP_1.length)], 1);
                else
                    npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_2[Rnd.get(ITEM_DROP_2.length)], 1);
        }

        if(HeavenlyRift.getAliveNpcCount(npc.getId()) == 0) { //Last
            GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
            GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        }
        return super.onKill(npc, killer, isSummon);
    }
}
