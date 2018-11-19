package l2s.gameserver.network.l2.c2s;

import java.util.List;

import l2s.commons.math.SafeMath;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.TradeDonePacket;
import l2s.gameserver.network.l2.s2c.TradeOtherAddPacket;
import l2s.gameserver.network.l2.s2c.TradeOwnAddPacket;
import l2s.gameserver.network.l2.s2c.TradeUpdatePacket;

public class AddTradeItem extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _tradeId;
	private int _objectId;
	private long _amount;

	@Override
	protected void readImpl()
	{
		_tradeId = readD(); // 1 ?
		_objectId = readD();
		_amount = readQ();
	}

	@Override
	protected void runImpl()
	{
		Player parthner1 = getClient().getActiveChar();
		if(parthner1 == null || _amount < 1)
			return;
		
		Request request = parthner1.getRequest();
		if(request == null || !request.isTypeOf(L2RequestType.TRADE))
		{
			parthner1.sendActionFailed();
			return;
		}

		if(!request.isInProgress())
		{
			request.cancel(TradeDonePacket.FAIL);
			parthner1.sendActionFailed();
			return;
		}

		if(parthner1.isOutOfControl())
		{
			request.cancel(TradeDonePacket.FAIL);
			parthner1.sendActionFailed();
			return;
		}

		Player parthner2 = request.getOtherPlayer(parthner1);
		if(parthner2 == null)
		{
			request.cancel(TradeDonePacket.FAIL);
			parthner1.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			parthner1.sendActionFailed();
			return;
		}

		if(parthner2.getRequest() != request)
		{
			request.cancel(TradeDonePacket.FAIL);
			parthner1.sendActionFailed();
			return;
		}

		if(request.isConfirmed(parthner1) || request.isConfirmed(parthner2))
		{
			parthner1.sendPacket(SystemMsg.YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED);
			parthner1.sendActionFailed();
			return;
		}

		ItemInstance item = parthner1.getInventory().getItemByObjectId(_objectId);
		if(item == null || !item.canBeTraded(parthner1))
		{
			parthner1.sendPacket(SystemMsg.THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD);
			return;
		}

		long count = Math.min(_amount, item.getCount());

		List<TradeItem> tradeList = parthner1.getTradeList();
		TradeItem tradeItem = null;

		try
		{
			for(TradeItem ti : parthner1.getTradeList())
				if(ti.getObjectId() == _objectId)
				{
					count = SafeMath.addAndCheck(count, ti.getCount());
					count = Math.min(count, item.getCount());
					ti.setCount(count);
					tradeItem = ti;
					break;
				}
		}
		catch(ArithmeticException ae)
		{
			parthner1.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return;
		}

		if(tradeItem == null)
		{
			// добавляем новую вещь в список
			tradeItem = new TradeItem(item);
			tradeItem.setCount(count);
			tradeList.add(tradeItem);
		}

		parthner1.sendPacket(new TradeOwnAddPacket(tradeItem, tradeItem.getCount()), new TradeUpdatePacket(tradeItem, item.getCount() - tradeItem.getCount()));
		parthner2.sendPacket(new TradeOtherAddPacket(tradeItem, tradeItem.getCount()));
	}
}