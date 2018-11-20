package org.l2j.gameserver.model.items.listeners;

import org.l2j.gameserver.data.xml.holder.OptionDataHolder;
import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.stats.triggers.TriggerInfo;
import org.l2j.gameserver.templates.OptionDataTemplate;

/**
 * @author VISTALL
 * @date 19:34/19.05.2011
 */
public final class ItemEnchantOptionsListener implements OnEquipListener
{
	private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();

	public static ItemEnchantOptionsListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		Player player = actor.getPlayer();

		boolean updateStats = false;
		boolean sendSkillList = false;
		for(int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if(template == null)
				continue;

			if(player.addOptionData(template) == template)
				continue;

			updateStats = true;

			if(!template.getSkills().isEmpty())
				sendSkillList = true;
		}

		if(updateStats)
		{
			if(sendSkillList)
				player.sendSkillList();
			player.sendChanges();
		}
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		Player player = actor.getPlayer();

		boolean updateStats = false;
		boolean sendSkillList = false;
		for(int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = player.removeOptionData(i);
			if(template == null)
				continue;

			updateStats = true;

			if(!template.getSkills().isEmpty())
				sendSkillList = true;
		}

		if(updateStats)
		{
			if(sendSkillList)
				player.sendSkillList();
			player.updateStats();
		}
	}
}
