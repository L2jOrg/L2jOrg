package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExTodoListInzone extends L2GameServerPacket
{
	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		int instancesCount = 0;
		buffer.putShort((short) 0);
		for(int i = 0; i < instancesCount; i++)
		{
			buffer.put((byte)0x00);
			writeString("", buffer);
			writeString("", buffer);
			buffer.putShort((short) 0x00);
			buffer.putShort((short) 0x00);
			buffer.putShort((short) 0x00);
			buffer.putShort((short) 0x00);
			buffer.put((byte)0x00);
			buffer.put((byte)0x00);
		}
	}
}