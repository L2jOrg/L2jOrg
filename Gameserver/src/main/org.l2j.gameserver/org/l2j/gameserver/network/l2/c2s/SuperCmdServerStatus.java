package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

/**
 * Format ch
 * c: (id) 0x39
 * h: (subid) 0x02
 */
class SuperCmdServerStatus extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{}
}