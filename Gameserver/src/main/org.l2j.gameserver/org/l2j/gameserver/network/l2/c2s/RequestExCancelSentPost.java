package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;
import java.util.Set;

import org.l2j.commons.math.SafeMath;
import org.l2j.gameserver.dao.MailDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExReplySentPost;
import org.l2j.gameserver.network.l2.s2c.ExShowSentPostList;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.utils.Log;

/**
 * Запрос на удаление письма с приложениями. Возвращает приложения отправителю на личный склад и удаляет письмо. Ответ на кнопку Cancel в {@link ExReplySentPost}.
 */
public class RequestExCancelSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		postId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
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

		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			if(mail.getAttachments().isEmpty())
			{
				activeChar.sendActionFailed();
				return;
			}
			activeChar.getInventory().writeLock();
			try
			{
				int slots = 0;
				long weight = 0;
				for(ItemInstance item : mail.getAttachments())
				{
					weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(item.getCount(), item.getTemplate().getWeight()));
					if(!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
						slots++;
				}

				if(!activeChar.getInventory().validateWeight(weight))
				{
					activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
					return;
				}

				if(!activeChar.getInventory().validateCapacity(slots))
				{
					activeChar.sendPacket(SystemMsg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
					return;
				}

				ItemInstance[] items;
				Set<ItemInstance> attachments = mail.getAttachments();

				synchronized (attachments)
				{
					items = mail.getAttachments().toArray(new ItemInstance[attachments.size()]);
					attachments.clear();
				}

				for(ItemInstance item : items)
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ACQUIRED_S2_S1).addItemName(item.getItemId()).addLong(item.getCount()));
					Log.LogItem(activeChar, Log.PostCancel, item);
					activeChar.getInventory().addItem(item);
				}

				mail.delete();

				activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_CANCELLED);
			}
			catch(ArithmeticException ae)
			{
				//TODO audit
			}
			finally
			{
				activeChar.getInventory().writeUnlock();
			}
		}
		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}