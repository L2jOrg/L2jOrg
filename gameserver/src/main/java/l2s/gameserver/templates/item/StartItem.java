package l2s.gameserver.templates.item;

import l2s.gameserver.templates.item.data.ItemData;

/**
 * @author Bonux
 */
public final class StartItem extends ItemData
{
	private final boolean _equiped;
	private final int _enchantLevel;

	public StartItem(int id, long count, boolean equiped, int enchantLevel)
	{
		super(id, count);
		_equiped = equiped;
		_enchantLevel = enchantLevel;
	}

	public boolean isEquiped()
	{
		return _equiped;
	}

	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
}