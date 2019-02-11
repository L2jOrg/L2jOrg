package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExNoticePostArrived;
import org.l2j.gameserver.network.l2.s2c.ExShowReceivedPostList;

import java.nio.ByteBuffer;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем.
 * В ответ шлется {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket
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
			cha.sendPacket(new ExShowReceivedPostList(cha));
	}
}