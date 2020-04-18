package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class TimedHuntingZoneExit extends ServerPacket {
	public static final TimedHuntingZoneExit STATIC_PACKET = new TimedHuntingZoneExit();

	public TimedHuntingZoneExit() {
	}

	@Override
	protected void writeImpl(GameClient client) {
		{
			writeId(ServerPacketId.EX_TIME_RESTRICT_FIELD_USER_EXIT);
		}
	}
}