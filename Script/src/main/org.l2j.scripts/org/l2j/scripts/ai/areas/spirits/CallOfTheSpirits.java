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
package org.l2j.scripts.ai.areas.spirits;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

public class CallOfTheSpirits extends AbstractNpcAI {

    // Wasteland
    private static final int MONSTEREYE = 20068;
    private static final int GRANITEGOLEM = 20083;
    private static final int GUARDIANGOLEM = 21656;

    //Execution ground
    private static final int GOUL = 20201;
    private static final int CORPSETRACKER = 20202;
    private static final int GUARDIANDRECO = 21654;

    //Plain of the lizardman
    private static final int LETOWARRIOR = 20580;
    private static final int LETOSHAMAN = 20581;
    private static final int GUARDIANRAIDO = 21655;

    //Sea of spores
    private static final int GIANTMONSTEREYE = 20556;
    private static final int DIREWYRM = 20557;
    private static final int GUARDIANWYRM = 21657;

    //Forest of mirrors
    private static final int LIZARDWARRIOR = 20643;
    private static final int LIZARDMATRIACH = 20645;
    private static final int GUARDIANHARIT = 21658;

    //Seal of shilen
    private static final int CRIMSONDRAKE = 20670;
    private static final int PALIBATI = 20673;
    private static final int GUARDIANPALIBATI = 21660;

    private CallOfTheSpirits()
    {
        addKillId(MONSTEREYE, GRANITEGOLEM,GOUL, CORPSETRACKER, LETOWARRIOR, LETOSHAMAN, GIANTMONSTEREYE, DIREWYRM, LIZARDWARRIOR, LIZARDMATRIACH, CRIMSONDRAKE , PALIBATI);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        final Npc spawnMonster;
        final Playable attacker;
        if (Rnd.chance(10)){
            spawnMonster = switch (npc.getId()) {
                case MONSTEREYE, GRANITEGOLEM -> addSpawn(GUARDIANGOLEM, npc, false, 300000);
                case GOUL, CORPSETRACKER -> addSpawn(GUARDIANDRECO, npc, false, 300000);
                case LETOWARRIOR, LETOSHAMAN -> addSpawn(GUARDIANRAIDO, npc, false, 300000);
                case GIANTMONSTEREYE, DIREWYRM -> addSpawn(GUARDIANWYRM, npc, false, 300000);
                case LIZARDWARRIOR, LIZARDMATRIACH -> addSpawn(GUARDIANHARIT, npc, false, 300000);
                case CRIMSONDRAKE, PALIBATI -> addSpawn(GUARDIANPALIBATI, npc, false, 300000);
                default -> null;
            };
            attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
            addAttackPlayerDesire(spawnMonster, attacker);
            npc.deleteMe();
        }
        return super.onKill(npc, killer, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new CallOfTheSpirits();
    }
}
