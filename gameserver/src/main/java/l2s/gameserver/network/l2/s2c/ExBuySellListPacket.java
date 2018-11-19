package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.network.l2.ServerPacketOpcodes;
import l2s.gameserver.templates.npc.BuyListTemplate;

public abstract class ExBuySellListPacket extends L2GameServerPacket
{
	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExBuySellListPacket;
	}

	public static class BuyList extends ExBuySellListPacket
	{
		private final int _listId;
		private final List<TradeItem> _buyList;
		private final long _adena;
		private final double _taxRate;

		public BuyList(BuyListTemplate buyList, Player activeChar, double taxRate)
		{
			_adena = activeChar.getAdena();
			_taxRate = taxRate;

			if(buyList != null)
			{
				_listId = buyList.getListId();
				_buyList = buyList.getItems();
				activeChar.setBuyListId(_listId);
			}
			else
			{
				_listId = 0;
				_buyList = Collections.emptyList();
				activeChar.setBuyListId(0);
			}
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x00); // BUY LIST TYPE
			writeQ(_adena); // current money
			writeD(_listId);
			writeD(0x00); //TODO [Bonux] Awakening
			writeH(_buyList.size());
			for(TradeItem item : _buyList)
			{
				writeItemInfo(item, item.getCurrentValue());
				writeQ((long) (item.getOwnersPrice() * (1. + _taxRate)));
			}
		}
	}

	public static class SellRefundList extends ExBuySellListPacket
	{
		private final List<TradeItem> _sellList;
		private final List<TradeItem> _refundList;
		private int _done;
		private final double _taxRate;

		public SellRefundList(Player activeChar, boolean done, double taxRate)
		{
			_done = done ? 1 : 0;
			_taxRate = taxRate;
			if(done)
			{
				_refundList = Collections.emptyList();
				_sellList = Collections.emptyList();
			}
			else
			{
				ItemInstance[] items = activeChar.getRefund().getItems();
				if(Config.ALLOW_ITEMS_REFUND)
				{
					_refundList = new ArrayList<TradeItem>(items.length);
					for(ItemInstance item : items)
						_refundList.add(new TradeItem(item));
				}
				else
					_refundList = new ArrayList<TradeItem>(0);

				items = activeChar.getInventory().getItems();
				_sellList = new ArrayList<TradeItem>(items.length);
				for(ItemInstance item : items)
					if(item.canBeSold(activeChar))
						_sellList.add(new TradeItem(item, item.getTemplate().isBlocked(activeChar, item)));
			}
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x01); // SELL/REFUND LIST TYPE
			writeD(0x00); //TODO [Bonux] Awakening
			writeH(_sellList.size());
			for(TradeItem item : _sellList)
			{
				writeItemInfo(item);
				if(Config.ALT_SELL_ITEM_ONE_ADENA)
					writeQ(1);
				else
					writeQ(item.getReferencePrice() / 2);
			}
			writeH(_refundList.size());
			for(TradeItem item : _refundList)
			{
				writeItemInfo(item);
				writeD(item.getObjectId());
				if(Config.ALT_SELL_ITEM_ONE_ADENA)
					writeQ(item.getCount());
				else	
					writeQ((long) (item.getCount() * item.getReferencePrice() / 2 * (1. - _taxRate)));
			}
			writeC(_done);
		}
	}
}