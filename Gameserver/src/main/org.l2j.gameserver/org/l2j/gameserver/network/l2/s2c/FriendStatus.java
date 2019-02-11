package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.actor.instances.player.Friend;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_login ? 1 : 0);
		writeString(_friend.getName(), buffer);
		if(!_login)
			buffer.putInt(_friend.getObjectId());
	}
}
