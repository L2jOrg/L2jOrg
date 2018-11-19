package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.handler.bbs.BbsHandlerHolder;
import l2s.gameserver.handler.bbs.IBbsHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public class RequestShowBoard extends L2GameClientPacket
{
	private int _unknown;

	@Override
	public void readImpl()
	{
		_unknown = readD();
	}

	@Override
	public void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(Config.BBS_ENABLED)
		{
			IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler(Config.BBS_DEFAULT_PAGE);
			if(handler != null)
				handler.onBypassCommand(activeChar, Config.BBS_DEFAULT_PAGE);
		}
		else
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
	}
}