package org.l2j.gameserver.network.l2.c2s;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.dao.CharacterPostFriendDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

import io.github.joealisson.primitive.pair.IntObjectPair;
import io.github.joealisson.primitive.maps.IntObjectMap;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 21:06/22.03.2011
 */
public class RequestExDeletePostFriendForPostBox extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		_name = readString(buffer);
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		if(Util.isNullOrEmpty(_name))
			return;

		int key = 0;
		IntObjectMap<String> postFriends = player.getPostFriends();
		for(IntObjectPair<String> entry : postFriends.entrySet())
		{
			if(entry.getValue().equalsIgnoreCase(_name))
				key = entry.getKey();
		}

		if(key == 0)
		{
			player.sendPacket(SystemMsg.THE_NAME_IS_NOT_CURRENTLY_REGISTERED);
			return;
		}

		player.getPostFriends().remove(key);

		CharacterPostFriendDAO.getInstance().delete(player, key);
		player.sendPacket(new SystemMessagePacket(SystemMsg.S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST).addString(_name));
	}
}
