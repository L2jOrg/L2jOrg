package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.TradeItem;
import org.l2j.gameserver.model.items.Warehouse.ItemClassComparator;
import org.l2j.gameserver.network.l2.GameClient;

public class PrivateStoreBuyManageList extends L2GameServerPacket
{
	private final int sendType;
	private int _buyerId;
	private long _adena;
	private List<TradeItem> _buyList0;
	private List<TradeItem> _buyList;

	/**
	 * Окно управления личным магазином покупки
	 * @param buyer
	 */
	public PrivateStoreBuyManageList(int sendType, Player buyer) {
		this.sendType = sendType;
		_buyerId = buyer.getObjectId();
		_adena = buyer.getAdena();
		_buyList0 = buyer.getBuyList();
		_buyList = new ArrayList<TradeItem>();

		ItemInstance[] items = buyer.getInventory().getItems();
		Arrays.sort(items, ItemClassComparator.getInstance());
		TradeItem bi;
		for(ItemInstance item : items)
			if(item.canBePrivateStore(buyer) && item.getItemId() != Items.ADENA)
			{
				_buyList.add(bi = new TradeItem(item, item.getTemplate().isBlocked(buyer, item)));
				bi.setObjectId(0);
			}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {

		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(_buyList.size());
			buffer.putInt(_buyList.size());

			for(TradeItem bi : _buyList)
			{
				writeItemInfo(buffer, bi);
				buffer.putLong(bi.getStorePrice());
			}
		} else {
			buffer.putInt(_buyerId);
			buffer.putLong(_adena);
			buffer.putInt(0x00);
			for(TradeItem bi : _buyList)
			{
				writeItemInfo(buffer, bi);
				buffer.putLong(bi.getStorePrice());
			}
			buffer.putInt(0x00);
			for(TradeItem bi : _buyList0)
			{
				writeItemInfo(buffer, bi);
				buffer.putLong(bi.getOwnersPrice());
				buffer.putLong(bi.getStorePrice());
				buffer.putLong(bi.getCount());
			}
		}
	}
}