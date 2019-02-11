package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExDominionWarStart extends L2GameServerPacket
{
	public ExDominionWarStart()
	{
		//
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
		buffer.putInt(0x00);
		buffer.putInt(0x00); //territory Id
		buffer.putInt(0x00);
		buffer.putInt(0x00); //territory Id
	}
}
