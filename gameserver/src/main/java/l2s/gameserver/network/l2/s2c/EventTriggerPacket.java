package l2s.gameserver.network.l2.s2c;

/**
 * @author SYS
 * @date 10/9/2007
 */
public class EventTriggerPacket extends L2GameServerPacket
{
	private final int _trapId;
	private final boolean _active;

	public EventTriggerPacket(int trapId, boolean active)
	{
		_trapId = trapId;
		_active = active;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_trapId); // trap object id
		writeC(_active ? 1 : 0); // trap activity 1 or 0
	}
}