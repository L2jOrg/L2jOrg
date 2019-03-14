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
package ai.others.Spawns;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;

import ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class NoRandomActivity extends AbstractNpcAI
{
	private NoRandomActivity()
	{
	}
	
	@Override
	public void onSpawnNpc(SpawnTemplate template, SpawnGroup group, L2Npc npc)
	{
		npc.setRandomAnimation(npc.getParameters().getBoolean("disableRandomAnimation", false));
		npc.setRandomWalking(npc.getParameters().getBoolean("disableRandomWalk", false));
		if (npc.getSpawn() != null)
		{
			npc.getSpawn().setRandomWalking(!npc.getParameters().getBoolean("disableRandomWalk", false));
		}
	}
	
	public static AbstractNpcAI provider()
	{
		return new NoRandomActivity();
	}
}
