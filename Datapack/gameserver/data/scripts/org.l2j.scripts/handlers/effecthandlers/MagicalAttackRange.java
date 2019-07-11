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
import org.l2j.gameserver.model.effects.L2EffectType;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * Magical Attack effect implementation.
 * @author Adry_85
 */
public final class MagicalAttackRange extends AbstractEffect
{
	private final double _power;
	private final double _shieldDefPercent;
	
	public MagicalAttackRange(StatsSet params)
	{
		_power = params.getDouble("power");
		_shieldDefPercent = params.getDouble("shieldDefPercent", 0);
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
	public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
	{
		if (effected.isPlayer() && effected.getActingPlayer().isFakeDeath())
		{
			effected.stopFakeDeath(true);
		}
		
		double mDef = effected.getMDef();
		switch (Formulas.calcShldUse(effector, effected))
		{
			case Formulas.SHIELD_DEFENSE_SUCCEED:
			{
				mDef += ((effected.getShldDef() * _shieldDefPercent) / 100);
				break;
			}
			case Formulas.SHIELD_DEFENSE_PERFECT_BLOCK:
			{
				mDef = -1;
				break;
			}
		}
		
		double damage = 1;
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		
		if (mDef != -1)
		{
			final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
			final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
			
			damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), _power, mDef, sps, bss, mcrit);
		}
		
		effector.doAttack(damage, effected, skill, false, false, mcrit, false);
	}
}