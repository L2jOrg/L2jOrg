package l2s.gameserver.model.items;

/**
 * Абстрактное описание предмета, безопасное для любых операций. Может использоваться как ссылка на уже существующий предмет либо как набор информации для создания нового.
 */
public final class TradeItem extends ItemInfo
{
	private long _price;
	private long _referencePrice;
	private long _currentValue;
	private int _lastRechargeTime;
	private int _rechargeTime;

	public TradeItem()
	{
		super();
	}

	public TradeItem(ItemInstance item)
	{
		this(item, false);
	}

	public TradeItem(ItemInstance item, boolean isBlocked)
	{
		super(item, isBlocked);
		setReferencePrice(item.getReferencePrice());
	}

	public void setOwnersPrice(long price)
	{
		_price = price;
	}

	public long getOwnersPrice()
	{
		return _price;
	}

	public void setReferencePrice(long price)
	{
		_referencePrice = price;
	}

	public long getReferencePrice()
	{
		return _referencePrice;
	}

	public long getStorePrice()
	{
		return getReferencePrice() / 2;
	}

	public void setCurrentValue(long value)
	{
		_currentValue = value;
	}

	public long getCurrentValue()
	{
		return _currentValue;
	}

	/**
	 * Устанавливает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @param rechargeTime : unixtime в минутах
	 */
	public void setRechargeTime(int rechargeTime)
	{
		_rechargeTime = rechargeTime;
	}

	/**
	 * Возвращает время респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @return unixtime в минутах
	 */
	public int getRechargeTime()
	{
		return _rechargeTime;
	}

	/**
	 * Возвращает ограничен ли этот предмет в количестве, используется в NPC магазинах с ограниченным количеством.
	 * @return true, если ограничен
	 */
	public boolean isCountLimited()
	{
		return getCount() > 0;
	}

	/**
	 * Устанавливает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @param lastRechargeTime : unixtime в минутах
	 */
	public void setLastRechargeTime(int lastRechargeTime)
	{
		_lastRechargeTime = lastRechargeTime;
	}

	/**
	 * Возвращает время последнего респауна предмета, используется в NPC магазинах с ограниченным количеством.
	 * @return unixtime в минутах
	 */
	public int getLastRechargeTime()
	{
		return _lastRechargeTime;
	}

	@Override
	public TradeItem clone()
	{
		TradeItem item = new TradeItem();
		item.setOwnerId(getOwnerId());
		item.setObjectId(getObjectId());
		item.setItemId(getItemId());
		item.setCount(getCount());
		item.setCustomType1(getCustomType1());
		item.setEquipped(isEquipped());
		item.setEnchantLevel(getEnchantLevel());
		item.setCustomType2(getCustomType2());
		item.setVariationStoneId(getVariationStoneId());
		item.setVariation1Id(getVariation1Id());
		item.setVariation2Id(getVariation2Id());
		item.setShadowLifeTime(getShadowLifeTime());
		item.setEquipSlot(getEquipSlot());
		item.setTemporalLifeTime(getTemporalLifeTime());
		item.setEnchantOptions(getEnchantOptions());
		item.setAttributeFire(getAttributeFire());
		item.setAttributeWater(getAttributeWater());
		item.setAttributeWind(getAttributeWind());
		item.setAttributeEarth(getAttributeEarth());
		item.setAttributeHoly(getAttributeHoly());
		item.setAttributeUnholy(getAttributeUnholy());
		item.setIsBlocked(isBlocked());

		item.setOwnersPrice(getOwnersPrice());
		item.setReferencePrice(getReferencePrice());
		item.setCurrentValue(getCurrentValue());
		item.setLastRechargeTime(getLastRechargeTime());
		item.setRechargeTime(getRechargeTime());
		return item;
	}
}