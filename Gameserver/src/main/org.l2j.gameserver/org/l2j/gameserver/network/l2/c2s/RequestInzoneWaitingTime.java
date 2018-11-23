package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExInzoneWaitingInfo;

public class RequestInzoneWaitingTime extends L2GameClientPacket
{
	private boolean _openWindow;

	@Override
	protected void readImpl()
	{
		_openWindow = readByte() > 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExInzoneWaitingInfo(activeChar, _openWindow));
	}
}