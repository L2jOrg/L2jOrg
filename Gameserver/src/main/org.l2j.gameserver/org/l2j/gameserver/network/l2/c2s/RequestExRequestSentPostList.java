package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExShowSentPostList;

import java.nio.ByteBuffer;

/**
 * Нажатие на кнопку "sent mail",запрос списка исходящих писем.
 * В ответ шлется {@link ExShowSentPostList}
 */
public class RequestExRequestSentPostList extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//just a trigger
	}

	@Override
	protected void runImpl()
	{
		Player cha = client.getActiveChar();
		if(cha != null)
			cha.sendPacket(new ExShowSentPostList(cha));
	}
}