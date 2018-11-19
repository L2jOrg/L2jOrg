package l2s.gameserver.network.l2.s2c;

public class ExRedSkyPacket extends L2GameServerPacket
{
	private int _duration;

	public ExRedSkyPacket(int duration)
	{
		_duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_duration);
	}
}