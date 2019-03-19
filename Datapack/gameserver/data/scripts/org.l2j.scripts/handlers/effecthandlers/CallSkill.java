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
package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * Call Skill effect implementation.
 * @author NosBit
 */
public final class CallSkill extends AbstractEffect
{
	private final SkillHolder _skill;
	private final int _skillLevelScaleTo;
	
	public CallSkill(StatsSet params)
	{
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1), params.getInt("skillSubLevel", 0));
		_skillLevelScaleTo = params.getInt("skillLevelScaleTo", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		Skill triggerSkill = null;
		if (_skillLevelScaleTo <= 0)
		{
			// Mobius: Use 0 to trigger max effector learned skill level.
			if (_skill.getSkillLevel() == 0)
			{
				final int knownLevel = effector.getSkillLevel(_skill.getSkillId());
				if (knownLevel > 0)
				{
					triggerSkill = SkillData.getInstance().getSkill(_skill.getSkillId(), knownLevel, _skill.getSkillSubLevel());
				}
				else
				{
					LOGGER.warn("Player " + effector + " called unknown skill " + _skill + " triggered by " + skill + " CallSkill.");
				}
			}
			else
			{
				triggerSkill = _skill.getSkill();
			}
		}
		else
		{
			final BuffInfo buffInfo = effected.getEffectList().getBuffInfoBySkillId(_skill.getSkillId());
			if (buffInfo != null)
			{
				triggerSkill = SkillData.getInstance().getSkill(_skill.getSkillId(), Math.min(_skillLevelScaleTo, buffInfo.getSkill().getLevel() + 1));
			}
			else
			{
				triggerSkill = _skill.getSkill();
			}
		}
		
		if (triggerSkill != null)
		{
			SkillCaster.triggerCast(effector, effected, triggerSkill);
		}
		else
		{
			LOGGER.warn("Skill not found effect called from " + skill);
		}
	}
}
