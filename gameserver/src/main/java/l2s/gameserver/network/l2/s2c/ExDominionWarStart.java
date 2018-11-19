package l2s.gameserver.network.l2.s2c;

public class ExDominionWarStart extends L2GameServerPacket
{
	public ExDominionWarStart()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);
		writeD(0x00);
		writeD(0x00); //territory Id
		writeD(0x00);
		writeD(0x00); //territory Id
	}
}
