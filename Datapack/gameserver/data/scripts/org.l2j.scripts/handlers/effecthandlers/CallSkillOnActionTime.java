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
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.Collections;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Dam Over Time effect implementation.
 */
public final class CallSkillOnActionTime extends AbstractEffect
{
	private final SkillHolder _skill;
	
	public CallSkillOnActionTime(StatsSet params)
	{
		_skill = new SkillHolder(params.getInt("skillId"), params.getInt("skillLevel", 1), params.getInt("skillSubLevel", 0));
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getEffectList().stopEffects(Collections.singleton(_skill.getSkill().getAbnormalType()));
		effected.getEffectList().addBlockedAbnormalTypes(Collections.singleton(_skill.getSkill().getAbnormalType()));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getEffectList().removeBlockedAbnormalTypes(Collections.singleton(_skill.getSkill().getAbnormalType()));
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector.isDead())
		{
			return false;
		}
		
		final Skill triggerSkill = _skill.getSkill();
		if (triggerSkill != null)
		{
			if (triggerSkill.isSynergySkill())
			{
				triggerSkill.applyEffects(effector, effector);
			}
			
			World.getInstance().forEachVisibleObjectInRange(effector, Creature.class, _skill.getSkill().getAffectRange(), c ->
			{
				final WorldObject target = triggerSkill.getTarget(effector, c, false, false, false);
				
				if (isCreature(target))
				{
					SkillCaster.triggerCast(effector, (Creature) target, triggerSkill);
				}
			});
		}
		else
		{
			LOGGER.warn("Skill not found effect called from " + skill);
		}
		return skill.isToggle();
	}
}
