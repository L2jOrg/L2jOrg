package org.l2j.gameserver.network.l2.c2s;

public class RequestExChangeName extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		int unk1 = readD();
		String name = readString();
		int unk2 = readD();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}