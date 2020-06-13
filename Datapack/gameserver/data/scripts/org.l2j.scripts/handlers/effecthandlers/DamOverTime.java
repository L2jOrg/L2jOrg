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
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Dam Over Time effect implementation.
 */
public final class DamOverTime extends AbstractEffect {
	private final boolean canKill;
	private final double power;
	private final StatModifierType mode;

	private DamOverTime(StatsSet params) {
		canKill = params.getBoolean("can-kill", false);
		power = params.getDouble("power");
		mode = params.getEnum("mode", StatModifierType.class);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (!skill.isToggle() && skill.isMagic()) {
			// TODO: M.Crit can occur even if this skill is resisted. Only then m.crit damage is applied and not debuff
			final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
			if (mcrit) {
				double damage = power * 10; // Tests show that 10 times HP DOT is taken during magic critical.
				
				if (!canKill && (damage >= (effected.getCurrentHp() - 1))) {
					damage = effected.getCurrentHp() - 1;
				}
				
				effected.reduceCurrentHp(damage, effector, skill, true, false, true, false, DamageInfo.DamageType.OTHER);
			}
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DMG_OVER_TIME;
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return false;
		}
		
		double damage = power * getTicksMultiplier() * (mode == StatModifierType.PER ? effected.getCurrentHp() : 1);
		if (damage >= effected.getCurrentHp() - 1) {
			if (skill.isToggle()) {
				effected.sendPacket(SystemMessageId.YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP);
				return false;
			}
			
			// For DOT skills that will not kill effected player.
			if (!canKill) {
				// Fix for players dying by DOTs if HP < 1 since reduceCurrentHP method will kill them
				if (effected.getCurrentHp() <= 1) {
					return false;
				}
				damage = effected.getCurrentHp() - 1;
			}
		}
		//DamageInfo.DamageType.POISON
		effector.doAttack(damage, effected, skill, true, false, false, false);
		return skill.isToggle();
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new DamOverTime(data);
		}

		@Override
		public String effectName() {
			return "damage-over-time";
		}
	}
}
