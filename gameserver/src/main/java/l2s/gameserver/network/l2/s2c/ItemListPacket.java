package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.LockType;

public class ItemListPacket extends L2GameServerPacket
{
	private final int _size;
	private final ItemInstance[] _items;
	private final boolean _showWindow;

	private LockType _lockType;
	private int[] _lockItems;

	private Player _player;

	public ItemListPacket(Player player, int size, ItemInstance[] items, boolean showWindow, LockType lockType, int[] lockItems)
	{
		_player = player;
		_size = size;
		_items = items;
		_showWindow = showWindow;
		_lockType = lockType;
		_lockItems = lockItems;
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_showWindow ? 1 : 0);

		writeH(_size);
		for(ItemInstance temp : _items)
		{
			if(temp.getTemplate().isQuest())
				continue;

			writeItemInfo(_player, temp);
		}

		writeH(_lockItems.length);
		if(_lockItems.length > 0)
		{
			writeC(_lockType.ordinal());
			for(int i : _lockItems)
				writeD(i);
		}
	}
}