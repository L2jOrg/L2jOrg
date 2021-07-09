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
package org.l2j.scripts.ai.areas.dungeonofabyss.tores;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * @author QuangNguyen
 * @author JoeAlisson
 */
public class Tores extends AbstractNpcAI {

	private static final int TORES_ID = 31778;

	private static final Location MAGRIT_LOCATION = new Location(-120325, -182444, -6752);
	private static final Location IRIS_LOCATION = new Location(-109202, -180546, -6751);

	private Tores() {
		addStartNpc(TORES_ID);
		addTalkId(TORES_ID);
		addFirstTalkId(TORES_ID);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "31778.htm";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		if ("1".equals(event)) {
			if ((player.getLevel() > 39) && (player.getLevel() < 45)) {
				player.teleToLocation(MAGRIT_LOCATION, true);
			} else {
				return "31778-no_level.htm";
			}
		} else if ("2".equals(event)) {
			if ((player.getLevel() > 44) && (player.getLevel() < 50)) {
				player.teleToLocation(IRIS_LOCATION, true);

			} else {
				return "31778-no_level01.htm";
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Tores();
	}
}