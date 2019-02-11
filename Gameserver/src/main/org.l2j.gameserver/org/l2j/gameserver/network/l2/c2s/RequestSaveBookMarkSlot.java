package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExGetBookMarkInfoPacket;

import java.nio.ByteBuffer;

/**
 * SdS
 */
public class RequestSaveBookMarkSlot extends L2GameClientPacket
{
	private String name, acronym;
	private int icon;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		name = readString(buffer, 32);
		icon = buffer.getInt();
		acronym = readString(buffer, 4);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar != null && activeChar.getBookMarkList().add(name, acronym, icon))
			activeChar.sendPacket(new ExGetBookMarkInfoPacket(activeChar));
	}
}