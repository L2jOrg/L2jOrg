package l2s.gameserver.network.l2.s2c;

public class TutorialEnableClientEventPacket extends L2GameServerPacket
{
	private int _event = 0;

	public TutorialEnableClientEventPacket(int event)
	{
		_event = event;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_event);
	}
}