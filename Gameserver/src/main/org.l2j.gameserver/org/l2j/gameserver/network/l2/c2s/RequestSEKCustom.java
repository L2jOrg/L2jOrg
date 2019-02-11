package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestSEKCustom extends L2GameClientPacket
{
	private int SlotNum, Direction;

	/**
	 * format: dd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		SlotNum = buffer.getInt();
		Direction = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}