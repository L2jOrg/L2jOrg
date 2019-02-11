package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_friends.length);
		for(Friend f : _friends)
		{
			buffer.putInt(f.getObjectId());
			writeString(f.getName(), buffer);
			buffer.putInt(f.isOnline() ? 0x01 : 0x00);
			buffer.putInt(f.isOnline() ? f.getObjectId() : 0);
			buffer.putInt(f.getLevel());
			buffer.putInt(f.getClassId());
			writeString(f.getMemo(), buffer);
		}
	}
}