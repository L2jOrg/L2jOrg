package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.Warehouse;

/**
 * @author VISTALL
 * @date 20:46/16.05.2011
 */
public class PackageSendableListPacket extends L2GameServerPacket
{
	private int _targetObjectId;
	private long _adena;
	private List<ItemInfo> _itemList;

	public PackageSendableListPacket(int objectId, Player cha)
	{
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
	protected final void writeImpl()
	{
		writeD(_targetObjectId);
		writeQ(_adena);
		writeD(_itemList.size());
		for(ItemInfo item : _itemList)
		{
			writeItemInfo(item);
			writeD(item.getObjectId());
		}
	}
}
