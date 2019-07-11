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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureKilled;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

/**
 * Trigger Skill By Kill effect implementation.
 * @author Sdw
 */
public final class TriggerSkillByKill extends AbstractEffect
{
	private final int _chance;
	private final SkillHolder _skill;
	
	public TriggerSkillByKill(StatsSet params)
	{
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
	}
	
	private void onCreatureKilled(OnCreatureKilled event, Creature target)
	{
		if ((_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLevel() == 0)))
		{
			return;
		}
		
		if (Rnd.get(100) > _chance)
		{
			return;
		}
		
		final Skill triggerSkill = _skill.getSkill();
		
		if (event.getAttacker() == target)
		{
			SkillCaster.triggerCast(target, target, triggerSkill);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_KILLED, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
	{
		effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_KILLED, (OnCreatureKilled event) -> onCreatureKilled(event, effected), this));
	}
}
