package org.l2j.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.Contants;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.database.mysql;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.ItemInstance.ItemLocation;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExNoticePostArrived;
import org.l2j.gameserver.network.l2.s2c.ExReplyWritePost;
import org.l2j.gameserver.network.l2.s2c.ExUnReadMailCount;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.utils.Functions;
import org.l2j.gameserver.utils.Log;
import org.l2j.gameserver.utils.Util;

/**
 * Запрос на отсылку нового письма. В ответ шлется {@link ExReplyWritePost}.
 *
 * @see RequestExPostItemList
 * @see RequestExRequestReceivedPostList
 */
public class RequestExSendPost extends L2GameClientPacket
{
	private int _messageType;
	private String _recieverName, _topic, _body;
	private int _count;
	private int[] _items;
	private long[] _itemQ;
	private long _price;

	/**
	 * format: SdSS dx[dQ] Q
	 */
	@Override
	protected void readImpl()
	{
		_recieverName = readS(35); // имя адресата
		_messageType = readInt(); // тип письма, 0 простое 1 с запросом оплаты
		_topic = readS(Byte.MAX_VALUE); // topic
		_body = readS(Short.MAX_VALUE); // body

		_count = readInt(); // число прикрепленных вещей
		if(_count * 12 + 4 > availableData() || _count > Short.MAX_VALUE || _count < 1) //TODO [G1ta0] audit
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];

		for(int i = 0; i < _count; i++)
		{
			_items[i] = readInt(); // objectId
			_itemQ[i] = readLong(); // количество
			if(_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				return;
			}
		}

		_price = readLong(); // цена для писем с запросом оплаты

		if(_price < 0)
		{
			_count = 0;
			_price = 0;
		}
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
		if(!activeChar.getPlayerAccess().UseMail)
		{
			activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
			activeChar.sendActionFailed();
			return;
		}
		// Custom
		if(activeChar.isGM() && _recieverName.equalsIgnoreCase("ONLINE_ALL"))
		{
			Map<Integer, Long> map = new HashMap<Integer, Long>();
			if(_items != null && _items.length > 0)
				for(int i = 0; i < _items.length; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
					map.put(item.getItemId(), _itemQ[i]);
				}

			for(Player p : GameObjectsStorage.getPlayers())
				if(p != null && p.isOnline())
					Functions.sendSystemMail(p, _topic, _body, map);

			activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
			activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
			return;
		}

		if(!Config.ALLOW_MAIL)
		{
			activeChar.sendMessage(new CustomMessage("mail.Disabled"));
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE);
			return;
		}

		if(activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		if(activeChar.getName().equalsIgnoreCase(_recieverName))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF);
			return;
		}

		if(_count > 0 && !activeChar.isInPeaceZone())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_IN_A_NONPEACE_ZONE_LOCATION);
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

		if(!activeChar.getAntiFlood().canMail())
		{
			activeChar.sendPacket(SystemMsg.THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED);
			return;
		}

		if(_price > 0)
		{
			if(!activeChar.getPlayerAccess().UseMail)
			{
				activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
				activeChar.sendActionFailed();
				return;
			}

			String tradeBan = activeChar.getVar("tradeBan");
			if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			{
				if(tradeBan.equals("-1"))
					activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently"));
				else
					activeChar.sendMessage(new CustomMessage("common.TradeBanned").addString(Util.formatTime((int) (Long.parseLong(tradeBan) / 1000L - System.currentTimeMillis() / 1000L))));
				return;
			}
		}

		// ищем цель и проверяем блоклисты
		if(activeChar.getBlockList().contains(_recieverName)) // тем кто в блоклисте не шлем
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_BLOCKED_C1).addString(_recieverName));
			return;
		}

		int recieverId;
		Player target = World.getPlayer(_recieverName);
		if(target != null)
		{
			recieverId = target.getObjectId();
			_recieverName = target.getName();
			if(target.getBlockList().contains(activeChar)) // цель заблокировала отправителя
			{
				activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_recieverName));
				return;
			}
		}
		else
		{
			recieverId = CharacterDAO.getInstance().getObjectIdByName(_recieverName);
			if(recieverId > 0)
				//TODO [G1ta0] корректировать _recieverName
				if(mysql.simple_get_int("target_Id", "character_blocklist", "obj_Id=" + recieverId + " AND target_Id=" + activeChar.getObjectId()) > 0) // цель заблокировала отправителя
				{
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1).addString(_recieverName));
					return;
				}
		}

		if(recieverId == 0) // не нашли цель?
		{
			activeChar.sendPacket(SystemMsg.WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE);
			return;
		}

		int expireTime = (_messageType == 1 ? 12 : 360) * 3600 + (int) (System.currentTimeMillis() / 1000L); //TODO [G1ta0] хардкод времени актуальности почты

		if(_count > 8) //клиент не дает отправить больше 8 вещей
		{
			activeChar.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		long serviceCost = 100 + _count * 1000; //TODO [G1ta0] хардкод цена за почту

		List<ItemInstance> attachments = new ArrayList<ItemInstance>();

		activeChar.getInventory().writeLock();
		try
		{
			if(activeChar.getAdena() < serviceCost)
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
				return;
			}

			// подготовить аттачи
			if(_count > 0)
				for(int i = 0; i < _count; i++)
				{
					ItemInstance item = activeChar.getInventory().getItemByObjectId(_items[i]);
					if(item == null || item.getCount() < _itemQ[i] || (item.getItemId() == Items.ADENA && item.getCount() < _itemQ[i] + serviceCost) || !item.canBeTraded(activeChar))
					{
						activeChar.sendPacket(SystemMsg.THE_ITEM_THAT_YOURE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISNT_PROPER);
						return;
					}
				}

			if(!activeChar.reduceAdena(serviceCost, true))
			{
				activeChar.sendPacket(SystemMsg.YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA);
				return;
			}

			if(_count > 0)
			{
				for(int i = 0; i < _count; i++)
				{
					ItemInstance item = activeChar.getInventory().removeItemByObjectId(_items[i], _itemQ[i]);

					Log.LogItem(activeChar, Log.PostSend, item);

					item.setOwnerId(activeChar.getObjectId());
					item.setLocation(ItemLocation.MAIL);
					if(item.getJdbcState().isSavable())
					{
						item.save();
					}
					else
					{
						item.setJdbcState(JdbcEntityState.UPDATED);
						item.update();
					}

					attachments.add(item);
				}
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		Mail mail = new Mail();
		mail.setSenderId(activeChar.getObjectId());
		mail.setSenderName(activeChar.getName());
		mail.setReceiverId(recieverId);
		mail.setReceiverName(_recieverName);
		mail.setTopic(_topic);
		mail.setBody(_body);
		mail.setPrice(_messageType > 0 ? _price : 0);
		mail.setUnread(true);
		mail.setType(Mail.SenderType.NORMAL);
		mail.setExpireTime(expireTime);
		for(ItemInstance item : attachments)
			mail.addAttachment(item);
		mail.save();

		activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
		activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);

		if(target != null)
		{
			target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			target.sendPacket(new ExUnReadMailCount(target));
			target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}
	}
}