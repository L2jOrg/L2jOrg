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

import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * @author Mobius
 */
public class CallRandomSkill extends AbstractEffect
{
	private final List<SkillHolder> _skills = new ArrayList<>();
	
	public CallRandomSkill(StatsSet params)
	{
		final String skills = params.getString("skills", null);
		if (skills != null)
		{
			for (String skill : skills.split(";"))
			{
				_skills.add(new SkillHolder(Integer.parseInt(skill.split(",")[0]), Integer.parseInt(skill.split(",")[1])));
			}
		}
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		SkillCaster.triggerCast(effector, effected, _skills.get(Rnd.get(_skills.size())).getSkill());
	}
}
