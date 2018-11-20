package org.l2j.gameserver.network.l2.s2c;

public class ExPartyMatchingRoomHistory extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeInt(0);
	}
}