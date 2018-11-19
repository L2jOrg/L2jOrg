package l2s.gameserver.handler.items.impl;

import l2s.gameserver.skills.SkillEntry;
import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.ItemFunctions;

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
			if(!pet.isMyFeed(item.getItemId()) && !ArrayUtils.contains(Config.ALT_ALLOWED_PET_POTIONS, item.getItemId()))
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