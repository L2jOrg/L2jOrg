package org.l2j.gameserver.model.items.listeners;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ArmorSetsHolder;
import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.skills.SkillEntry;

public final class ArmorSetListener implements OnEquipListener
{
	private static final ArmorSetListener _instance = new ArmorSetListener();

	public static ArmorSetListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		// checks if there is armorset for chest item that player worns
		List<ArmorSet> armorSets = ArmorSetsHolder.getInstance().getArmorSets(item.getItemId());
		if(armorSets == null || armorSets.isEmpty())
			return;

		Player player = (Player) actor;

		boolean update = false;
		for(ArmorSet armorSet : armorSets)
		{
			// checks if equipped item is part of set
			if(armorSet.containItem(slot, item.getItemId()))
			{
				List<SkillEntry> skills = armorSet.getSkills(armorSet.getEquipedSetPartsCount(player));
				for(SkillEntry skillEntry : skills)
				{
					player.addSkill(skillEntry, false);
					update = true;
				}

				if(armorSet.containAll(player))
				{
					if(armorSet.containShield(player)) // has shield from set
					{
						skills = armorSet.getShieldSkills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}

					int enchantLevel = armorSet.getEnchantLevel(player);
					if(enchantLevel >= 6) // has all parts of set enchanted to 6 or more
					{
						skills = armorSet.getEnchant6skills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}
					if(enchantLevel >= 7) // has all parts of set enchanted to 7 or more
					{
						skills = armorSet.getEnchant7skills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}
					if(enchantLevel >= 8) // has all parts of set enchanted to 8 or more
					{
						skills = armorSet.getEnchant8skills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}
					if(enchantLevel >= 9)
					{
						skills = armorSet.getEnchant9skills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}
					if(enchantLevel >= 10)
					{
						skills = armorSet.getEnchant10skills();
						for(SkillEntry skillEntry : skills)
						{
							player.addSkill(skillEntry, false);
							update = true;
						}
					}
					player.setArmorSetEnchant(enchantLevel);
				}
			}
			else if(armorSet.containShield(item.getItemId()) && armorSet.containAll(player))
			{
				List<SkillEntry> skills = armorSet.getShieldSkills();
				for(SkillEntry skillEntry : skills)
				{
					player.addSkill(skillEntry, false);
					update = true;
				}
			}
		}

		if(update)
		{
			player.sendSkillList();
			player.updateStats();
		}
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		List<ArmorSet> armorSets = ArmorSetsHolder.getInstance().getArmorSets(item.getItemId());
		if(armorSets == null || armorSets.isEmpty())
			return;

		Player player = (Player) actor;

		boolean update = false;
		for(ArmorSet armorSet : armorSets)
		{
			boolean remove = false;
			boolean setPartUneqip = false;
			List<SkillEntry> removeSkillId1 = new ArrayList<SkillEntry>(); // set skill
			List<SkillEntry> removeSkillId2 = new ArrayList<SkillEntry>(); // shield skill
			List<SkillEntry> removeSkillId3 = new ArrayList<SkillEntry>(); // enchant +6 skill
			List<SkillEntry> removeSkillId4 = new ArrayList<SkillEntry>(); // enchant +7 skill
			List<SkillEntry> removeSkillId5 = new ArrayList<SkillEntry>(); // enchant +8 skill

			if(armorSet.containItem(slot, item.getItemId())) // removed part of set
			{
				remove = true;
				setPartUneqip = true;
				removeSkillId1 = armorSet.getSkillsToRemove();
				removeSkillId2 = armorSet.getShieldSkills();
				removeSkillId3 = armorSet.getEnchant6skills();
				removeSkillId4 = armorSet.getEnchant7skills();
				removeSkillId5 = armorSet.getEnchant8skills();
			}
			else if(armorSet.containShield(item.getItemId())) // removed shield
			{
				remove = true;
				removeSkillId2 = armorSet.getShieldSkills();
			}

			if(remove)
			{
				for(SkillEntry skillEntry : removeSkillId1)
				{
					player.removeSkill(skillEntry, false);
					update = true;
				}
				for(SkillEntry skillEntry : removeSkillId2)
				{
					player.removeSkill(skillEntry);
					update = true;
				}
				for(SkillEntry skillEntry : removeSkillId3)
				{
					player.removeSkill(skillEntry);
					update = true;
				}
				for(SkillEntry skillEntry : removeSkillId4)
				{
					player.removeSkill(skillEntry);
					update = true;
				}
				for(SkillEntry skillEntry : removeSkillId5)
				{
					player.removeSkill(skillEntry);
					update = true;
				}
				player.setArmorSetEnchant(0);
			}

			List<SkillEntry> skills = armorSet.getSkills(armorSet.getEquipedSetPartsCount(player));
			for(SkillEntry skillEntry : skills)
			{
				player.addSkill(skillEntry, false);
				update = true;
			}
		}

		if(update)
		{
			player.sendSkillList();
			player.updateStats();
		}
	}
}