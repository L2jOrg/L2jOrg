package org.l2j.gameserver.network.l2.s2c;

public class ExShowDominionRegistry extends L2GameServerPacket
{
	public ExShowDominionRegistry()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeInt(0x00);
		writeString("");
		writeString("");
		writeString("");
		writeInt(0x00); // Clan Request
		writeInt(0x00); // Merc Request
		writeInt(0x00); // War Time
		writeInt(0x00); // Current Time
		writeInt(0x00); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeInt(0x00); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeInt(0x01);
		writeInt(0x00); // Territory Count
	}
}