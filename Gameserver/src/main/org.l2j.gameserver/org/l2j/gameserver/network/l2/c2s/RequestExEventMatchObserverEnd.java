package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestExEventMatchObserverEnd extends L2GameClientPacket
{
	private int unk, unk2;

	/**
	 * format: dd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		unk = buffer.getInt();
		unk2 = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}