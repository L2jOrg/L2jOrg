package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.network.l2.GameClient;

public class PrivateStoreManageList extends L2GameServerPacket
{
	private final int sendType;
	private int _sellerId;
	private long _adena;
	private boolean _package;
	private List<TradeItem> _sellList;
	private List<TradeItem> _sellList0;

	/**
	 * Окно управления личным магазином продажи
	 * @param seller
	 */
	public PrivateStoreManageList(int sendType, Player seller, boolean pkg) {
		this.sendType = sendType;
		_sellerId = seller.getObjectId();
		_adena = seller.getAdena();
		_package = pkg;
		_sellList0 = seller.getSellList(_package);
		_sellList = new ArrayList<TradeItem>();

		// Проверяем список вещей в инвентаре, если вещь остутствует - убираем из списка продажи
		for(TradeItem si : _sellList0)
		{
			if(si.getCount() <= 0)
			{
				_sellList0.remove(si);
				continue;
			}

			ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
			if(item == null)
				//вещь недоступна, пробуем найти такую же по itemId
				item = seller.getInventory().getItemByItemId(si.getItemId());

			if(item == null || !item.canBePrivateStore(seller) || item.getItemId() == Items.ADENA)
			{
				_sellList0.remove(si);
				continue;
			}

			//корректируем количество
			si.setCount(Math.min(item.getCount(), si.getCount()));
		}

		ItemInstance[] items = seller.getInventory().getItems();
		// Проверяем список вещей в инвентаре, если вещь остутствует в списке продажи, добавляем в список доступных для продажи
		loop: for(ItemInstance item : items)
			if(item.canBePrivateStore(seller) && item.getItemId() != Items.ADENA)
			{
				for(TradeItem si : _sellList0)
					if(si.getObjectId() == item.getObjectId())
					{
						if(si.getCount() == item.getCount())
							continue loop;
						// Показывает остаток вещей для продажи
						TradeItem ti = new TradeItem(item, item.getTemplate().isBlocked(seller, item));
						ti.setCount(item.getCount() - si.getCount());
						_sellList.add(ti);
						continue loop;
					}
				_sellList.add(new TradeItem(item, item.getTemplate().isBlocked(seller, item)));
			}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {

		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(_sellList.size());
			buffer.putInt(_sellList.size());

			for(TradeItem si : _sellList)
			{
				writeItemInfo(buffer, si);
				buffer.putLong(si.getStorePrice());
			}
		} else {
			buffer.putInt(_sellerId);
			buffer.putInt(_package ? 1 : 0);
			buffer.putLong(_adena);
			buffer.putInt(0x00);

			for(TradeItem si : _sellList) {
				writeItemInfo(buffer, si);
				buffer.putLong(si.getStorePrice());
			}
			buffer.putInt(0x00);
			for(TradeItem si : _sellList0)
			{
				writeItemInfo(buffer, si);
				buffer.putLong(si.getOwnersPrice());
				buffer.putLong(si.getStorePrice());
			}
		}
	}
}