package l2s.gameserver.network.l2.s2c;

/**
 *
 * @author monithly
 */
public class ExLightingCandleEvent extends L2GameServerPacket
{
	public static final L2GameServerPacket ENABLED = new ExLightingCandleEvent(1);
	public static final L2GameServerPacket DISABLED = new ExLightingCandleEvent(0);

	private final int _value;

	public ExLightingCandleEvent(int value)
	{
		_value = value;
	}

	@Override
	protected void writeImpl()
	{
		writeH(_value);	// Available
	}
}
