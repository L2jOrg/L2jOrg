package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestCreatePledge extends L2GameClientPacket
{
	//Format: cS
	private String _pledgename;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_pledgename = readString(buffer, 64);
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}