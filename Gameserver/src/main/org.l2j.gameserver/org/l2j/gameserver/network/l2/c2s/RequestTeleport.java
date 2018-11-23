package org.l2j.gameserver.network.l2.c2s;

public class RequestTeleport extends L2GameClientPacket
{
	private int unk, _type, unk2, unk3, unk4;

	@Override
	protected void readImpl()
	{
		unk = readInt();
		_type = readInt();
		if(_type == 2)
		{
			unk2 = readInt();
			unk3 = readInt();
		}
		else if(_type == 3)
		{
			unk2 = readInt();
			unk3 = readInt();
			unk4 = readInt();
		}
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}