package org.l2j.gameserver.model.items.listeners;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.support.Ensoul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemSkillsListener implements OnEquipListener
{
	private static final ItemSkillsListener _instance = new ItemSkillsListener();

	public static ItemSkillsListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		Player player = (Player) actor;

		ItemTemplate it = item.getTemplate();

		List<SkillEntry> itemSkills = new ArrayList<SkillEntry>();

		itemSkills.addAll(Arrays.asList(it.getAttachedSkills()));

		SkillEntry enchant4Skill = it.getEnchant4Skill();
		if(enchant4Skill != null)
			itemSkills.add(enchant4Skill);

		for(Ensoul ensoul : item.getNormalEnsouls())
			itemSkills.addAll(ensoul.getSkills());

		for(Ensoul ensoul : item.getSpecialEnsouls())
			itemSkills.addAll(ensoul.getSkills());

		player.removeTriggers(it);

		if(!itemSkills.isEmpty())
		{
			if(it.getItemType() == EtcItemType.RUNE_SELECT)
			{
				for(SkillEntry itemSkillEntry : itemSkills)
				{
					int level = player.getSkillLevel(itemSkillEntry.getId());
					int newlevel = level - 1;
					if(newlevel > 0)
						player.addSkill(SkillHolder.getInstance().getSkillEntry(itemSkillEntry.getId(), newlevel), false);
					else
						player.removeSkillById(itemSkillEntry.getId());
				}
			}
			else
			{
				for(SkillEntry itemSkillEntry : itemSkills)
					player.removeSkill(itemSkillEntry, false);
			}

			player.sendSkillList();
			player.updateStats();
		}
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		Player player = (Player) actor;

		ItemTemplate it = item.getTemplate();

		List<SkillEntry> itemSkills = new ArrayList<SkillEntry>();

		itemSkills.addAll(Arrays.asList(it.getAttachedSkills()));

		if(item.getFixedEnchantLevel(player) >= 4)
		{
			SkillEntry enchant4Skill = it.getEnchant4Skill();
			if(enchant4Skill != null)
				itemSkills.add(enchant4Skill);
		}
		for(Ensoul ensoul : item.getNormalEnsouls())
			itemSkills.addAll(ensoul.getSkills());

		for(Ensoul ensoul : item.getSpecialEnsouls())
			itemSkills.addAll(ensoul.getSkills());

		// Для оружия при несоотвествии грейда скилы не выдаем
		if(it.getType2() == ItemTemplate.TYPE2_WEAPON && player.getWeaponsExpertisePenalty() > 0)
			return;

		player.addTriggers(it);

		if(!itemSkills.isEmpty())
		{
			if(it.getItemType() == EtcItemType.RUNE_SELECT)
			{
				for(SkillEntry itemSkillEntry : itemSkills)
				{
					int level = player.getSkillLevel(itemSkillEntry.getId());
					int newlevel = level;
					if(level > 0)
					{
						if(SkillHolder.getInstance().getSkill(itemSkillEntry.getId(), level + 1) != null)
							newlevel = level + 1;
					}
					else
						newlevel = 1;

					if(newlevel != level)
						player.addSkill(SkillHolder.getInstance().getSkillEntry(itemSkillEntry.getId(), newlevel), false);
				}
			}
			else
			{
				for(SkillEntry itemSkillEntry : itemSkills)
				{
					if(player.getSkillLevel(itemSkillEntry.getId()) < itemSkillEntry.getLevel())
					{
						player.addSkill(itemSkillEntry, false);

						Skill itemSkill = itemSkillEntry.getTemplate();
						if(itemSkill.isActive())
						{
							if(!player.isSkillDisabled(itemSkill))
							{
								long reuseDelay = Formulas.calcSkillReuseDelay(player, itemSkill);
								reuseDelay = Math.min(reuseDelay, 30000);

								if(reuseDelay > 0)
									player.disableSkill(itemSkill, reuseDelay);
							}
						}
					}
				}
			}

			player.sendSkillList();
			player.updateStats();
		}
	}
}