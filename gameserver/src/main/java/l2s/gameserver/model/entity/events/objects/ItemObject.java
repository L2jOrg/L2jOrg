package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

public class ItemObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final int _itemId;
	private final long _count;

	public ItemObject(int itemId, long count)
	{
		_itemId = itemId;
		_count = count;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getCount()
	{
		return _count;
	}
}