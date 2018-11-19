package l2s.gameserver.network.l2.s2c;

public class ExChangeClientEffectInfo extends L2GameServerPacket
{
	private int _unk1;
	private int _unk2;
	private int _state;

	public ExChangeClientEffectInfo(int state)
	{
		_unk1 = 0;
		_unk2 = 0;
		_state = state;
	}

	public ExChangeClientEffectInfo(int unk1, int unk2, int state)
	{
		_unk1 = unk1;
		_unk2 = unk2;
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_unk1);
		writeD(_unk2);
		writeD(_state);
	}
}