/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.others;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.L2Playable;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * Fairy Trees AI.
 * @author Charus
 */
public class FairyTrees extends AbstractNpcAI
{
	// NPC
	private static final int SOUL_GUARDIAN = 27189; // Soul of Tree Guardian
	
	private static final int[] MOBS =
	{
		27185, // Fairy Tree of Wind
		27186, // Fairy Tree of Star
		27187, // Fairy Tree of Twilight
		27188, // Fairy Tree of Abyss
	};
	
	// Skill
	private static SkillHolder VENOMOUS_POISON = new SkillHolder(4243, 1); // Venomous Poison
	
	// Misc
	private static final int MIN_DISTANCE = 1500;
	
	private FairyTrees()
	{
		addKillId(MOBS);
		addSpawnId(MOBS);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.calculateDistance3D(killer) <= MIN_DISTANCE)
		{
			for (int i = 0; i < 20; i++)
			{
				final L2Npc guardian = addSpawn(SOUL_GUARDIAN, npc, false, 30000);
				final L2Playable attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
				addAttackPlayerDesire(guardian, attacker);
				if (getRandomBoolean())
				{
					guardian.setTarget(attacker);
					guardian.doCast(VENOMOUS_POISON.getSkill());
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.setRandomWalking(false);
		npc.setIsImmobilized(true);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new FairyTrees();
	}
}