package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Friend;
import l2s.gameserver.network.l2.s2c.ExFriendDetailInfo;

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