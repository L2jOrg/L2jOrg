package org.l2j.gameserver.network.l2.c2s;

public class RequestExJoinDominionWar extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		readD();
		readD();
		readD();
	}

	@Override
	protected void runImpl()
	{
		//
	}
}