package l2s.gameserver.templates;

/**
 * @author VISTALL
 * @date  11:14/08.12.2010
 */
public class SoulCrystal
{
	private final int _itemId;
	private final int _level;
	private final int _nextItemId;
	private final int _cursedNextItemId;

	public SoulCrystal(int itemId, int level, int nextItemId, int cursedNextItemId)
	{
		_itemId = itemId;
		_level = level;
		_nextItemId = nextItemId;
		_cursedNextItemId = cursedNextItemId;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getNextItemId()
	{
		return _nextItemId;
	}

	public int getCursedNextItemId()
	{
		return _cursedNextItemId;
	}
}