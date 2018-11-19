package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 * @date 4:20/06.05.2011
 */
public class ExGMViewQuestItemListPacket extends L2GameServerPacket
{
	private int _size;
	private ItemInstance[] _items;

	private int _limit;
	private String _name;

	public ExGMViewQuestItemListPacket(Player player, ItemInstance[] items, int size)
	{
		_items = items;
		_size = size;
		_name = player.getName();
		_limit = Config.QUEST_INVENTORY_MAXIMUM;
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_name);
		writeD(_limit);
		writeH(_size);
		for(ItemInstance temp : _items)
			if(temp.getTemplate().isQuest())
				writeItemInfo(temp);
	}
}
