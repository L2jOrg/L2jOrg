package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class NotifyExitBeautyshop extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{
		//
	}

	@Override
	protected void runImpl() throws Exception
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.unblock();
		activeChar.broadcastCharInfo();
	}
}
