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
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.serverpackets.ExCastleState;
import org.l2j.gameserver.world.zone.Zone;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Shows castle side effect in cities.
 * @author Gigi
 *
 */
public class CastleSideEffect extends AbstractNpcAI
{
	private static final int[] ZONE_ID =
	{
		11020, // Giran
		11027, // Gludio
		11028, // Dion
		11029, // Oren
		11031, // aden
		11032, // Goddard
		11033, // Rune
		11034, // Heine
		11035, // Shuttgard
	};
	
	public CastleSideEffect()
	{
		addEnterZoneId(ZONE_ID);
	}
	
	@Override
	public String onEnterZone(Creature character, Zone zone)
	{
		if (isPlayer(character))
		{
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				character.sendPacket(new ExCastleState(castle));
			}
		}
		return super.onEnterZone(character, zone);
	}
	
	public static void main(String[] args)
	{
		new CastleSideEffect();
	}
}