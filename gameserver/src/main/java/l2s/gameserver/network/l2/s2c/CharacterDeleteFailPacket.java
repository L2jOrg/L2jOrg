package l2s.gameserver.network.l2.s2c;

public class CharacterDeleteFailPacket extends L2GameServerPacket
{
	public static int REASON_DELETION_FAILED = 0x01;
	public static int REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER = 0x02;
	public static int REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED = 0x03;
	int _error;

	public CharacterDeleteFailPacket(int error)
	{
		_error = error;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_error);
	}
}