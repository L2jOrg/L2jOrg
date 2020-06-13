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
package ai.areas.DwarvenVillage.Toma;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Mobius
 */
public class Toma extends AbstractNpcAI
{
	// NPC
	private static final int TOMA = 30556;
	// Locations
	private static final Location[] LOCATIONS =
	{
		new Location(151680, -174891, -1782),
		new Location(154153, -220105, -3402),
		new Location(178834, -184336, -355, 41400)
	};
	// Misc
	private static final int TELEPORT_DELAY = 1800000; // 30 minutes
	
	private Toma()
	{
		addFirstTalkId(TOMA);
		onAdvEvent("RESPAWN_TOMA", null, null);
		startQuestTimer("RESPAWN_TOMA", TELEPORT_DELAY, null, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equals("RESPAWN_TOMA"))
		{
			addSpawn(TOMA, getRandomEntry(LOCATIONS), false, TELEPORT_DELAY);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "30556.htm";
	}
	
	public static AbstractNpcAI provider()
	{
		return new Toma();
	}
}
