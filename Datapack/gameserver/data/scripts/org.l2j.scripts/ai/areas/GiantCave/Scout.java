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
package ai.areas.GiantCave;


import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.GameUtils.isMonster;

public class Scout extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Scout.class);


    private static final int SPAWN_DELAY = 10000; // milliseconds
    private static final int GAMLIN = 20651;
    private static final int LEOGUL = 20652;

    private Scout()
    {
        addAttackId(GAMLIN, LEOGUL);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("GC_SCOUT_EVENT_AI")) {
            final Playable pAttacker = player.getServitors().size() > 0 ? player.getServitors().values().stream().findFirst().orElse(player.getPet()) : player;
            final Monster monster = (Monster) npc;

            if (monster != null && !monster.isDead() && !monster.isTeleporting() && !monster.hasMinions())
                for (MinionHolder is : npc.getParameters().getMinionList("Privates")) {
                    monster.getMinionList().spawnMinions(monster.getParameters().getMinionList("Privates"));
                    monster.getMinionList().getSpawnedMinions().forEach(minion -> {
                        addAttackPlayerDesire(minion, pAttacker);
                    });
                }
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
    {
        if (isMonster(npc))
        {
            final Monster monster = (Monster) npc;

            if (!monster.isTeleporting() && !monster.hasMinions() && getQuestTimer("GC_SCOUT_EVENT_AI", npc, attacker) == null)
                startQuestTimer("GC_SCOUT_EVENT_AI", SPAWN_DELAY, npc, attacker);
        }

        return super.onAttack(npc, attacker, damage, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new Scout();
    }
}
