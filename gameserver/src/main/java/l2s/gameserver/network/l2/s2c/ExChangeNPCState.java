package l2s.gameserver.network.l2.s2c;

public class ExChangeNPCState extends L2GameServerPacket
{
	private int _objId;
	private int _state;

	public ExChangeNPCState(int objId, int state)
	{
		_objId = objId;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_objId);
		writeD(_state);
	}
}
