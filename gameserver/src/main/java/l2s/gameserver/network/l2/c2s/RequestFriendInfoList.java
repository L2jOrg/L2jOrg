package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.L2FriendListPacket;

/**
 * @author Bonux
**/
public class RequestFriendInfoList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new L2FriendListPacket(activeChar));
	}
}