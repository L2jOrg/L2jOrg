package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//System.out.println("Unimplemented packet: " + getType() + " | size: " + buffer.remaining());
	}

	@Override
	protected void runImpl()
	{}
}