package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExReceiveShowPostFriend;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 22:04/22.03.2011
 */
public class RequestExShowPostFriendListForPostBox extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{

	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new ExReceiveShowPostFriend(player));
	}
}
