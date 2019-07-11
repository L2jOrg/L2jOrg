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
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Double Casting effect implementation.
 * @author Nik
 */
public final class DoubleCast extends AbstractEffect
{
	private static final SkillHolder[] TOGGLE_SKILLS = new SkillHolder[]
	{
		new SkillHolder(11007, 1),
		new SkillHolder(11009, 1),
		new SkillHolder(11008, 1),
		new SkillHolder(11010, 1)
	};
	
	private final Map<Integer, List<SkillHolder>> _addedToggles;
	
	public DoubleCast(StatsSet params)
	{
		_addedToggles = new HashMap<>();
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.DOUBLE_CAST.getMask();
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effected.isPlayer())
		{
			for (SkillHolder holder : TOGGLE_SKILLS)
			{
				final Skill s = holder.getSkill();
				if ((s != null) && !effected.isAffectedBySkill(holder))
				{
					_addedToggles.computeIfAbsent(effected.getObjectId(), v -> new ArrayList<>()).add(holder);
					s.applyEffects(effected, effected);
				}
			}
		}
		super.onStart(effector, effected, skill, item);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isPlayer())
		{
			_addedToggles.computeIfPresent(effected.getObjectId(), (k, v) ->
			{
				v.forEach(h -> effected.stopSkillEffects(h.getSkill()));
				return null;
			});
		}
	}
}