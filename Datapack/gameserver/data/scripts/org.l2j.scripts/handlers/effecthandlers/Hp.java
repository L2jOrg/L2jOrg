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
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * HP change effect. It is mostly used for potions and static damage.
 * @author Nik
 * @author JoeAlisson
 */
public final class Hp extends AbstractEffect {
	private final int power;
	private final StatModifierType mode;
	
	private Hp(StatsSet params) {
		power = params.getInt("power", 0);
		mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
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

		int basicAmount = power;
		if (nonNull(item) && (item.isPotion() || item.isElixir())) {
			basicAmount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0);
		}
		
		double amount = 0;
		switch (mode) {
			case DIFF -> amount = Math.min(basicAmount, effected.getMaxRecoverableHp() - effected.getCurrentHp());
			case PER -> amount = Math.min((effected.getMaxHp() * basicAmount) / 100.0, effected.getMaxRecoverableHp() - effected.getCurrentHp());
		}
		
		if (amount >= 0) {
			if (amount != 0) {
				effected.setCurrentHp(amount + effected.getCurrentHp(), false);
				effected.broadcastStatusUpdate(effector);
			}
			
			SystemMessage sm;
			if (effector.getObjectId() != effected.getObjectId()) {
				sm = getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
			} else {
				sm = getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
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
			return new Hp(data);
		}

		@Override
		public String effectName() {
			return "hp";
		}
	}
}
