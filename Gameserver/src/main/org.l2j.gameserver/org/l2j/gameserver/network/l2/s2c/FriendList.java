package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;

/**
 * @author Bonux
 */
public class FriendList extends L2GameServerPacket
{
	private Friend[] _friends;

	public FriendList(Player player)
	{
		_friends = player.getFriendList().values();
	}

	@Override
	protected void writeImpl()
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
		}
	}
}
