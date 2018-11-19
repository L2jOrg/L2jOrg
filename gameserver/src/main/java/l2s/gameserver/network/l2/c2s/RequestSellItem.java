package l2s.gameserver.network.l2.c2s;

import l2s.commons.math.SafeMath;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExBuySellListPacket;
import l2s.gameserver.utils.Log;
import l2s.gameserver.utils.NpcUtils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * packet type id 0x37
 * format:		cddb, b - array if(ddd)
 */
public class RequestSellItem extends L2GameClientPacket
{
	private int _listId;
	private int _count;
	private int[] _items; // object id
	private long[] _itemQ; // count

	@Override
	protected void readImpl()
	{
		_listId = readD();
		_count = readD();
		if(_count * 16 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}
		_items = new int[_count];
		_itemQ = new long[_count];

		for(int i = 0; i < _count; i++)
		{
			_items[i] = readD(); // object id
			readD(); //item id
			_itemQ[i] = readQ(); // count
			if(_itemQ[i] < 1 || ArrayUtils.indexOf(_items, _items[i]) < i)
			{
				_count = 0;
				break;
			}
		}
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

		if(!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.isPK() && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		NpcInstance merchant = NpcUtils.canPassPacket(activeChar, this);
		if(!Config.BBS_SELL_ITEMS_ENABLED && merchant == null && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		long totalPrice = 0;
		long tax = 0;
		double taxRate = 0;

		Castle castle = null;
		if(merchant != null)
		{
			castle = merchant.getCastle(activeChar);
			if(castle != null)
				taxRate = castle.getBuyTaxRate();
		}

		final Inventory inventory = activeChar.getInventory();

		inventory.writeLock();
		activeChar.getRefund().writeLock();
		try
		{
			for(int i = 0; i < _count; i++)
			{
				int objectId = _items[i];
				long count = _itemQ[i];
				if(count <= 0)
					continue;

				ItemInstance item = inventory.getItemByObjectId(objectId);
				if(item == null || item.getCount() < count || !item.canBeSold(activeChar))
					continue;

				totalPrice = SafeMath.addAndCheck(totalPrice, Config.ALT_SELL_ITEM_ONE_ADENA ? 1 : SafeMath.mulAndCheck(item.getReferencePrice(), count) / 2);

				if(Config.ALLOW_ITEMS_REFUND)
				{
					ItemInstance refund = inventory.removeItemByObjectId(objectId, count);
					Log.LogItem(activeChar, Log.RefundSell, refund);
					activeChar.getRefund().addItem(refund);
				}
				else
				{
					inventory.destroyItemByObjectId(objectId, count);
					Log.LogItem(activeChar, Log.RefundSell, item, count);
				}
			}
		}
		catch(ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			inventory.writeUnlock();
			activeChar.getRefund().writeUnlock();
		}

		tax = (long) (totalPrice * taxRate);

		totalPrice -= tax;

		activeChar.addAdena(totalPrice);

		if(castle != null)
		{
			if(tax > 0 && castle.getOwnerId() > 0 && activeChar.getReflection().isMain())
				castle.addToTreasury(tax, true);
		}

		activeChar.sendPacket(new ExBuySellListPacket.SellRefundList(activeChar, true, 0.));
		activeChar.sendChanges();
	}
}