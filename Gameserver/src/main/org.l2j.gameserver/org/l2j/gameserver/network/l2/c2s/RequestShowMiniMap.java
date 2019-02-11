package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Zone;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ShowMinimapPacket;

import java.nio.ByteBuffer;

public class RequestShowMiniMap extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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