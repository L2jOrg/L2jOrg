package org.l2j.gameserver.network.l2.c2s;

public class RequestSEKCustom extends L2GameClientPacket
{
	private int SlotNum, Direction;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		SlotNum = readInt();
		Direction = readInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}