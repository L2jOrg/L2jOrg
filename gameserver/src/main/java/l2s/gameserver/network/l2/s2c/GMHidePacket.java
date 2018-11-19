package l2s.gameserver.network.l2.s2c;

public class GMHidePacket extends L2GameServerPacket
{
	private final int obj_id;

	public GMHidePacket(int id)
	{
		obj_id = id; //TODO хз чей id должен посылатся, нужно эксперементировать
	}

	@Override
	protected void writeImpl()
	{
		writeD(obj_id);
	}
}