package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.RecipeBookItemListPacket;

import java.nio.ByteBuffer;

public class RequestRecipeBookOpen extends L2GameClientPacket
{
	private boolean isDwarvenCraft;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		if(buffer.remaining() >= 4)
			isDwarvenCraft = buffer.getInt() == 0;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		sendPacket(new RecipeBookItemListPacket(activeChar, isDwarvenCraft));
	}
}