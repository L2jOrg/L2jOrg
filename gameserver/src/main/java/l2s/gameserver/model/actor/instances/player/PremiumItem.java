package l2s.gameserver.model.actor.instances.player;

/**
 * @author Gnacik
 * @corrected by n0nam3
 **/
public class PremiumItem
{
	private final int _receiveTime;
	private final int _itemId;
	private long _itemCount;
	private final String _sender;

	public PremiumItem(int receiveTime, int itemId, long itemCount, String sender)
	{
		_receiveTime = receiveTime;
		_itemId = itemId;
		_itemCount = itemCount;
		_sender = sender;
	}

	public PremiumItem(int itemId, long itemCount, String sender)
	{
		this((int) (System.currentTimeMillis() / 1000L), itemId, itemCount, sender);
	}

	public int getReceiveTime()
	{
		return _receiveTime;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void setItemCount(long value)
	{
		_itemCount = value;
	}

	public long getItemCount()
	{
		return _itemCount;
	}

	public String getSender()
	{
		return _sender;
	}

	@Override
	public String toString()
	{
		return "PremiumItem[receiveTime=" + _receiveTime + ", itemId=" + _itemId + ", itemCount=" + _itemCount + ", sender=" + _sender + "]";
	}
}