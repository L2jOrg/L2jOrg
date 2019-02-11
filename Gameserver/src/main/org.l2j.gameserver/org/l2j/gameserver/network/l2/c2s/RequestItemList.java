package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.StatusUpdatePacket;

import java.nio.ByteBuffer;

public class RequestItemList extends L2GameClientPacket
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

		if(!activeChar.getPlayerAccess().UseInventory || activeChar.isBlocked())
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendItemList(true);
		activeChar.sendStatusUpdate(false, false, StatusUpdatePacket.CUR_LOAD);
	}
}