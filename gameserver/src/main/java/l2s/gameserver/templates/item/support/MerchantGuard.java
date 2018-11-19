package l2s.gameserver.templates.item.support;

/**
 * @author VISTALL
 * @date 14:02/14.07.2011
 */
public class MerchantGuard
{
	private int _itemId;
	private int _npcId;
	private int _max;

	public MerchantGuard(int itemId, int npcId, int max)
	{
		_itemId = itemId;
		_npcId = npcId;
		_max = max;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getMax()
	{
		return _max;
	}
}