package l2s.gameserver.network.l2.s2c;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeWaitingUser extends L2GameServerPacket
{
	private final int _charId;
	private final String _desc;

	public ExPledgeWaitingUser(int charId, String desc)
	{
		_charId = charId;
		_desc = desc;
	}

	protected void writeImpl()
	{
		writeD(_charId);
		writeS(_desc);
	}
}