package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.PetInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.utils.ItemFunctions;

import java.util.Arrays;

/**
 * @author VISTALL
 * @date 7:34/17.03.2011
 */
public class SkillsItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer() && !playable.isPet())
			return false;

		//TODO: [Bonux] Тупая заглушка...
		if(playable.isPet())
		{
			PetInstance pet = (PetInstance) playable;
			if(!pet.isMyFeed(item.getItemId()) && Arrays.binarySearch(Config.ALT_ALLOWED_PET_POTIONS, item.getItemId()) < 0)
			{
				//TODO: Вынести все в другое правильное место.
				if(pet.getPlayer() != null)
					pet.getPlayer().sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
				return false;
			}
		}

		SkillEntry[] skills = item.getTemplate().getAttachedSkills();
		for(int i = 0; i < skills.length; i++)
		{
			SkillEntry skillEntry = skills[i];
			Skill skill = skillEntry.getTemplate();
			Creature aimingTarget = skill.getAimingTarget(playable, playable.getTarget());
			boolean sendMessage = false;
			if(skill.checkCondition(playable, aimingTarget, ctrl, false, true))
			{
				if(!playable.getAI().Cast(skill, aimingTarget, ctrl, false))
					return false;

				if(!skill.altUse())
					sendMessage = true;
			}
			else if(i == 0) //FIXME [VISTALL] всегда первый скил идет вместо конда?
				return false;

			if(reduceAfterUse())
				ItemFunctions.deleteItem(playable, item, 1, sendMessage);
		}

		return true;
	}

	public boolean reduceAfterUse()
	{
		return false;
	}
}