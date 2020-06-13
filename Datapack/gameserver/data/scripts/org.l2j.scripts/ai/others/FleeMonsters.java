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
package ai.others;

import ai.AbstractNpcAI;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;

import static org.l2j.gameserver.util.MathUtil.calculateAngleFrom;

/**
 * Flee Monsters AI.
 * @author Pandragon, NosBit
 */
public final class FleeMonsters extends AbstractNpcAI
{
	// NPCs
	private static final int[] MOBS =
	{
		20002, // Rabbit
		20432, // Elpy
	};
	// Misc
	private static final int FLEE_DISTANCE = 500;
	
	private FleeMonsters()
	{
		addAttackId(MOBS);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		npc.disableCoreAI(true);
		npc.setRunning();
		
		final Summon summon = isSummon ? attacker.getServitors().values().stream().findFirst().orElse(attacker.getPet()) : null;
		final ILocational attackerLoc = summon == null ? attacker : summon;
		final double radians = Math.toRadians(calculateAngleFrom(attackerLoc, npc));
		final int posX = (int) (npc.getX() + (FLEE_DISTANCE * Math.cos(radians)));
		final int posY = (int) (npc.getY() + (FLEE_DISTANCE * Math.sin(radians)));
		final int posZ = npc.getZ();
		
		final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ, attacker.getInstanceWorld());
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static AbstractNpcAI provider()
	{
		return new FleeMonsters();
	}
}
