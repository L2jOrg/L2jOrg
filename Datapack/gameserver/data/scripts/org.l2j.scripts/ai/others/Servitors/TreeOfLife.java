/*
 * Copyright © 2019 L2J Mobius
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
package ai.others.Servitors;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Tree of Life AI.
 * @author St3eT.
 */
public final class TreeOfLife extends AbstractNpcAI
{
	// NPCs
	private static final int[] TREE_OF_LIFE =
	{
		14933,
		14943,
		15010,
		15011,
		15154,
	};
	
	private TreeOfLife()
	{
		addSummonSpawnId(TREE_OF_LIFE);
	}
	
	@Override
	public void onSummonSpawn(Summon summon)
	{
		getTimers().addTimer("HEAL", 3000, null, summon.getOwner());
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, Npc npc, Player player)
	{
		if (player != null)
		{
			final Summon summon = player.getFirstServitor();
			if (event.equals("HEAL") && (summon != null) && Util.contains(TREE_OF_LIFE, summon.getId()))
			{
				summon.doCast(summon.getTemplate().getParameters().getSkillHolder("s_tree_heal").getSkill(), null, false, false);
				getTimers().addTimer("HEAL", 8000, null, player);
			}
		}
	}
	
	public static AbstractNpcAI provider()
	{
		return new TreeOfLife();
	}
}