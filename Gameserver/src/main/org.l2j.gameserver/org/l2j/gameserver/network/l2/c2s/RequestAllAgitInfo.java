package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.ExShowAgitInfo;

import java.nio.ByteBuffer;

public class RequestAllAgitInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		client.getActiveChar().sendPacket(new ExShowAgitInfo());
	}
}