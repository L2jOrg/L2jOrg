package l2s.gameserver.templates.fakeplayer.actions;

import l2s.gameserver.ai.FakeAI;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.utils.ItemFunctions;

import org.dom4j.Element;

public class UseItemAction extends AbstractAction
{
	private final int _itemId;

	public UseItemAction(int itemId, double chance)
	{
		super(chance);
		_itemId = itemId;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		Player player = ai.getActor();

		player.getInventory().writeLock();
		try
		{
			ItemInstance item = player.getInventory().getItemByItemId(_itemId);
			if(item == null)
			{
				ItemFunctions.addItem(player, _itemId, 1, true);

				item = player.getInventory().getItemByItemId(_itemId);
				if(item == null)
				{
					player.sendActionFailed();
					return false;
				}
			}
			return ItemFunctions.useItem(player, item, false, true);
		}
		finally
		{
			player.getInventory().writeUnlock();
		}
	}

	@Override
	public boolean checkCondition(FakeAI ai, boolean force)
	{
		return !ai.getActor().isUseItemDisabled();
	}

	public static UseItemAction parse(Element element)
	{
		int itemId = Integer.parseInt(element.attributeValue("id"));
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new UseItemAction(itemId, chance);
	}
}