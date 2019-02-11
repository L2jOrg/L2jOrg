package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class ReplyGameGuardQuery extends L2GameClientPacket
{
	// Format: cdddd
	/* CCP Guard START
	private byte _data[] = new byte[80];
	** CCP Guard END*/

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		/* CCP Guard START
		ccpGuard.Protection.doReadReplyGameGuard(client, _buf, _data);
		** CCP Guard END*/
	}

	@Override
	protected void runImpl()
	{
		/* CCP Guard START
		ccpGuard.Protection.doReplyGameGuard(client, _data);
		** CCP Guard END*/
	}
}