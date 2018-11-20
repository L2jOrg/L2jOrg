package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.actor.instances.player.Friend;

/**
 * @author Bonux
**/
public class FriendStatus extends L2GameServerPacket
{
	private final Friend _friend;
	private final boolean _login;

	public FriendStatus(Friend friend, boolean login)
	{
		_friend = friend;
		_login = login;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_login);
		writeS(_friend.getName());
		if(!_login)
			writeD(_friend.getObjectId());
	}
}
