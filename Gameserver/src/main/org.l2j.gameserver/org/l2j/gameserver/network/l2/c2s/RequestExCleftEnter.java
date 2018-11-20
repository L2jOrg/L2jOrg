package org.l2j.gameserver.network.l2.c2s;

public class RequestExCleftEnter extends L2GameClientPacket
{
	private int unk;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		unk = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}