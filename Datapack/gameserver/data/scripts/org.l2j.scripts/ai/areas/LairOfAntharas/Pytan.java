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
package ai.areas.LairOfAntharas;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Pytan, Knorkiks AI.
 * @author Quangnguyen
 */
public final class Pytan extends AbstractNpcAI
{
	// NPCs
	private static final int PYTAN = 20761;
	private static final int KNORIKS = 20405;
	
	private Pytan()
	{
		addKillId(PYTAN);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (getRandom(100) < 5)
		{
			final L2Npc spawnBanshee = addSpawn(KNORIKS, npc, false, 300000);
			final L2Playable attacker = isSummon ? killer.getServitors().values().stream().findFirst().orElse(killer.getPet()) : killer;
			addAttackPlayerDesire(spawnBanshee, attacker);
			npc.deleteMe();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Pytan();
	}
}