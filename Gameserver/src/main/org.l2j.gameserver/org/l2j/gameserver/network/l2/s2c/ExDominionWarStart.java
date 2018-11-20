package org.l2j.gameserver.network.l2.s2c;

public class ExDominionWarStart extends L2GameServerPacket
{
	public ExDominionWarStart()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00); //territory Id
		writeInt(0x00);
		writeInt(0x00); //territory Id
	}
}
