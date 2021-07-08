/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.scripts.ai.areas.DragonValley;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Cave Maiden, Keeper AI.
 * @author proGenitor
 */
public final class CaveMaiden extends AbstractNpcAI
{
	// NPCs
	private static final int CAVEMAIDEN = 20134;
	private static final int CAVEKEEPER = 20246;
	private static final int BANSHEE = 20412;
	
	private CaveMaiden()
	{
		addKillId(CAVEMAIDEN, CAVEKEEPER);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (Rnd.get(100) < 20)
		{
			final Npc spawnBanshee = addSpawn(BANSHEE, npc, false, 300000);
			final Playable attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
			addAttackPlayerDesire(spawnBanshee, attacker);
			npc.deleteMe();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static AbstractNpcAI provider()
	{
		return new CaveMaiden();
	}
}