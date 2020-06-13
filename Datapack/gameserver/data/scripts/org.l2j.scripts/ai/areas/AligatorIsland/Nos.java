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
package ai.areas.AligatorIsland;


import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;


public class Nos extends AbstractNpcAI {

    private final int MONSTER_CHANCE_SPAWN = 70;
    private final int NOS = 20793;
    private final int CROKIAN = 20804;
    private final int MONSTER_DESPAWN_DELAY = 300000;

    private Nos()
    {
    addAggroRangeEnterId(CROKIAN);
    }

    @Override
    public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon) {
        if(Rnd.get(100) <= MONSTER_CHANCE_SPAWN) {
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
        }
        return super.onAggroRangeEnter(npc, player, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new ai.areas.AligatorIsland.Nos();
    }
}
