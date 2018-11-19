package l2s.gameserver.network.l2.s2c;

public class ExPledgeBonusUpdate extends L2GameServerPacket
{
	private final BonusType _type;
	private final int _value;

	public static enum BonusType
	{
		ATTENDANCE,
		HUNTING
	}

	public ExPledgeBonusUpdate(BonusType type, int value)
	{
		_type = type;
		_value = value;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_type.ordinal());
		writeD(_value);
	}
}