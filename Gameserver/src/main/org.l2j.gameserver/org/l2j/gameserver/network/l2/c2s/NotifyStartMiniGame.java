package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class NotifyStartMiniGame extends L2GameClientPacket
{
	@Override
	protected void runImpl()
	{
		// just trigger
	}

	@Override
	protected void readImpl(ByteBuffer buffer)
	{}
}