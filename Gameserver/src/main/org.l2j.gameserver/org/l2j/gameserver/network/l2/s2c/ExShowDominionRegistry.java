package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExShowDominionRegistry extends L2GameServerPacket
{
	public ExShowDominionRegistry()
	{
		//
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);
		writeString("", buffer);
		writeString("", buffer);
		writeString("", buffer);
		buffer.putInt(0x00); // Clan Request
		buffer.putInt(0x00); // Merc Request
		buffer.putInt(0x00); // War Time
		buffer.putInt(0x00); // Current Time
		buffer.putInt(0x00); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		buffer.putInt(0x00); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		buffer.putInt(0x01);
		buffer.putInt(0x00); // Territory Count
	}
}