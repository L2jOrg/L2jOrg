package l2s.gameserver.templates;

import l2s.gameserver.utils.Location;

public class TeleportLocation extends Location
{
	private static final long serialVersionUID = 1L;

	private final int _itemId;
	private final long _price;
	private final int _name;
	private final int[] _castleIds;
	private final boolean _primeHours;
	private final int _questZoneId;

	public TeleportLocation(int itemId, long price, int name, int[] castleIds, boolean primeHours, int questZoneId)
	{
		_itemId = itemId;
		_price = price;
		_name = name;
		_castleIds = castleIds;
		_primeHours = primeHours;
		_questZoneId = questZoneId;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public long getPrice()
	{
		return _price;
	}

	public int getName()
	{
		return _name;
	}

	public int[] getCastleIds()
	{
		return _castleIds;
	}

	public boolean isPrimeHours()
	{
		return _primeHours;
	}

	public int getQuestZoneId()
	{
		return _questZoneId;
	}
}