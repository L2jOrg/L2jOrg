package l2s.gameserver.network.l2.s2c;

//пропадает почти весь интерфейс и пооявляется кнопка отказ
//связан с пакетом RequestLeaveCuriousHouse
public class ExCuriousHouseLeave extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExCuriousHouseLeave();

	private ExCuriousHouseLeave()
	{
		//TRIGGER
	}

	@Override
	protected void writeImpl()
	{
	}
}