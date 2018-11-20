package org.l2j.gameserver.network.l2.s2c;

public class ExTutorialList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeString("");
		writeInt(0x00);
		writeInt(0x00);
		writeInt(0x00);
	}
}
