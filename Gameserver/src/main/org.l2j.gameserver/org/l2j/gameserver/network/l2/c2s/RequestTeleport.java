package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestTeleport extends L2GameClientPacket
{
	private int unk, _type, unk2, unk3, unk4;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		unk = buffer.getInt();
		_type = buffer.getInt();
		if(_type == 2)
		{
			unk2 = buffer.getInt();
			unk3 = buffer.getInt();
		}
		else if(_type == 3)
		{
			unk2 = buffer.getInt();
			unk3 = buffer.getInt();
			unk4 = buffer.getInt();
		}
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}