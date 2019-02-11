package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestExJoinDominionWar extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		buffer.getInt();
		buffer.getInt();
		buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//
	}
}