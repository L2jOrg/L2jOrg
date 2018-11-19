package l2s.gameserver.model.base;

import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.items.ItemAttributes;

public class MultiSellIngredient implements Cloneable
{
	private int _itemId;
	private long _itemCount;
	private int _itemEnchant;
	private int _chance;
	private ItemAttributes _itemAttributes;
	private boolean _mantainIngredient;
	private int _flags;
	private int _durability;

	public MultiSellIngredient(int itemId, long itemCount)
	{
		this(itemId, itemCount, 0, -1, -1, 0);
	}

	public MultiSellIngredient(int itemId, long itemCount, int chance)
	{
		this(itemId, itemCount, chance, -1, -1, 0);
	}

	public MultiSellIngredient(int itemId, long itemCount, int chance, int flags, int durability, int enchant)
	{
		_itemId = itemId;
		_itemCount = itemCount;
		_chance = chance;
		_flags = flags;
		_durability = durability;
		_itemEnchant = enchant;
		_mantainIngredient = false;
		_itemAttributes = new ItemAttributes();
	}

	@Override
	public MultiSellIngredient clone()
	{
		MultiSellIngredient mi = new MultiSellIngredient(_itemId, _itemCount, _chance);
		mi.setItemEnchant(_itemEnchant);
		mi.setMantainIngredient(_mantainIngredient);
		mi.setItemAttributes(_itemAttributes.clone());
		return mi;
	}

	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public void setItemCount(long itemCount)
	{
		_itemCount = itemCount;
	}

	public long getItemCount()
	{
		return _itemCount;
	}

	public boolean isStackable()
	{
		return _itemId <= 0 || ItemHolder.getInstance().getTemplate(_itemId).isStackable();
	}

	public void setItemEnchant(int itemEnchant)
	{
		_itemEnchant = itemEnchant;
	}

	public int getItemEnchant()
	{
		return _itemEnchant;
	}

	public void setFlags(int value)
	{
		_flags = value;
	}

	public int getFlags()
	{
		return _flags;
	}

	public void setDurablity(int value)
	{
		_durability = value;
	}

	public int getDurability()
	{
		return _durability;
	}

	public ItemAttributes getItemAttributes()
	{
		return _itemAttributes;
	}

	public void setItemAttributes(ItemAttributes attr)
	{
		_itemAttributes = attr;
	}

	public void setChance(int val)
	{
		_chance = val;
	}

	public int getChance()
	{
		return _chance;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (_itemCount ^ _itemCount >>> 32);
		for(Element e : Element.VALUES)
			result = prime * result + _itemAttributes.getValue(e);
		result = prime * result + _itemEnchant;
		result = prime * result + _itemId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		MultiSellIngredient other = (MultiSellIngredient) obj;
		if(_itemId != other._itemId)
			return false;
		if(_itemCount != other._itemCount)
			return false;
		if(_itemEnchant != other._itemEnchant)
			return false;
		for(Element e : Element.VALUES)
			if(_itemAttributes.getValue(e) != other._itemAttributes.getValue(e))
				return false;
		return true;
	}

	public boolean getMantainIngredient()
	{
		return _mantainIngredient;
	}

	public void setMantainIngredient(boolean mantainIngredient)
	{
		_mantainIngredient = mantainIngredient;
	}
}