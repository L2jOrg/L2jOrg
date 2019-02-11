package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExGetBookMarkInfoPacket;

import java.nio.ByteBuffer;

public class RequestBookMarkSlotInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//just trigger
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));
	}
}