package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import io.github.joealisson.primitive.maps.IntObjectMap;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 22:01/22.03.2011
 */
public class ExReceiveShowPostFriend extends L2GameServerPacket
{
	private IntObjectMap<String> _list;

	public ExReceiveShowPostFriend(Player player)
	{
		_list = player.getPostFriends();
	}

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_list.size());
		for(String t : _list.values())
			writeString(t, buffer);
	}
}
