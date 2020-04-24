package org.l2j.gameserver.network.serverpackets.sessionzones;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class TimedHuntingZoneEnter extends ServerPacket
{
	private final int _remainingTime;
	
	public TimedHuntingZoneEnter(int remainingTime)
	{
		_remainingTime = remainingTime;
	}

	@Override
	protected void writeImpl(GameClient client)  {
		writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_USER_ENTER);
		writeInt(_remainingTime);
	}
}