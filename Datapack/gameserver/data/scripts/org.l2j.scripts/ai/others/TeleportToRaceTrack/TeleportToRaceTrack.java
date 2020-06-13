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
package ai.others.TeleportToRaceTrack;

import ai.AbstractNpcAI;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Monster Derby Track teleport AI.
 * @author Plim
 * @author Mobius
 */
public final class TeleportToRaceTrack extends AbstractNpcAI
{
	// NPC
	private static final int RACE_MANAGER = 30995;
	// Locations
	private static final Location RACE_TRACK_TELEPORT = new Location(12661, 181687, -3540);
	private static final IntMap<Location> TELEPORTER_LOCATIONS = new HashIntMap<>();
	static {
		TELEPORTER_LOCATIONS.put(30320, new Location(-80826, 149775, -3043)); // Richlin
		TELEPORTER_LOCATIONS.put(30256, new Location(-12672, 122776, -3116)); // Bella
		TELEPORTER_LOCATIONS.put(30059, new Location(15670, 142983, -2705)); // Trisha
		TELEPORTER_LOCATIONS.put(30080, new Location(83400, 147943, -3404)); // Clarissa
		TELEPORTER_LOCATIONS.put(30899, new Location(111409, 219364, -3545)); // Flauen
		TELEPORTER_LOCATIONS.put(30177, new Location(82956, 53162, -1495)); // Valentina
		TELEPORTER_LOCATIONS.put(30848, new Location(146331, 25762, -2018)); // Elisa
		TELEPORTER_LOCATIONS.put(30233, new Location(116819, 76994, -2714)); // Esmeralda
		TELEPORTER_LOCATIONS.put(31275, new Location(147930, -55281, -2728)); // Tatiana
		TELEPORTER_LOCATIONS.put(31210, new Location(12882, 181053, -3560)); // Race Track Gatekeeper
	}

	// Player Variables
	private static final String MONSTER_RETURN = "MONSTER_RETURN";
	
	private TeleportToRaceTrack() {
		var teleporters =  TELEPORTER_LOCATIONS.keySet().toArray();
		addStartNpc(RACE_MANAGER);
		addStartNpc(teleporters);
		addTalkId(RACE_MANAGER);
		addTalkId(teleporters);
	}
	
	@Override
	public String onTalk(Npc npc, Player player) {
		if (npc.getId() == RACE_MANAGER) {
			final int returnId = player.getVariables().getInt(MONSTER_RETURN, -1);
			if (returnId > 30000) {
				player.teleToLocation(TELEPORTER_LOCATIONS.get(returnId));
				player.getVariables().remove(MONSTER_RETURN);
			} else {
				player.teleToLocation(TELEPORTER_LOCATIONS.get(30059)); // Dion
			}
		}
		else {
			player.teleToLocation(RACE_TRACK_TELEPORT);
			player.getVariables().set(MONSTER_RETURN, npc.getId());
		}
		return super.onTalk(npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new TeleportToRaceTrack();
	}
}
