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
package ai.areas.CallOfTheSpirits;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallOfTheSpirits extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(CallOfTheSpirits.class);

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
            switch (npc.getId()){
                case MONSTEREYE:
                case GRANITEGOLEM:
                    spawnMonster = addSpawn(GUARDIANGOLEM, npc, false, 300000);
                    break;
                case GOUL:
                case CORPSETRACKER:
                    spawnMonster = addSpawn(GUARDIANDRECO, npc, false, 300000);
                    break;
                case LETOWARRIOR:
                case LETOSHAMAN:
                    spawnMonster = addSpawn(GUARDIANRAIDO, npc, false, 300000);
                    break;
                case GIANTMONSTEREYE:
                case DIREWYRM:
                    spawnMonster = addSpawn(GUARDIANWYRM, npc, false, 300000);
                    break;
                case LIZARDWARRIOR:
                case LIZARDMATRIACH:
                    spawnMonster = addSpawn(GUARDIANHARIT, npc, false, 300000);
                    break;
                case CRIMSONDRAKE:
                case PALIBATI:
                    spawnMonster = addSpawn(GUARDIANPALIBATI, npc, false, 300000);
                    break;
                default:
                    spawnMonster = null;
                    break;
            }
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
