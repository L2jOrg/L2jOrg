package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestPrivateStoreList extends L2GameClientPacket
{
	private int unk;

	/**
	 * format: d
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		unk = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}