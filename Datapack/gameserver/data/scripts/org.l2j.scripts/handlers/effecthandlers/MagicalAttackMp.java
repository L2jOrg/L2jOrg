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

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.L2EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 */
public final class MagicalAttackMp extends AbstractEffect
{
	private final double _power;
	private final boolean _critical;
	private final double _criticalLimit;
	
	public MagicalAttackMp(StatsSet params)
	{
		_power = params.getDouble("power");
		_critical = params.getBoolean("critical");
		_criticalLimit = params.getDouble("criticalLimit");
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		if (effected.isMpBlocked())
		{
			return false;
		}
		
		if (effector.isPlayer() && effected.isPlayer() && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY))
		{
			return false;
		}
		
		if (!Formulas.calcMagicAffected(effector, effected, skill))
		{
			if (effector.isPlayer())
			{
				effector.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
			}
			if (effected.isPlayer())
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_S_DRAIN);
				sm.addString(effected.getName());
				sm.addString(effector.getName());
				effected.sendPacket(sm);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector.isAlikeDead())
		{
			return;
		}
		
		final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
		final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = Formulas.calcShldUse(effector, effected);
		final boolean mcrit = _critical ? Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill) : false;
		final double damage = Formulas.calcManaDam(effector, effected, skill, _power, shld, sps, bss, mcrit, _criticalLimit);
		final double mp = Math.min(effected.getCurrentMp(), damage);
		
		if (damage > 0)
		{
			effected.stopEffectsOnDamage();
			effected.setCurrentMp(effected.getCurrentMp() - mp);
		}
		
		if (effected.isPlayer())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S_MP_HAS_BEEN_DRAINED_BY_C1);
			sm.addString(effector.getName());
			sm.addInt((int) mp);
			effected.sendPacket(sm);
		}
		
		if (effector.isPlayer())
		{
			final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1);
			sm2.addInt((int) mp);
			effector.sendPacket(sm2);
		}
	}
}
