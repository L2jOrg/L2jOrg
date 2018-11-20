package org.l2j.gameserver.model.items;

import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.support.Ensoul;

public class ItemInfo
{
	private int ownerId;
	private int lastChange;
	private int type1;
	private int objectId;
	private int itemId;
	private long count;
	private int type2;
	private int customType1;
	private boolean isEquipped;
	private int bodyPart;
	private int enchantLevel;
	private int customType2;
	private int _variationStoneId;
	private int _variation1Id;
	private int _variation2Id;
	private int shadowLifeTime;
	private int equipSlot;
	private int temporalLifeTime;
	private int[] enchantOptions = ItemInstance.EMPTY_ENCHANT_OPTIONS;
	//Attributes
	private int _attrFire;
	private int _attrWater;
	private int _attrWind;
	private int _attrEarth;
	private int _attrHoly;
	private int _attrUnholy;

	private boolean _isBlocked;

	private Ensoul[] _normalEnsouls = ItemInstance.EMPTY_ENSOULS_ARRAY;
	private Ensoul[] _specialEnsouls = ItemInstance.EMPTY_ENSOULS_ARRAY;

	private ItemTemplate item;

	public ItemInfo()
	{}

	public ItemInfo(ItemInstance item)
	{
		this(item, false);
	}

	public ItemInfo(ItemInstance item, boolean isBlocked)
	{
		setOwnerId(item.getOwnerId());
		setObjectId(item.getObjectId());
		setItemId(item.getItemId());
		setCount(item.getCount());
		setCustomType1(item.getCustomType1());
		setEquipped(item.isEquipped());
		setEnchantLevel(item.getEnchantLevel());
		setCustomType2(item.getCustomType2());
		setVariationStoneId(item.getVariationStoneId());
		setVariation1Id(item.getVariation1Id());
		setVariation2Id(item.getVariation2Id());
		setShadowLifeTime(item.getShadowLifeTime());
		setEquipSlot(item.getEquipSlot());
		setTemporalLifeTime(item.getTemporalLifeTime());
		setEnchantOptions(item.getEnchantOptions());
		setAttributeFire(item.getAttributeElementValue(Element.FIRE, true));
		setAttributeWater(item.getAttributeElementValue(Element.WATER, true));
		setAttributeWind(item.getAttributeElementValue(Element.WIND, true));
		setAttributeEarth(item.getAttributeElementValue(Element.EARTH, true));
		setAttributeHoly(item.getAttributeElementValue(Element.HOLY, true));
		setAttributeUnholy(item.getAttributeElementValue(Element.UNHOLY, true));
		setIsBlocked(isBlocked);
		setNormalEnsouls(item.getNormalEnsouls());
		setSpecialEnsouls(item.getSpecialEnsouls());
	}

	public ItemTemplate getItem()
	{
		return item;
	}

	public void setOwnerId(int ownerId)
	{
		this.ownerId = ownerId;
	}

	public void setLastChange(int lastChange)
	{
		this.lastChange = lastChange;
	}

	public void setType1(int type1)
	{
		this.type1 = type1;
	}

	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}

	public void setItemId(int itemId)
	{
		this.itemId = itemId;
		if(itemId > 0)
			item = ItemHolder.getInstance().getTemplate(getItemId());
		else
			item = null;
		if(item != null)
		{
			setType1(item.getType1());
			setType2(item.getType2());
			setBodyPart(item.getBodyPart());
		}
	}

	public void setCount(long count)
	{
		this.count = count;
	}

	public void setType2(int type2)
	{
		this.type2 = type2;
	}

	public void setCustomType1(int customType1)
	{
		this.customType1 = customType1;
	}

	public void setEquipped(boolean isEquipped)
	{
		this.isEquipped = isEquipped;
	}

	public void setBodyPart(int bodyPart)
	{
		this.bodyPart = bodyPart;
	}

	public void setEnchantLevel(int enchantLevel)
	{
		this.enchantLevel = enchantLevel;
	}

	public void setCustomType2(int customType2)
	{
		this.customType2 = customType2;
	}

	public void setVariationStoneId(int val)
	{
		_variationStoneId = val;
	}

	public void setVariation1Id(int val)
	{
		_variation1Id = val;
	}

	public void setVariation2Id(int val)
	{
		_variation2Id = val;
	}

	public void setShadowLifeTime(int shadowLifeTime)
	{
		this.shadowLifeTime = shadowLifeTime;
	}

	public void setEquipSlot(int equipSlot)
	{
		this.equipSlot = equipSlot;
	}

	public void setTemporalLifeTime(int temporalLifeTime)
	{
		this.temporalLifeTime = temporalLifeTime;
	}

	public void setIsBlocked(boolean val)
	{
		_isBlocked = val;
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	public int getLastChange()
	{
		return lastChange;
	}

	public int getType1()
	{
		return type1;
	}

	public int getObjectId()
	{
		return objectId;
	}

	public int getItemId()
	{
		return itemId;
	}

	public long getCount()
	{
		return count;
	}

	public int getType2()
	{
		return type2;
	}

	public int getCustomType1()
	{
		return customType1;
	}

	public boolean isEquipped()
	{
		return isEquipped;
	}

	public int getBodyPart()
	{
		return bodyPart;
	}

	public int getEnchantLevel()
	{
		return enchantLevel;
	}

	public int getVariationStoneId()
	{
		return _variationStoneId;
	}

	public int getVariation1Id()
	{
		return _variation1Id;
	}

	public int getVariation2Id()
	{
		return _variation2Id;
	}

	public int getShadowLifeTime()
	{
		return shadowLifeTime;
	}

	public int getCustomType2()
	{
		return customType2;
	}

	public int getEquipSlot()
	{
		return equipSlot;
	}

	public int getTemporalLifeTime()
	{
		return temporalLifeTime;
	}

	public boolean isBlocked()
	{
		return _isBlocked;
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
		if(getObjectId() == 0)
			return getItemId() == ((ItemInfo) obj).getItemId();
		return getObjectId() == ((ItemInfo) obj).getObjectId();
	}

	@Override     
	public int hashCode()
	{
		int hash = getItemId();
		hash = 89 * hash + getObjectId();
		return hash;
    }

	public int[] getEnchantOptions()
	{
		return enchantOptions;
	}

	public void setEnchantOptions(int[] enchantOptions)
	{
		this.enchantOptions = enchantOptions;
	}

	public int getAttributeFire()
	{
		return _attrFire;
	}

	public void setAttributeFire(int val)
	{
		_attrFire = val;
	}

	public int getAttributeWater()
	{
		return _attrWater;
	}

	public void setAttributeWater(int val)
	{
		_attrWater = val;
	}

	public int getAttributeWind()
	{
		return _attrWind;
	}

	public void setAttributeWind(int val)
	{
		_attrWind = val;
	}

	public int getAttributeEarth()
	{
		return _attrEarth;
	}

	public void setAttributeEarth(int val)
	{
		_attrEarth = val;
	}

	public int getAttributeHoly()
	{
		return _attrHoly;
	}

	public void setAttributeHoly(int val)
	{
		_attrHoly = val;
	}

	public int getAttributeUnholy()
	{
		return _attrUnholy;
	}

	public void setAttributeUnholy(int val)
	{
		_attrUnholy = val;
	}

	private int[] getAttackElementInfo()
	{
		if(getItem().isWeapon())
		{
			if(_attrFire > 0)
				return new int[] { Element.FIRE.getId(), _attrFire };
			if(_attrWater > 0)
				return new int[] { Element.WATER.getId(), _attrWater };
			if(_attrWind > 0)
				return new int[] { Element.WIND.getId(), _attrWind };
			if(_attrEarth > 0)
				return new int[] { Element.EARTH.getId(), _attrEarth };
			if(_attrHoly > 0)
				return new int[] { Element.HOLY.getId(), _attrHoly };
			if(_attrUnholy > 0)
				return new int[] { Element.UNHOLY.getId(), _attrUnholy };
		}
		return new int[] { Element.NONE.getId(), 0 };
	}

	public int getAttackElement()
	{
		return getAttackElementInfo()[0];
	}

	public int getAttackElementValue()
	{
		return getAttackElementInfo()[1];
	}

	public int getDefenceFire()
	{
		return getItem().isWeapon() ? 0 : _attrFire;
	}

	public int getDefenceWater()
	{
		return getItem().isWeapon() ? 0 : _attrWater;
	}

	public int getDefenceWind()
	{
		return getItem().isWeapon() ? 0 : _attrWind;
	}

	public int getDefenceEarth()
	{
		return getItem().isWeapon() ? 0 : _attrEarth;
	}

	public int getDefenceHoly()
	{
		return getItem().isWeapon() ? 0 : _attrHoly;
	}

	public int getDefenceUnholy()
	{
		return getItem().isWeapon() ? 0 : _attrUnholy;
	}

	public Ensoul[] getNormalEnsouls()
	{
		return _normalEnsouls;
	}

	public void setNormalEnsouls(Ensoul[] ensouls)
	{
		_normalEnsouls = ensouls;
	}

	public Ensoul[] getSpecialEnsouls()
	{
		return _specialEnsouls;
	}

	public void setSpecialEnsouls(Ensoul[] ensouls)
	{
		_specialEnsouls = ensouls;
	}
}
