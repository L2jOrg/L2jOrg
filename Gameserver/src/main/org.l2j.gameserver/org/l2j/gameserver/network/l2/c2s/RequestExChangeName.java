package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestExChangeName extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		int unk1 = buffer.getInt();
		String name = readString(buffer);
		int unk2 = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}