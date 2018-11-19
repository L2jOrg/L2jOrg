package l2s.gameserver.network.l2.s2c;

public class ExShowDominionRegistry extends L2GameServerPacket
{
	public ExShowDominionRegistry()
	{
		//
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00);
		writeS("");
		writeS("");
		writeS("");
		writeD(0x00); // Clan Request
		writeD(0x00); // Merc Request
		writeD(0x00); // War Time
		writeD(0x00); // Current Time
		writeD(0x00); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(0x00); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
		writeD(0x01);
		writeD(0x00); // Territory Count
	}
}