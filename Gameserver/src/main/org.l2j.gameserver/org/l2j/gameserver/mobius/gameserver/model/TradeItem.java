/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model;

import com.l2jmobius.gameserver.enums.AttributeType;
import com.l2jmobius.gameserver.model.ensoul.EnsoulOption;
import com.l2jmobius.gameserver.model.items.L2Item;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class TradeItem
{
	private int _objectId;
	private final L2Item _item;
	private final int _location;
	private int _enchant;
	private final int _type1;
	private final int _type2;
	private long _count;
	private long _storeCount;
	private long _price;
	private byte _elemAtkType;
	private int _elemAtkPower;
	private final int[] _elemDefAttr =
	{
		0,
		0,
		0,
		0,
		0,
		0
	};
	private final int[] _enchantOptions;
	private Collection<EnsoulOption> _soulCrystalOptions;
	private Collection<EnsoulOption> _soulCrystalSpecialOptions;
	private int _visualId;
	private int _augmentationOption1 = -1;
	private int _augmentationOption2 = -1;
	
	public TradeItem(L2ItemInstance item, long count, long price)
	{
		Objects.requireNonNull(item);
		_objectId = item.getObjectId();
		_item = item.getItem();
		_location = item.getLocationSlot();
		_enchant = item.getEnchantLevel();
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		_count = count;
		_price = price;
		_elemAtkType = item.getAttackAttributeType().getClientId();
		_elemAtkPower = item.getAttackAttributePower();
		for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
		{
			_elemDefAttr[type.getClientId()] = item.getDefenceAttribute(type);
		}
		_enchantOptions = item.getEnchantOptions();
		_soulCrystalOptions = item.getSpecialAbilities();
		_soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
		_visualId = item.getVisualId();
		
		if (item.getAugmentation() != null)
		{
			_augmentationOption1 = item.getAugmentation().getOption1Id();
			_augmentationOption1 = item.getAugmentation().getOption2Id();
		}
	}
	
	public TradeItem(L2Item item, long count, long price)
	{
		Objects.requireNonNull(item);
		_objectId = 0;
		_item = item;
		_location = 0;
		_enchant = 0;
		_type1 = 0;
		_type2 = 0;
		_count = count;
		_storeCount = count;
		_price = price;
		_elemAtkType = AttributeType.NONE.getClientId();
		_elemAtkPower = 0;
		_enchantOptions = L2ItemInstance.DEFAULT_ENCHANT_OPTIONS;
		_soulCrystalOptions = Collections.emptyList();
		_soulCrystalSpecialOptions = Collections.emptyList();
	}
	
	public TradeItem(TradeItem item, long count, long price)
	{
		Objects.requireNonNull(item);
		_objectId = item.getObjectId();
		_item = item.getItem();
		_location = item.getLocationSlot();
		_enchant = item.getEnchant();
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		_count = count;
		_storeCount = count;
		_price = price;
		_elemAtkType = item.getAttackElementType();
		_elemAtkPower = item.getAttackElementPower();
		for (byte i = 0; i < 6; i++)
		{
			_elemDefAttr[i] = item.getElementDefAttr(i);
		}
		_enchantOptions = item.getEnchantOptions();
		_soulCrystalOptions = item.getSoulCrystalOptions();
		_soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
		_visualId = item.getVisualId();
	}
	
	public void setObjectId(int objectId)
	{
		_objectId = objectId;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public L2Item getItem()
	{
		return _item;
	}
	
	public int getLocationSlot()
	{
		return _location;
	}
	
	public void setEnchant(int enchant)
	{
		_enchant = enchant;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public void setCount(long count)
	{
		_count = count;
	}
	
	public long getCount()
	{
		return _count;
	}
	
	public long getStoreCount()
	{
		return _storeCount;
	}
	
	public void setPrice(long price)
	{
		_price = price;
	}
	
	public long getPrice()
	{
		return _price;
	}
	
	public void setAttackElementType(AttributeType attackElement)
	{
		_elemAtkType = attackElement.getClientId();
	}
	
	public byte getAttackElementType()
	{
		return _elemAtkType;
	}
	
	public void setAttackElementPower(int attackElementPower)
	{
		_elemAtkPower = attackElementPower;
	}
	
	public int getAttackElementPower()
	{
		return _elemAtkPower;
	}
	
	public void setElementDefAttr(AttributeType element, int value)
	{
		_elemDefAttr[element.getClientId()] = value;
	}
	
	public int getElementDefAttr(byte i)
	{
		return _elemDefAttr[i];
	}
	
	public int[] getEnchantOptions()
	{
		return _enchantOptions;
	}
	
	public void setSoulCrystalOptions(Collection<EnsoulOption> soulCrystalOptions)
	{
		_soulCrystalOptions = soulCrystalOptions;
	}
	
	public Collection<EnsoulOption> getSoulCrystalOptions()
	{
		return _soulCrystalOptions == null ? Collections.emptyList() : _soulCrystalOptions;
	}
	
	public void setSoulCrystalSpecialOptions(Collection<EnsoulOption> soulCrystalSpecialOptions)
	{
		_soulCrystalSpecialOptions = soulCrystalSpecialOptions;
	}
	
	public Collection<EnsoulOption> getSoulCrystalSpecialOptions()
	{
		return _soulCrystalSpecialOptions == null ? Collections.emptyList() : _soulCrystalSpecialOptions;
	}
	
	public void setAugmentation(int option1, int option2)
	{
		_augmentationOption1 = option1;
		_augmentationOption2 = option2;
	}
	
	public int getAugmentationOption1()
	{
		return _augmentationOption1;
	}
	
	public int getAugmentationOption2()
	{
		return _augmentationOption2;
	}
	
	public void setVisualId(int visualItemId)
	{
		_visualId = visualItemId;
	}
	
	public int getVisualId()
	{
		return _visualId;
	}
}
