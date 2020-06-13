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
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Heal effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class Heal extends AbstractEffect {
	private final double power;
	
	private Heal(StatsSet params)
	{
		power = params.getDouble("power", 0);
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
		
		if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
			return;
		}
		
		double amount = power;

		if (nonNull(item) && (item.isPotion() || item.isElixir())) {
			amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0);
		}

		double staticShotBonus = 0;
		double mAtkMul = skill.isMagic() ? effector.chargedShotBonus(ShotType.SPIRITSHOTS) : 1;

		if (mAtkMul > 1 && (isPlayer(effector) && effector.getActingPlayer().isMageClass() || isSummon(effector))) {
			staticShotBonus = skill.getMpConsume();
			staticShotBonus *= mAtkMul >= 4 ? 2.4 : 1.0; // 2.4 if is blessed spiritshots TODO improve
		} else if (mAtkMul > 1  && isNpc(effector)) {
			staticShotBonus = 2.4 * skill.getMpConsume(); // always blessed spiritshots
		}
		else {
			// shot dynamic bonus
			mAtkMul = mAtkMul >= 4 ? mAtkMul * 4 : mAtkMul + 1;
		}
		
		if (!skill.isStatic()) {
			amount += staticShotBonus + Math.sqrt(mAtkMul * effector.getMAtk());
			amount *= effected.getStats().getValue(Stat.HEAL_RECEIVE, 1);
			amount = effector.getStats().getValue(Stat.HEAL_POWER, amount);
			// Heal critic, since CT2.3 Gracia Final
			if (skill.isMagic() && Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill)) {
				amount *= 3;
				effector.sendPacket(SystemMessageId.M_CRITICAL);
				effector.sendPacket(new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
				if (isPlayer(effected) && (effected != effector))
				{
					effected.sendPacket(new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), ExMagicAttackInfo.CRITICAL_HEAL));
				}
			}
		}
		
		// Prevents overheal
		amount = Math.min(amount, effected.getMaxRecoverableHp() - effected.getCurrentHp());
		if (amount != 0) {
			final double newHp = amount + effected.getCurrentHp();
			effected.setCurrentHp(newHp, false);
			effected.broadcastStatusUpdate(effector);
		}
		
		if (isPlayer(effected)) {
			if (skill.getId() == 4051) {
				effected.sendPacket(SystemMessageId.REJUVENATING_HP);
			} else if (isPlayer(effector) && (effector != effected)) {
				effected.sendPacket(getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName()).addInt((int) amount));
			} else {
				effected.sendPacket(getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED).addInt((int) amount));
			}
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new Heal(data);
		}

		@Override
		public String effectName() {
			return "Heal";
		}
	}
}
