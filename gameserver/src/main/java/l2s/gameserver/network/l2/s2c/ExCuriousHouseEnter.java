package l2s.gameserver.network.l2.s2c;

//пир отправке этого пакета на экране появляется иконка получения письма
public class ExCuriousHouseEnter extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExCuriousHouseEnter();

	public void ExCuriousHouseEnter()
	{
		//TRIGGER
	}

	@Override
	protected void writeImpl()
	{
	}
}
