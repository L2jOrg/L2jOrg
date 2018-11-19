package l2s.gameserver.templates.fakeplayer.actions;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.handler.items.IItemHandler;
import l2s.gameserver.handler.items.impl.BlessedSpiritShotItemHandler;
import l2s.gameserver.handler.items.impl.SoulShotItemHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.SoulShotType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.utils.ItemFunctions;

import org.dom4j.Element;

public class AddItemAction extends AbstractAction
{
	private final int _itemId;
	private final long _minCount;
	private final long _maxCount;

	public AddItemAction(int itemId, long minCount, long maxCount, double chance)
	{
		super(chance);
		_itemId = itemId;
		_minCount = minCount;
		_maxCount = maxCount;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		Player player = ai.getActor();

		ItemFunctions.addItem(player, _itemId, Rnd.get(_minCount, _maxCount), true);

		player.getInventory().writeLock();
		try
		{
			ItemInstance item = player.getInventory().getItemByItemId(_itemId);
			if(item != null)
			{
				IItemHandler handler = item.getTemplate().getHandler();
				if(handler != null && handler.isAutoUse())
				{
					if(handler instanceof BlessedSpiritShotItemHandler || handler instanceof BlessedSpiritShotItemHandler)
						player.addAutoShot(item.getItemId(), true, SoulShotType.SPIRITSHOT);
					else if(handler instanceof SoulShotItemHandler)
						player.addAutoShot(item.getItemId(), true, SoulShotType.SOULSHOT);
					ItemFunctions.useItem(player, item, false, false);
				}
				return true;
			}
		}
		finally
		{
			player.getInventory().writeUnlock();
		}
		return false;
	}

	public static AddItemAction parse(Element element)
	{
		int itemId = Integer.parseInt(element.attributeValue("id"));
		long minCount = element.attributeValue("count") != null ? Long.parseLong(element.attributeValue("count")) : Long.parseLong(element.attributeValue("min_count"));
		long maxCount = element.attributeValue("max_count") == null ? minCount : Long.parseLong(element.attributeValue("max_count"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new AddItemAction(itemId, minCount, maxCount, chance);
	}
}