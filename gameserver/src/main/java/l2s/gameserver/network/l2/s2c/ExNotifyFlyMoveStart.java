package l2s.gameserver.network.l2.s2c;

public final class ExNotifyFlyMoveStart extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC = new ExNotifyFlyMoveStart();

	public ExNotifyFlyMoveStart()
	{
		//trigger
	}

	@Override
	protected void writeImpl()
	{
	}
}