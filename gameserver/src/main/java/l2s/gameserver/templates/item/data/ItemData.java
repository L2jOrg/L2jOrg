package l2s.gameserver.templates.item.data;

/**
 * @author Bonux
 */
public class ItemData
{
	private final int _id;
	private final long _count;

	public ItemData(int id, long count)
	{
		_id = id;
		_count = count;
	}

	public int getId()
	{
		return _id;
	}

	public long getCount()
	{
		return _count;
	}
}