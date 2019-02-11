package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.SendStatus;

import java.nio.ByteBuffer;

public final class RequestStatus extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		client.close(new SendStatus());
	}
}