package l2s.gameserver.network.l2.c2s;

public class RequestTeleport extends L2GameClientPacket
{
	private int unk, _type, unk2, unk3, unk4;

	@Override
	protected void readImpl()
	{
		unk = readD();
		_type = readD();
		if(_type == 2)
		{
			unk2 = readD();
			unk3 = readD();
		}
		else if(_type == 3)
		{
			unk2 = readD();
			unk3 = readD();
			unk4 = readD();
		}
	}

	@Override
	protected void runImpl()
	{
		//TODO not implemented
	}
}