package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.items.Warehouse;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * @author VISTALL
 * @date 20:46/16.05.2011
 */
public class PackageSendableListPacket extends L2GameServerPacket
{
	private final int sendType;
	private int _targetObjectId;
	private long _adena;
	private List<ItemInfo> _itemList;

	public PackageSendableListPacket(int sendType, int objectId, Player cha) {
		this.sendType = sendType;
		_adena = cha.getAdena();
		_targetObjectId = objectId;

		ItemInstance[] items = cha.getInventory().getItems();
		Arrays.sort(items, Warehouse.ItemClassComparator.getInstance());
		_itemList = new ArrayList<ItemInfo>(items.length);
		for(ItemInstance item : items)
			if(item.getTemplate().isFreightable())
				_itemList.add(new ItemInfo(item, item.getTemplate().isBlocked(cha, item)));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) sendType);
		if(sendType == 2) {
			buffer.putInt(_itemList.size());
			buffer.putInt(_itemList.size());

			for(ItemInfo item : _itemList) {
				writeItemInfo(buffer, item);
				buffer.putInt(item.getObjectId());
			}
		} else {
			buffer.putInt(_targetObjectId);
			buffer.putLong(_adena);
			buffer.putInt(_itemList.size());
		}
	}
}
