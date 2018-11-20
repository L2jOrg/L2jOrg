package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Friend;
import org.l2j.gameserver.network.l2.s2c.ExFriendDetailInfo;

/**
 * @author Bonux
**/
public class RequestFriendDetailInfo extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		_name = readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Friend friend = activeChar.getFriendList().get(_name);
		if(friend == null)
			return;

		activeChar.sendPacket(new ExFriendDetailInfo(activeChar, friend));
	}
}