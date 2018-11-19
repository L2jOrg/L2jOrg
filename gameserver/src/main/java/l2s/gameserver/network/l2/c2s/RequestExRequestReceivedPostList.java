package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExShowReceivedPostList;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от {@link ExNoticePostArrived}, запрос входящих писем.
 * В ответ шлется {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//just a trigger
	}

	@Override
	protected void runImpl()
	{
		Player cha = getClient().getActiveChar();
		if(cha != null)
			cha.sendPacket(new ExShowReceivedPostList(cha));
	}
}