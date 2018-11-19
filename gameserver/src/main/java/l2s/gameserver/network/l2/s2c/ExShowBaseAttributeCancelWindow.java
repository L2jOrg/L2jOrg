package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.ItemTemplate;

/**
 * @author SYS
 */
public class ExShowBaseAttributeCancelWindow extends L2GameServerPacket
{
	private final List<ItemInstance> _items = new ArrayList<ItemInstance>();

	public ExShowBaseAttributeCancelWindow(Player activeChar)
	{
		for(ItemInstance item : activeChar.getInventory().getItems())
		{
			if(item.getAttributeElement() == Element.NONE || !item.canBeEnchanted() || getAttributeRemovePrice(item) == 0)
				continue;
			_items.add(item);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_items.size());
		for(ItemInstance item : _items)
		{
			writeD(item.getObjectId());
			writeQ(getAttributeRemovePrice(item));
		}
	}

	public static long getAttributeRemovePrice(ItemInstance item)
	{
		switch(item.getGrade())
		{
			case S:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 50000 : 40000;
			case S80:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 100000 : 80000;
			case S84:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 200000 : 160000;
			case R:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 250000 : 240000;
			case R95:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 300000 : 280000;
			case R99:
				return item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON ? 350000 : 320000;

		}
		return 0;
	}
}