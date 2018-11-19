package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.network.l2.s2c.ExReplyReceivedPost;
import l2s.gameserver.network.l2.s2c.ExShowReceivedPostList;

/**
 * Шлется клиентом как запрос на отказ принять письмо из {@link ExReplyReceivedPost}. Если к письму приложены вещи то их надо вернуть отправителю.
 */
public class RequestExRejectPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
			return;
		}

		if(activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		if(!activeChar.isInPeaceZone())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_IN_A_NONPEACE_ZONE_LOCATION);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if(activeChar.isInTrainingCamp())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_TAKE_OTHER_ACTION_WHILE_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			if(!mail.isReturnable())
			{
				activeChar.sendActionFailed();
				return;
			}

			int expireTime = 360 * 3600 + (int) (System.currentTimeMillis() / 1000L); //TODO [G1ta0] хардкод времени актуальности почты

			Mail reject = mail.reject();
			mail.delete();
			reject.setExpireTime(expireTime);
			reject.save();

			Player sender = World.getPlayer(reject.getReceiverId());
			if(sender != null)
				sender.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		}

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}