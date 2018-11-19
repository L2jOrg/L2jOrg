package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Friend;

/**
 * @author Bonux
 */
public class L2FriendListPacket extends L2GameServerPacket
{
	private Friend[] _friends;

	public L2FriendListPacket(Player player)
	{
		_friends = player.getFriendList().values();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_friends.length);
		for(Friend f : _friends)
		{
			writeD(f.getObjectId());
			writeS(f.getName());
			writeD(f.isOnline());
			writeD(f.isOnline() ? f.getObjectId() : 0);
			writeD(f.getLevel());
			writeD(f.getClassId());
			writeS(f.getMemo());
		}
	}
}