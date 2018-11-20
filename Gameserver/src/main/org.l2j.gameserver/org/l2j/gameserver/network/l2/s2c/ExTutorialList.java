package org.l2j.gameserver.network.l2.s2c;

public class ExTutorialList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeS("");
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
	}
}
