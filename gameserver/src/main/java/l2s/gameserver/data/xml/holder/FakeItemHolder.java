package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

import org.napile.primitive.Containers;
import org.napile.primitive.lists.IntList;
import org.napile.primitive.lists.impl.ArrayIntList;


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

	private final Map<ItemGrade, Map<WeaponType, IntList>> weapons = new HashMap<ItemGrade, Map<WeaponType, IntList>>();
	private final Map<ItemGrade, Map<ArmorType, IntList>> armors = new HashMap<ItemGrade, Map<ArmorType, IntList>>();
	private final Map<ItemGrade, List<IntList>> accessorys = new HashMap<ItemGrade, List<IntList>>();
	private final Map<Integer, ClassWeaponAndArmor> classWeaponAndArmors = new HashMap<Integer, ClassWeaponAndArmor>();
	private final IntList _hairAccessories = new ArrayIntList();
	private final IntList _cloaks = new ArrayIntList();

	public void addWeapons(ItemGrade grade, IntList list)
	{
		Map<WeaponType, IntList> map = new HashMap<WeaponType, IntList>();
		for(int itemId : list.toArray())
		{
			ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
			if(template == null || !template.isWeapon())
				continue;

			WeaponType weaponType = ((WeaponTemplate) template).getItemType();
			if(template.isMagicWeapon())
				weaponType = WeaponTemplate.WeaponType.MAGIC;

			if(map.get(weaponType) == null)
				map.put(weaponType, new ArrayIntList());

			map.get(weaponType).add(itemId);
		}
		weapons.put(grade, map);
	}

	public void addArmors(ItemGrade grade, Map<ArmorType, IntList> map)
	{
		armors.put(grade, map);
	}

	public void addAccessorys(ItemGrade grade, List<IntList> list)
	{
		accessorys.put(grade, list);
	}

	public void addClassWeaponAndArmors(int classId, String weaponTypes, String armorTypes)
	{
		classWeaponAndArmors.put(classId, new ClassWeaponAndArmor(classId, weaponTypes, armorTypes));
	}

	public void addHairAccessories(IntList list)
	{
		_hairAccessories.addAll(list);
	}

	public IntList getHairAccessories()
	{
		return _hairAccessories;
	}

	public void addCloaks(IntList list)
	{
		_cloaks.addAll(list);
	}

	public IntList getCloaks()
	{
		return _cloaks;
	}

	public IntList getRandomItems(Player player, String type, int expertiseIndex)
	{
		ItemGrade grade = ItemGrade.values()[expertiseIndex];

		switch(type)
		{
			case "Accessory":
			{
				List<IntList> packs = accessorys.get(grade);
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
				IntList weaponIds = new ArrayIntList();
				while(weaponIds.isEmpty())
				{
					IntList list = weapons.get(grade).get(classWeaponAndArmor.getRandomWeaponType());
					if(list != null)
						weaponIds.add(list.get(list.size() - 1));
				}
				return weaponIds;
			}
		}

		return Containers.EMPTY_INT_LIST;
	}

	@Override
	public void log()
	{
		info("loaded fake items.");
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