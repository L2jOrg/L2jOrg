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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Resist Skill effect implementaion.
 * @author UnAfraid
 */
public final class ResistSkill extends AbstractEffect
{
	private final List<SkillHolder> _skills = new ArrayList<>();
	
	public ResistSkill(StatsSet params)
	{
		for (int i = 1;; i++)
		{
			final int skillId = params.getInt("skillId" + i, 0);
			final int skillLvl = params.getInt("skillLvl" + i, 0);
			if (skillId == 0)
			{
				break;
			}
			_skills.add(new SkillHolder(skillId, skillLvl));
		}
		
		if (_skills.isEmpty())
		{
			throw new IllegalArgumentException(getClass().getSimpleName() + ": Without parameters!");
		}
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		for (SkillHolder holder : _skills)
		{
			effected.addIgnoreSkillEffects(holder);
		}
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		for (SkillHolder holder : _skills)
		{
			effected.removeIgnoreSkillEffects(holder);
		}
	}
}
