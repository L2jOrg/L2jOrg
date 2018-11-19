package l2s.gameserver.network.l2.s2c;

public class ExCuriousHouseRemainTime extends L2GameServerPacket
{
	private int _time;

	public ExCuriousHouseRemainTime(int time)
	{
		_time = time;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_time);
	}
}
