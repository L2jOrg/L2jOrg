package org.l2j.gameserver.network.l2.c2s;

public class RequestTimeCheck extends L2GameClientPacket
{
	private int unk, unk2;

	/**
	 * format: dd
	 */
	@Override
	protected void readImpl()
	{
		unk = readInt();
		unk2 = readInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}