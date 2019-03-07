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
package org.l2j.gameserver.mobius.gameserver.model.items.appearance;

import com.l2jmobius.gameserver.datatables.ItemTable;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.AppearanceHolder;
import com.l2jmobius.gameserver.model.items.L2Item;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.items.type.ArmorType;
import com.l2jmobius.gameserver.model.items.type.CrystalType;
import com.l2jmobius.gameserver.model.items.type.WeaponType;
import com.l2jmobius.gameserver.network.SystemMessageId;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid
 */
public class AppearanceStone
{
	private final int _id;
	private final int _cost;
	private final int _visualId;
	private final long _lifeTime;
	private final AppearanceType _type;
	private final WeaponType _weaponType;
	private final ArmorType _armorType;
	private final AppearanceHandType _handType;
	private final AppearanceMagicType _magicType;
	private List<CrystalType> _crystalTypes;
	private List<AppearanceTargetType> _targetTypes;
	private List<Long> _bodyParts;
	private List<Race> _races;
	private List<Race> _racesNot;
	private List<AppearanceHolder> _allVisualIds;
	
	public AppearanceStone(StatsSet set)
	{
		_id = set.getInt("id");
		_visualId = set.getInt("visualId", 0);
		_cost = set.getInt("cost", 0);
		_lifeTime = set.getDuration("lifeTime", Duration.ofSeconds(0)).toMillis();
		_type = set.getEnum("type", AppearanceType.class, AppearanceType.NONE);
		_weaponType = set.getEnum("weaponType", WeaponType.class, WeaponType.NONE);
		_armorType = set.getEnum("armorType", ArmorType.class, ArmorType.NONE);
		_handType = set.getEnum("handType", AppearanceHandType.class, AppearanceHandType.NONE);
		_magicType = set.getEnum("magicType", AppearanceMagicType.class, AppearanceMagicType.NONE);
		
		final AppearanceTargetType targetType = set.getEnum("targetType", AppearanceTargetType.class, AppearanceTargetType.NONE);
		if (targetType != AppearanceTargetType.NONE)
		{
			addTargetType(targetType);
		}
		
		// No grade items cannot change appearance, because client doesn't have No-Grade restoration stones.
		final CrystalType crystalType = set.getEnum("grade", CrystalType.class, null);
		
		// If no crystal type is defined, we must add all defaults.
		if (crystalType == null)
		{
			switch (targetType)
			{
				case ACCESSORY:
				case ALL:
				{
					addCrystalType(CrystalType.NONE);
				}
				case WEAPON:
				case ARMOR:
				{
					for (CrystalType cryType : CrystalType.values())
					{
						if ((cryType != CrystalType.NONE) && (cryType != CrystalType.EVENT))
						{
							addCrystalType(cryType);
						}
					}
				}
			}
		}
		else
		{
			addCrystalType(crystalType);
		}
		
		final long bodyPart = ItemTable.SLOTS.get(set.getString("bodyPart", "none"));
		if (bodyPart != L2Item.SLOT_NONE)
		{
			addBodyPart(bodyPart);
		}
		
		final Race race = set.getEnum("race", Race.class, Race.NONE);
		if (race != Race.NONE)
		{
			addRace(race);
		}
		
		final Race raceNot = set.getEnum("raceNot", Race.class, Race.NONE);
		if (raceNot != Race.NONE)
		{
			addRaceNot(raceNot);
		}
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getVisualId()
	{
		return _visualId;
	}
	
	public int getCost()
	{
		return _cost;
	}
	
	public long getLifeTime()
	{
		return _lifeTime;
	}
	
	public AppearanceType getType()
	{
		return _type;
	}
	
	public WeaponType getWeaponType()
	{
		return _weaponType;
	}
	
	public ArmorType getArmorType()
	{
		return _armorType;
	}
	
	public AppearanceHandType getHandType()
	{
		return _handType;
	}
	
	public AppearanceMagicType getMagicType()
	{
		return _magicType;
	}
	
	public void addCrystalType(CrystalType type)
	{
		if (_crystalTypes == null)
		{
			_crystalTypes = new ArrayList<>();
		}
		_crystalTypes.add(type);
	}
	
	public List<CrystalType> getCrystalTypes()
	{
		return _crystalTypes != null ? _crystalTypes : Collections.emptyList();
	}
	
	public void addTargetType(AppearanceTargetType type)
	{
		if (_targetTypes == null)
		{
			_targetTypes = new ArrayList<>();
		}
		_targetTypes.add(type);
	}
	
	public List<AppearanceTargetType> getTargetTypes()
	{
		return _targetTypes != null ? _targetTypes : Collections.emptyList();
	}
	
	public void addBodyPart(long part)
	{
		if (_bodyParts == null)
		{
			_bodyParts = new ArrayList<>();
		}
		_bodyParts.add(part);
	}
	
	public void addVisualId(AppearanceHolder appearanceHolder)
	{
		if (_allVisualIds == null)
		{
			_allVisualIds = new ArrayList<>();
		}
		_allVisualIds.add(appearanceHolder);
	}
	
	public List<AppearanceHolder> getVisualIds()
	{
		return _allVisualIds != null ? _allVisualIds : Collections.emptyList();
	}
	
	public List<Long> getBodyParts()
	{
		return _bodyParts != null ? _bodyParts : Collections.emptyList();
	}
	
	public void addRace(Race race)
	{
		if (_races == null)
		{
			_races = new ArrayList<>();
		}
		_races.add(race);
	}
	
	public List<Race> getRaces()
	{
		return _races != null ? _races : Collections.emptyList();
	}
	
	public void addRaceNot(Race race)
	{
		if (_racesNot == null)
		{
			_racesNot = new ArrayList<>();
		}
		_racesNot.add(race);
	}
	
	public List<Race> getRacesNot()
	{
		return _racesNot != null ? _racesNot : Collections.emptyList();
	}
	
	/**
	 * @param player the actor requesting to use this appearance.
	 * @param targetItem the item to be modified with this appearance.
	 * @return {@code true} if the item is valid for appearance change, {@code false} otherwise.
	 */
	public boolean checkConditions(L2PcInstance player, L2ItemInstance targetItem)
	{
		if (targetItem == null)
		{
			return false;
		}
		
		if (getTargetTypes().isEmpty())
		{
			return false;
		}
		
		if (targetItem.isEquipped() && (getRacesNot().contains(player.getRace()) || (!getRaces().isEmpty() && !getRaces().contains(player.getRace()))))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_AN_EQUIPPED_ITEM_INTO_THE_APPEARANCE_OF_AN_UNEQUIPPABLE_ITEM_PLEASE_CHECK_RACE_GENDER_RESTRICTIONS_YOU_CAN_MODIFY_THE_APPEARANCE_IF_YOU_UNEQUIP_THE_ITEM);
			return false;
		}
		
		switch (_type)
		{
			case RESTORE:
			{
				if (targetItem.getVisualId() == 0)
				{
					player.sendPacket(SystemMessageId.YOU_CANNOT_RESTORE_ITEMS_THAT_HAVE_NOT_BEEN_MODIFIED);
					return false;
				}
				
				if ((targetItem.isWeapon() && !getTargetTypes().contains(AppearanceTargetType.WEAPON)) || (targetItem.isArmor() && !getTargetTypes().contains(AppearanceTargetType.ARMOR)) || (targetItem.isEtcItem() && !getTargetTypes().contains(AppearanceTargetType.ACCESSORY)))
				{
					player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
					return false;
				}
				
				if (((targetItem.getItem().getBodyPart() == L2Item.SLOT_HAIR) || (targetItem.getItem().getBodyPart() == L2Item.SLOT_HAIR2) || (targetItem.getItem().getBodyPart() == L2Item.SLOT_HAIRALL)) && !getTargetTypes().contains(AppearanceTargetType.ACCESSORY))
				{
					player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
					return false;
				}
				
				break;
			}
			default:
			{
				// Seems like in retail item with already changed appearance, can be changed again without being restored.
				
				final AppearanceTargetType targetType = getTargetTypes().get(0);
				switch (targetType)
				{
					case NONE:
					{
						return false;
					}
					case WEAPON:
					{
						if (!targetItem.isWeapon())
						{
							player.sendPacket(SystemMessageId.WEAPONS_ONLY);
							return false;
						}
						
						if (targetItem.getItem().getCrystalType() == CrystalType.NONE)
						{
							player.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_OR_RESTORE_NO_GRADE_ITEMS);
							return false;
						}
						
						break;
					}
					case ARMOR:
					{
						if (!targetItem.isArmor())
						{
							player.sendPacket(SystemMessageId.ARMOR_ONLY);
							return false;
						}
						
						if (targetItem.getItem().getCrystalType() == CrystalType.NONE)
						{
							player.sendPacket(SystemMessageId.YOU_CANNOT_MODIFY_OR_RESTORE_NO_GRADE_ITEMS);
							return false;
						}
						
						break;
					}
					case ACCESSORY:
					{
						if ((targetItem.getItem().getBodyPart() != L2Item.SLOT_HAIR) && (targetItem.getItem().getBodyPart() != L2Item.SLOT_HAIR2) && (targetItem.getItem().getBodyPart() != L2Item.SLOT_HAIRALL))
						{
							player.sendPacket(SystemMessageId.HAIR_ACCESSORIES_ONLY);
							return false;
						}
						break;
					}
					case ALL:
					{
						if (!getCrystalTypes().isEmpty() && !getCrystalTypes().contains(targetItem.getItem().getCrystalType()))
						{
							player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
							return false;
						}
						
						if (findVisualChange(targetItem) == null)
						{
							player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
							return false;
						}
						return true;
					}
				}
				
				break;
			}
		}
		
		if (!getCrystalTypes().isEmpty() && !getCrystalTypes().contains(targetItem.getItem().getCrystalType()))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
			return false;
		}
		
		if (targetItem.isArmor() && !getBodyParts().isEmpty() && !getBodyParts().contains(targetItem.getItem().getBodyPart()))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
			return false;
		}
		
		if (_weaponType != WeaponType.NONE)
		{
			if (!targetItem.isWeapon() || (targetItem.getItemType() != _weaponType))
			{
				player.sendPacket(SystemMessageId.THIS_ITEM_CANNOT_BE_USED_AS_AN_APPEARANCE_WEAPON);
				return false;
			}
			
			switch (_handType)
			{
				case ONE_HANDED:
				{
					if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_R_HAND) != L2Item.SLOT_R_HAND)
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
					break;
				}
				case TWO_HANDED:
				{
					if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_LR_HAND) != L2Item.SLOT_LR_HAND)
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
					break;
				}
			}
			
			switch (_magicType)
			{
				case MAGICAL:
				{
					if (!targetItem.getItem().isMagicWeapon())
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
					break;
				}
				case PHYISICAL:
				{
					if (targetItem.getItem().isMagicWeapon())
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
				}
			}
		}
		
		if (_armorType != ArmorType.NONE)
		{
			switch (_armorType)
			{
				case SHIELD:
				{
					if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SHIELD))
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
					break;
				}
				case SIGIL:
				{
					if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SIGIL))
					{
						player.sendPacket(SystemMessageId.THIS_ITEM_DOES_NOT_MEET_REQUIREMENTS);
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public AppearanceHolder findVisualChange(L2ItemInstance targetItem)
	{
		for (AppearanceHolder holder : _allVisualIds)
		{
			if (targetItem.isArmor() && (holder.getBodyPart() != 0) && (targetItem.getItem().getBodyPart() != holder.getBodyPart()))
			{
				continue;
			}
			
			if (holder.getWeaponType() != WeaponType.NONE)
			{
				if (!targetItem.isWeapon() || (targetItem.getItemType() != holder.getWeaponType()))
				{
					continue;
				}
				
				switch (holder.getHandType())
				{
					case ONE_HANDED:
					{
						if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_R_HAND) != L2Item.SLOT_R_HAND)
						{
							continue;
						}
						break;
					}
					case TWO_HANDED:
					{
						if ((targetItem.getItem().getBodyPart() & L2Item.SLOT_LR_HAND) != L2Item.SLOT_LR_HAND)
						{
							continue;
						}
						break;
					}
				}
				
				switch (holder.getMagicType())
				{
					case MAGICAL:
					{
						if (!targetItem.getItem().isMagicWeapon())
						{
							continue;
						}
						break;
					}
					case PHYISICAL:
					{
						if (targetItem.getItem().isMagicWeapon())
						{
							continue;
						}
					}
				}
			}
			
			if (holder.getArmorType() != ArmorType.NONE)
			{
				switch (holder.getArmorType())
				{
					case SHIELD:
					{
						if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SHIELD))
						{
							continue;
						}
						break;
					}
					case SIGIL:
					{
						if (!targetItem.isArmor() || (targetItem.getItemType() != ArmorType.SIGIL))
						{
							continue;
						}
					}
				}
			}
			return holder;
		}
		return null;
	}
}
