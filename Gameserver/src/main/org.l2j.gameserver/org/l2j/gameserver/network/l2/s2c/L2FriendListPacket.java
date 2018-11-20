package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;

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
		writeInt(_friends.length);
		for(Friend f : _friends)
		{
			writeInt(f.getObjectId());
			writeString(f.getName());
			writeInt(f.isOnline());
			writeInt(f.isOnline() ? f.getObjectId() : 0);
			writeInt(f.getLevel());
			writeInt(f.getClassId());
			writeString(f.getMemo());
		}
	}
}