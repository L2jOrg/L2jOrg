package org.l2j.gameserver.network.l2.s2c;

public class ExTodoListInzone extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		int instancesCount = 0;
		writeShort(0);
		for(int i = 0; i < instancesCount; i++)
		{
			writeByte(0x00);
			writeString("");
			writeString("");
			writeShort(0x00);
			writeShort(0x00);
			writeShort(0x00);
			writeShort(0x00);
			writeByte(0x00);
			writeByte(0x00);
		}
	}
}