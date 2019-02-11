package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExReplyDominionInfo extends L2GameServerPacket
{
	public ExReplyDominionInfo()
	{
		//
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
	}
}