package org.l2j.gameserver.network.l2.c2s;

public class RequestExChangeName extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		int unk1 = readInt();
		String name = readString();
		int unk2 = readInt();
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}