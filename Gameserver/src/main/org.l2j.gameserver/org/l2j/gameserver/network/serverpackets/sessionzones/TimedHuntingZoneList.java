/*
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
package org.l2j.gameserver.network.serverpackets.sessionzones;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class TimedHuntingZoneList extends ServerPacket {

	private final Player _player;
	private final boolean _isInTimedHuntingZone;

	public TimedHuntingZoneList(Player player) {
		_player = player;
		_isInTimedHuntingZone = player.isInTimedHuntingZone();
	}

	@Override
	protected void writeImpl(GameClient client, WritableBuffer buffer) {
		writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST, buffer );

		final long currentTime = System.currentTimeMillis();
		long endTime;
		buffer.writeInt(1); // zone count

		// Ancient Pirates' Tomb
		buffer.writeInt(1); // required item count
		buffer.writeInt(57); // item id
		buffer.writeLong(Config.TIME_LIMITED_ZONE_TELEPORT_FEE); // item count
		buffer.writeInt(1); // reset cycle
		buffer.writeInt(2); // zone id
		buffer.writeInt(78); // min level
		buffer.writeInt(999); // max level
		buffer.writeInt(0); // remain time base?
		endTime = _player.getHuntingZoneResetTime(2);
		if ((endTime + Config.TIME_LIMITED_ZONE_RESET_DELAY) < currentTime) {
			endTime = currentTime + 3600000;
		}
		buffer.writeInt((int) (Math.max(endTime - currentTime, 0)) / 1000); // remain time
		buffer.writeInt((int) (Config.TIME_LIMITED_MAX_ADDED_TIME / 1000));
		buffer.writeInt(3600); // remain refill time
		buffer.writeInt(3600); // refill time max
		buffer.writeByte(_isInTimedHuntingZone ? 0 : 1); // field activated
		buffer.writeByte(false); // bUserBound
		buffer.writeByte(true); // bCanReEnter
	}
}