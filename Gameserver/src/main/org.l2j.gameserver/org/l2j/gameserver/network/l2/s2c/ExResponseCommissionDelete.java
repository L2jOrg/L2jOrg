package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExResponseCommissionDelete extends L2GameServerPacket
{
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
		buffer.putInt(0x00);
		buffer.putLong(0x00);
	}
}
