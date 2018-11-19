package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 **/
public final class ItemHaveCondition implements ICheckStartCondition
{
	private final int _itemId;
	private final long _count;

	public ItemHaveCondition(int itemId, long count)
	{
		_itemId = itemId;
		_count = count;
	}

	@Override
	public final boolean checkCondition(Player player)
	{
		return ItemFunctions.haveItem(player, _itemId, _count);
	}
}