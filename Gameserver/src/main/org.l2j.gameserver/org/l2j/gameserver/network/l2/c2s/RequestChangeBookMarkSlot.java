package org.l2j.gameserver.network.l2.c2s;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	protected void readImpl()
	{
		slot_old = readInt();
		slot_new = readInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}