package org.l2j.gameserver.network.l2.s2c;

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
		writeInt(1); //UNK
		writeString(_friendName); //FriendName
	}
}
