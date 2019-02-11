package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		slot_old = buffer.getInt();
		slot_new = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}