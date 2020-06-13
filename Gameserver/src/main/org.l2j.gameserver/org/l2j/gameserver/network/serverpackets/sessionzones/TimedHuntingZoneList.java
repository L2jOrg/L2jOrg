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
package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.variables.PlayerVariables;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mobius
 */
public class TimedHuntingZoneList extends ServerPacket {
	private static Logger LOGGER = LoggerFactory.getLogger(TimedHuntingZoneList.class);
	private final Player _player;
	private final boolean _isInTimedHuntingZone;

	public TimedHuntingZoneList(Player player) {
		_player = player;
		_isInTimedHuntingZone = player.isInTimedHuntingZone();
	}

	@Override
	protected void writeImpl(GameClient client) {
		{
			writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST);

			final long currentTime = System.currentTimeMillis();
			long endTime;
			writeInt(1); // zone count

			// Ancient Pirates' Tomb
			writeInt(1); // required item count
			writeInt(57); // item id
			writeLong(Config.TIME_LIMITED_ZONE_TELEPORT_FEE); // item count
			writeInt(1); // reset cycle
			writeInt(2); // zone id
			writeInt(78); // min level
			writeInt(999); // max level
			writeInt(0); // remain time base?
			endTime = _player.getVariables().getLong(PlayerVariables.HUNTING_ZONE_RESET_TIME + 2, 0);
			if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime) {
				endTime = currentTime + 3600000;
			}
			writeInt((int) (Math.max(endTime - currentTime, 0)) / 1000); // remain time
			writeInt((int) (Config.TIME_LIMITED_MAX_ADDED_TIME / 1000));
			writeInt(3600); // remain refill time
			writeInt(3600); // refill time max
			writeByte(_isInTimedHuntingZone ? 0 : 1); // field activated
			writeByte(false); // bUserBound
			writeByte(true); // bCanReEnter
		}
	}
}