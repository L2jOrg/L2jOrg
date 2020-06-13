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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class MagicalAttackMp extends AbstractEffect {
	private final double power;
	private final boolean critical;
	private final double criticalLimit;
	
	private MagicalAttackMp(StatsSet params) {
		power = params.getDouble("power");
		critical = params.getBoolean("critical");
		criticalLimit = params.getDouble("critical-limit");
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
		if (effected.isMpBlocked()) {
			return false;
		}
		
		if (isPlayer(effector) && isPlayer(effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
			return false;
		}
		
		if (!Formulas.calcMagicAffected(effector, effected, skill)) {
			if (isPlayer(effector)) {
				effector.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
			}
			if (isPlayer(effected)) {
				effected.sendPacket( getSystemMessage(SystemMessageId.C1_RESISTED_C2_S_DRAIN).addString(effected.getName()).addString(effector.getName()));
			}
			return false;
		}
		return true;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effector.isAlikeDead()) {
			return;
		}

		final boolean mcrit = critical && Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		final double damage = Formulas.calcManaDam(effector, effected, skill, power, mcrit, criticalLimit);
		final double mp = Math.min(effected.getCurrentMp(), damage);
		
		if (damage > 0) {
			effected.stopEffectsOnDamage();
			effected.setCurrentMp(effected.getCurrentMp() - mp);
		}
		
		if (isPlayer(effected)) {
			effected.sendPacket(getSystemMessage(SystemMessageId.S2_S_MP_HAS_BEEN_DRAINED_BY_C1).addString(effector.getName()).addInt((int) mp));
		}
		
		if (isPlayer(effector)) {
			effector.sendPacket( getSystemMessage(SystemMessageId.YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1).addInt((int) mp));
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new MagicalAttackMp(data);
		}

		@Override
		public String effectName() {
			return "magical-attack-mp";
		}
	}
}
