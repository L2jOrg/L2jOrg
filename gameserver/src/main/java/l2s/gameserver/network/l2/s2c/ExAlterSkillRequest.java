package l2s.gameserver.network.l2.s2c;

/**
 * @autor Monithly
 */
public class ExAlterSkillRequest extends L2GameServerPacket
{
	private final int _activeId, _requestId, _duration;

	public ExAlterSkillRequest(int requestId, int activeId, int duration)
	{
		_requestId = requestId;
		_activeId = activeId;
		_duration = duration;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_requestId);
		writeD(_activeId);
		writeD(_duration);
	}
}
