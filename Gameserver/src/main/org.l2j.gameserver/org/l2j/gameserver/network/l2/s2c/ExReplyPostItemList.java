package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends L2GameServerPacket
{
	private List<ItemInfo> _itemsList = new ArrayList<ItemInfo>();

	public ExReplyPostItemList(Player activeChar)
	{
		ItemInstance[] items = activeChar.getInventory().getItems();
		for(ItemInstance item : items)
			if(item.canBeTraded(activeChar))
				_itemsList.add(new ItemInfo(item, item.getTemplate().isBlocked(activeChar, item)));
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemsList.size());
		for(ItemInfo item : _itemsList)
			writeItemInfo(buffer, item);
	}
}