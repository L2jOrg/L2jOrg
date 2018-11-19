package l2s.gameserver.network.l2.s2c;

/**
 * @author Bonux
**/
public class FriendRemove extends L2GameServerPacket
{
	private final String _friendName;

	public FriendRemove(String name)
	{
		_friendName = name;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(1); //UNK
		writeS(_friendName); //FriendName
	}
}
