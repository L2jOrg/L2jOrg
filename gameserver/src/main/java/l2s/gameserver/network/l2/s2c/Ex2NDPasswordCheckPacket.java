package l2s.gameserver.network.l2.s2c;

/**
 * Format (ch)dd
 * d: window type
 * d: ban user (1)
 */
public class Ex2NDPasswordCheckPacket extends L2GameServerPacket
{
	public static final int PASSWORD_NEW = 0x00;
	public static final int PASSWORD_PROMPT = 0x01;
	public static final int PASSWORD_OK = 0x02;

	private int _windowType;

	public Ex2NDPasswordCheckPacket(int windowType)
	{
		_windowType = windowType;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_windowType);
		writeD(0x00);
	}
}
