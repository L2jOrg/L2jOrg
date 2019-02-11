package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExGetBookMarkInfoPacket;

import java.nio.ByteBuffer;

public class RequestDeleteBookMarkSlot extends L2GameClientPacket
{
	private int slot;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		slot = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar != null)
		{
			//TODO Msg.THE_SAVED_TELEPORT_LOCATION_WILL_BE_DELETED_DO_YOU_WISH_TO_CONTINUE
			activeChar.getBookMarkList().remove(slot);
			activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));
		}
	}
}