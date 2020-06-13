/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;

import static java.util.Objects.nonNull;

/**
 * Call Skill effect implementation.
 * @author NosBit
 * @author JoeAlisson
 */
public final class CallSkill extends AbstractEffect {

	private final SkillHolder skill;
	
	private CallSkill(StatsSet params) {
		skill = new SkillHolder(params.getInt("skill"), params.getInt("power", 1));
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		Skill triggerSkill = null;
		// Mobius: Use 0 to trigger max effector learned skill level.
		if (this.skill.getLevel() == 0) {
			final int knownLevel = effector.getSkillLevel(this.skill.getSkillId());

			if (knownLevel > 0) {
				triggerSkill = SkillEngine.getInstance().getSkill(this.skill.getSkillId(), knownLevel);
			} else {
				LOGGER.warn("Player {} called unknown skill {} triggered by {} CallSkill.", effector, this.skill, skill);
			}
		} else {
			triggerSkill = this.skill.getSkill();
		}
		
		if (nonNull(triggerSkill)) {
			SkillCaster.triggerCast(effector, effected, triggerSkill);
		} else {
			LOGGER.warn("Skill not found effect called from {}", skill);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new CallSkill(data);
		}

		@Override
		public String effectName() {
			return "call-skill";
		}
	}
}
