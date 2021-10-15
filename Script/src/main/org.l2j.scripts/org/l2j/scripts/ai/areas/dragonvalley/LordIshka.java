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

package org.l2j.scripts.ai.areas.dragonvalley;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.world.World;
import org.l2j.scripts.ai.AbstractNpcAI;

public class LordIshka extends AbstractNpcAI {
    private static final int LORD_ISHKA = 22100;
    private static final int SKILL = 50124;

    private LordIshka()
    {
        addKillId(LORD_ISHKA);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon)
    {
        npc.broadcastPacket(new ExShowScreenMessage(NpcStringId.GLORY_TO_THE_HEROES_WHO_HAVE_DEFEATED_LORD_ISHKA, 2, 5000, true));
        if (killer.isInParty()){
            World.getInstance().forEachPlayerInRange(killer, 1000, player -> SkillCaster.triggerCast(npc, player,  SkillEngine.getInstance().getSkill(SKILL, 1)), player -> killer.getParty().getMembers().contains(player));
        }
        SkillCaster.triggerCast(npc, killer,  SkillEngine.getInstance().getSkill(SKILL, 1));
        return super.onKill(npc, killer, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new LordIshka();
    }
}
