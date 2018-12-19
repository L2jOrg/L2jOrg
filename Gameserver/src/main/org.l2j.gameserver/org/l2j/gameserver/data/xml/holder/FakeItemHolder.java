package org.l2j.gameserver.data.xml.holder;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.TroveUtils;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.l2j.gameserver.templates.item.ItemGrade;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FakeItemHolder extends AbstractHolder
{
	private static class ClassWeaponAndArmor
	{
		private final int classId;
		private final List<WeaponType> weaponTypes = new ArrayList<WeaponType>();
		private final List<ArmorType> armorTypes = new ArrayList<ArmorType>();

		public ClassWeaponAndArmor(int classId, String weaponTypes, String armorTypes)
		{
			this.classId = classId;
			for(String s : weaponTypes.split(";"))
				this.weaponTypes.add(WeaponType.valueOf(s));

			for(String s : armorTypes.split(";"))
				this.armorTypes.add(ArmorType.valueOf(s));
		}

		public WeaponType getRandomWeaponType()
		{
			return weaponTypes.get(Rnd.get(weaponTypes.size()));
		}

		public ArmorType getRandomArmorType()
		{
			return armorTypes.get(Rnd.get(armorTypes.size()));
		}
	}

	private static FakeItemHolder ourInstance = new FakeItemHolder();

	public static FakeItemHolder getInstance()
	{
		return ourInstance;
	}

	private final Map<ItemGrade, Map<WeaponType, TIntList>> weapons = new HashMap<>();
	private final Map<ItemGrade, Map<ArmorType, TIntList>> armors = new HashMap<>();
	private final Map<ItemGrade, List<TIntList>> accessorys = new HashMap<>();
	private final Map<Integer, ClassWeaponAndArmor> classWeaponAndArmors = new HashMap<>();
	private final TIntList _hairAccessories = new TIntArrayList();
	private final TIntList _cloaks = new TIntArrayList();

	public void addWeapons(ItemGrade grade, TIntList list)
	{
		Map<WeaponType, TIntList> map = new HashMap<>();
		for(int itemId : list.toArray())
		{
			ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
			if(template == null || !template.isWeapon())
				continue;

			WeaponType weaponType = ((WeaponTemplate) template).getItemType();
			if(template.isMagicWeapon())
				weaponType = WeaponTemplate.WeaponType.MAGIC;

			if(map.get(weaponType) == null)
				map.put(weaponType, new TIntArrayList());

			map.get(weaponType).add(itemId);
		}
		weapons.put(grade, map);
	}

	public void addArmors(ItemGrade grade, Map<ArmorType, TIntList> map)
	{
		armors.put(grade, map);
	}

	public void addAccessorys(ItemGrade grade, List<TIntList> list)
	{
		accessorys.put(grade, list);
	}

	public void addClassWeaponAndArmors(int classId, String weaponTypes, String armorTypes)
	{
		classWeaponAndArmors.put(classId, new ClassWeaponAndArmor(classId, weaponTypes, armorTypes));
	}

	public void addHairAccessories(TIntList list)
	{
		_hairAccessories.addAll(list);
	}

	public TIntList getHairAccessories()
	{
		return _hairAccessories;
	}

	public void addCloaks(TIntList list)
	{
		_cloaks.addAll(list);
	}

	public TIntList getCloaks()
	{
		return _cloaks;
	}

	public TIntList getRandomItems(Player player, String type, int expertiseIndex)
	{
		ItemGrade grade = ItemGrade.values()[expertiseIndex];

		switch(type)
		{
			case "Accessory":
			{
				List<TIntList> packs = accessorys.get(grade);
				return packs.get(Rnd.get(packs.size()));
			}
			case "Armor":
			{
				try
				{
					ClassWeaponAndArmor classWeaponAndArmor = classWeaponAndArmors.get(player.getClassId().getId());
					return armors.get(grade).get(classWeaponAndArmor.getRandomArmorType());
				}
				catch(Exception e)
				{
					System.out.println(player.getClassId().getId());
					break;
				}
			}
			case "Weapon":
			{
				ClassWeaponAndArmor classWeaponAndArmor = classWeaponAndArmors.get(player.getClassId().getId());
				TIntList weaponIds = new TIntArrayList();
				while(weaponIds.isEmpty())
				{
					TIntList list = weapons.get(grade).get(classWeaponAndArmor.getRandomWeaponType());
					if(list != null)
						weaponIds.add(list.get(list.size() - 1));
				}
				return weaponIds;
			}
		}

		return TroveUtils.EMPTY_INT_ARRAY_LIST;
	}

	@Override
	public void log()
	{
		logger.info("loaded fake items.");
	}

	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{}
}