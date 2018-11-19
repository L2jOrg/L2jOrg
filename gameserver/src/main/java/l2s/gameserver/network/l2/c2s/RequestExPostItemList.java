package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.ExReplyPostItemList;

/**
 *  Нажатие на кнопку "send mail" в списке из {@link ExShowReceivedPostList}, запрос создания нового письма
 *  В ответ шлется {@link ExReplyPostItemList}
 */
public class RequestExPostItemList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//just a trigger
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
			activeChar.sendActionFailed();

		if(!Config.ALLOW_MAIL)
		{
			activeChar.sendMessage(new CustomMessage("mail.Disabled"));
			activeChar.sendActionFailed();
			return;
		}

		activeChar.sendPacket(new ExReplyPostItemList(activeChar));
	}
}