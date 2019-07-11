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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Set Skill effect implementation.
 * @author Zoey76
 */
public final class SetSkill extends AbstractEffect
{
	private final int _skillId;
	private final int _skillLvl;
	
	public SetSkill(StatsSet params)
	{
		_skillId = params.getInt("skillId", 0);
		_skillLvl = params.getInt("skillLvl", 1);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effected.isPlayer())
		{
			return;
		}
		
		final Skill setSkill = SkillData.getInstance().getSkill(_skillId, _skillLvl);
		if (setSkill == null)
		{
			return;
		}
		
		effected.getActingPlayer().addSkill(setSkill, true);
	}
}
