package l2s.gameserver.network.l2.s2c;

public class ExWaitWaitingSubStituteInfo extends L2GameServerPacket
{
	public static final L2GameServerPacket OPEN = new ExWaitWaitingSubStituteInfo(true);
	public static final L2GameServerPacket CLOSE = new ExWaitWaitingSubStituteInfo(false);

	private boolean _open;

	public ExWaitWaitingSubStituteInfo(boolean open)
	{
		_open = open;
	}

	protected void writeImpl()
	{
		writeD(_open);
	}
}