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
package org.l2j.gameserver.network.serverpackets.timedzone;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class TimeRestrictFieldUserEnter extends ServerPacket {
	private final int remainingTime;
	private final int zoneId;
	private final int enterTimestamp;

	public TimeRestrictFieldUserEnter(int zoneId, int remainingTime) {
		this.zoneId = zoneId;
		this.remainingTime = remainingTime;
		this.enterTimestamp =  (int) (System.currentTimeMillis() / 1000);
	}

	@Override
	protected void writeImpl(GameClient client, WritableBuffer buffer)  {
		writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_USER_ENTER, buffer );
		buffer.writeByte(true); // success ?
		buffer.writeInt(zoneId);
		buffer.writeInt(enterTimestamp);
		buffer.writeInt(remainingTime);
	}
}