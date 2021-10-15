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
package org.l2j.scripts.ai.others.Spawns;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class NoRandomActivity extends AbstractNpcAI
{
	private NoRandomActivity()
	{
	}
	
	@Override
	public void onSpawnNpc(SpawnTemplate template, SpawnGroup group, Npc npc)
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
