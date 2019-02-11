package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * format ch
 * c: (id) 0xD0
 * h: (subid) 0x2F
 */
public class RequestExOlympiadObserverEnd extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		// ignored
	}
}