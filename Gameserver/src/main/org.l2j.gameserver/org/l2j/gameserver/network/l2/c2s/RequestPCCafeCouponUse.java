package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * format: chS
 */
public class RequestPCCafeCouponUse extends L2GameClientPacket
{
	// format: (ch)S
	private String _unknown;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_unknown = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}