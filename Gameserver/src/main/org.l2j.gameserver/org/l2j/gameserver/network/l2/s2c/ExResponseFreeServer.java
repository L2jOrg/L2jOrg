package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExResponseFreeServer extends L2GameServerPacket
{
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		// just trigger
	}
}