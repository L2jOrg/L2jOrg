package l2s.gameserver.network.l2.c2s;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.data.xml.holder.BuyListHolder;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ShopPreviewInfoPacket;
import l2s.gameserver.network.l2.s2c.ShopPreviewListPacket;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.npc.BuyListTemplate;
import l2s.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestPreviewItem extends L2GameClientPacket
{
	// format: cdddb
	private static final Logger _log = LoggerFactory.getLogger(RequestPreviewItem.class);

	@SuppressWarnings("unused")
	private int _unknow;
	private int _listId;
	private int _count;
	private int[] _items;

	@Override
	protected void readImpl()
	{
		_unknow = readD();
		_listId = readD();
		_count = readD();
		if(_count * 4 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new int[_count];
		for(int i = 0; i < _count; i++)
			_items[i] = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null || _count == 0)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.isPK() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		BuyListTemplate list = null;

		NpcInstance merchant = NpcUtils.canPassPacket(activeChar, this, new Object[0]);
		if(merchant != null)
			list = merchant.getBuyList(_listId);

		if(activeChar.isGM() && (merchant == null || list == null || merchant.getNpcId() != list.getNpcId()))
			list = BuyListHolder.getInstance().getBuyList(_listId);

		if(list == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		int slots = 0;
		long totalPrice = 0; // Цена на примерку каждого итема 10 Adena.

		Map<Integer, Integer> itemList = new HashMap<Integer, Integer>();
		try
		{
			for(int i = 0; i < _count; i++)
			{
				int itemId = _items[i];
				if(list.getItemByItemId(itemId) == null)
				{
					activeChar.sendActionFailed();
					return;
				}

				ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
				if(template == null)
					continue;

				if(!template.isEquipable())
					continue;

				int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
				if(paperdoll < 0)
					continue;

				if(template.getItemType() == WeaponType.CROSSBOW || template.getItemType() == WeaponType.RAPIER || template.getItemType() == WeaponType.ANCIENTSWORD)
					continue;

				if(itemList.containsKey(paperdoll))
				{
					activeChar.sendPacket(SystemMsg.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
					return;
				}
				else
					itemList.put(paperdoll, itemId);

				totalPrice += ShopPreviewListPacket.getWearPrice(template);
			}

			if(!activeChar.reduceAdena(totalPrice))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		catch(ArithmeticException ae)
		{
			//TODO audit
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		if(!itemList.isEmpty())
		{
			activeChar.sendPacket(new ShopPreviewInfoPacket(itemList));
			// Schedule task
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(activeChar), Config.WEAR_DELAY * 1000);
		}
	}

	private static class RemoveWearItemsTask extends RunnableImpl
	{
		private Player _activeChar;

		public RemoveWearItemsTask(Player activeChar)
		{
			_activeChar = activeChar;
		}

		public void runImpl() throws Exception
		{
			_activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_);
			_activeChar.sendUserInfo(true);
		}
	}
}