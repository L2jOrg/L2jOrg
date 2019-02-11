package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExClosePartyRoomPacket extends L2GameServerPacket
{
	public static L2GameServerPacket STATIC = new ExClosePartyRoomPacket();

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
	}
}