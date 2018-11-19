package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ShowMinimapPacket;

public class RequestShowMiniMap extends L2GameClientPacket
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

		if(activeChar.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP))
		{
			activeChar.sendPacket(SystemMsg.THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP);
			return;
		}

		sendPacket(new ShowMinimapPacket(activeChar, 0));
	}
}