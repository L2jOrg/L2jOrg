package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.ExShowCastleInfo;

import java.nio.ByteBuffer;

public class RequestAllCastleInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		client.getActiveChar().sendPacket(new ExShowCastleInfo());
	}
}