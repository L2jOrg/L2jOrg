package l2s.gameserver.stats.conditions;

import l2s.gameserver.stats.Env;
import l2s.gameserver.utils.ItemFunctions;

public final class ConditionSlotItemId extends ConditionInventory
{
	private final int _itemId;

	private final int _enchantLevel;

	public ConditionSlotItemId(int slot, int itemId, int enchantLevel)
	{
		super(slot);
		_itemId = itemId;
		_enchantLevel = enchantLevel;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		return ItemFunctions.checkIsEquipped(env.character.getPlayer(), _slot, _itemId, _enchantLevel);
	}
}
