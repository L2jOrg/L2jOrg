package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExRaidServerInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)0x00);
		buffer.put((byte)0x00);
	}
}
