package l2s.gameserver.network.l2.s2c;

public class Ex2NDPasswordVerifyPacket extends L2GameServerPacket
{
	public static final int PASSWORD_OK = 0x00;
	public static final int PASSWORD_WRONG = 0x01;
	public static final int PASSWORD_BAN = 0x02;

	private int _wrongTentatives, _mode;

	public Ex2NDPasswordVerifyPacket(int mode, int wrongTentatives)
	{
		_mode = mode;
		_wrongTentatives = wrongTentatives;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_mode);
		writeD(_wrongTentatives);
	}
}
