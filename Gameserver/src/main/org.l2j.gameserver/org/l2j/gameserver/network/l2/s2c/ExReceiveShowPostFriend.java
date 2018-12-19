package org.l2j.gameserver.network.l2.s2c;

import gnu.trove.map.TIntObjectMap;
import org.l2j.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 22:01/22.03.2011
 */
public class ExReceiveShowPostFriend extends L2GameServerPacket
{
	private TIntObjectMap<String> _list;

	public ExReceiveShowPostFriend(Player player)
	{
		_list = player.getPostFriends();
	}

	@Override
	public void writeImpl()
	{
		writeInt(_list.size());
		for(String t : _list.valueCollection())
			writeString(t);
	}
}
