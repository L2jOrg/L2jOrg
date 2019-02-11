package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExItemAuctionStatus extends L2GameServerPacket
{
	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) 0);
		buffer.putShort((short) 0);
		buffer.putShort((short) 0);
		buffer.putShort((short) 0);
		buffer.putShort((short) 0);
		buffer.putShort((short) 0);
		buffer.putInt(0);
		buffer.put((byte)0);
	}
}