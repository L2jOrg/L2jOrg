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
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Lethal effect implementation.
 * @author Adry_85
 */
public final class Lethal extends AbstractEffect
{
	private final double _fullLethal;
	private final double _halfLethal;
	
	public Lethal(StatsSet params)
	{
		_fullLethal = params.getDouble("fullLethal", 0);
		_halfLethal = params.getDouble("halfLethal", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.LETHAL_ATTACK;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector.isPlayer() && !effector.getAccessLevel().canGiveDamage())
		{
			return;
		}
		
		if (skill.getMagicLevel() < (effected.getLevel() - 6))
		{
			return;
		}
		
		if (!effected.isLethalable() || effected.isHpBlocked())
		{
			return;
		}
		
		if (effector.isPlayer() && effected.isPlayer() && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY))
		{
			return;
		}
		
		final double chanceMultiplier = Formulas.calcAttributeBonus(effector, effected, skill) * Formulas.calcGeneralTraitBonus(effector, effected, skill.getTraitType(), false);
		
		// Calculate instant kill resistance first.
		if (Rnd.get(100) < effected.getStat().getValue(Stats.INSTANT_KILL_RESIST, 0))
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK);
			sm.addString(effected.getName());
			sm.addString(effector.getName());
			effected.sendPacket(sm);
			final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY);
			sm2.addString(effector.getName());
			effector.sendPacket(sm2);
		}
		// Lethal Strike
		else if (Rnd.get(100) < (_fullLethal * chanceMultiplier))
		{
			// for Players CP and HP is set to 1.
			if (effected.isPlayer())
			{
				effected.setCurrentCp(1);
				effected.setCurrentHp(1);
				effected.sendPacket(SystemMessageId.LETHAL_STRIKE);
			}
			// for Monsters HP is set to 1.
			else if (effected.isMonster() || effected.isSummon())
			{
				effected.setCurrentHp(1);
			}
			effector.sendPacket(SystemMessageId.HIT_WITH_LETHAL_STRIKE);
		}
		// Half-Kill
		else if (Rnd.get(100) < (_halfLethal * chanceMultiplier))
		{
			// for Players CP is set to 1.
			if (effected.isPlayer())
			{
				effected.setCurrentCp(1);
				effected.sendPacket(SystemMessageId.HALF_KILL);
				effected.sendPacket(SystemMessageId.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_HALF_KILL_SKILL);
			}
			// for Monsters HP is set to 50%.
			else if (effected.isMonster() || effected.isSummon())
			{
				effected.setCurrentHp(effected.getCurrentHp() * 0.5);
			}
			effector.sendPacket(SystemMessageId.HALF_KILL);
		}
		
		// No matter if lethal succeeded or not, its reflected.
		Formulas.calcCounterAttack(effector, effected, skill, false);
	}
}
