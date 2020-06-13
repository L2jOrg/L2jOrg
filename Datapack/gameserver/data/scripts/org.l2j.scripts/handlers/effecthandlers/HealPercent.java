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
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Heal Percent effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class HealPercent extends AbstractEffect {
	private final int power;
	
	private HealPercent(StatsSet params)
	{
		power = params.getInt("power", 0);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.HEAL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead() || isDoor(effected) || effected.isHpBlocked()) {
			return;
		}
		
		double amount;
		final double power = this.power;
		final boolean full = (power == 100.0);
		
		amount = full ? effected.getMaxHp() : (effected.getMaxHp() * power) / 100.0;
		if (nonNull(item) && (item.isPotion() || item.isElixir())) {
			amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0);
		}
		// Prevents overheal
		amount = Math.min(amount, effected.getMaxRecoverableHp() - effected.getCurrentHp());
		if (amount >= 0) {
			if (amount != 0) {
				effected.setCurrentHp(amount + effected.getCurrentHp(), false);
				effected.broadcastStatusUpdate(effector);
			}
			
			SystemMessage sm;
			if (effector.getObjectId() != effected.getObjectId()) {
				sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
			} else {
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
			}
			sm.addInt((int) amount);
			effected.sendPacket(sm);
		} else {
			final double damage = -amount;
			effected.reduceCurrentHp(damage, effector, skill, false, false, false, false, DamageInfo.DamageType.OTHER);
			effector.sendDamageMessage(effected, skill, (int) damage, 0, false, false);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new HealPercent(data);
		}

		@Override
		public String effectName() {
			return "HealPercent";
		}
	}
}
