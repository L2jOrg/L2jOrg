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

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.gameserver.enums.StatModifierType;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.conditions.Condition;
import com.l2jmobius.gameserver.model.conditions.ConditionPlayerIsInCombat;
import com.l2jmobius.gameserver.model.conditions.ConditionUsingItemType;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.items.type.ArmorType;
import com.l2jmobius.gameserver.model.items.type.WeaponType;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Stats;

/**
 * @author Sdw
 */
public abstract class AbstractStatEffect extends AbstractEffect
{
	protected final Stats _addStat;
	protected final Stats _mulStat;
	protected final double _amount;
	protected final StatModifierType _mode;
	protected final List<Condition> _conditions = new ArrayList<>();
	
	public AbstractStatEffect(StatsSet params, Stats stat)
	{
		this(params, stat, stat);
	}
	
	public AbstractStatEffect(StatsSet params, Stats mulStat, Stats addStat)
	{
		_addStat = addStat;
		_mulStat = mulStat;
		_amount = params.getDouble("amount", 0);
		_mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
		
		int weaponTypesMask = 0;
		final List<String> weaponTypes = params.getList("weaponType", String.class);
		if (weaponTypes != null)
		{
			for (String weaponType : weaponTypes)
			{
				try
				{
					weaponTypesMask |= WeaponType.valueOf(weaponType).mask();
				}
				catch (IllegalArgumentException e)
				{
					final IllegalArgumentException exception = new IllegalArgumentException("weaponType should contain WeaponType enum value but found " + weaponType);
					exception.addSuppressed(e);
					throw exception;
				}
			}
		}
		
		int armorTypesMask = 0;
		final List<String> armorTypes = params.getList("armorType", String.class);
		if (armorTypes != null)
		{
			for (String armorType : armorTypes)
			{
				try
				{
					armorTypesMask |= ArmorType.valueOf(armorType).mask();
				}
				catch (IllegalArgumentException e)
				{
					final IllegalArgumentException exception = new IllegalArgumentException("armorTypes should contain ArmorType enum value but found " + armorType);
					exception.addSuppressed(e);
					throw exception;
				}
			}
		}
		
		if (weaponTypesMask != 0)
		{
			_conditions.add(new ConditionUsingItemType(weaponTypesMask));
		}
		
		if (armorTypesMask != 0)
		{
			_conditions.add(new ConditionUsingItemType(armorTypesMask));
		}
		
		if (params.contains("inCombat"))
		{
			_conditions.add(new ConditionPlayerIsInCombat(params.getBoolean("inCombat")));
		}
	}
	
	@Override
	public void pump(L2Character effected, Skill skill)
	{
		if (_conditions.isEmpty() || _conditions.stream().allMatch(cond -> cond.test(effected, effected, skill)))
		{
			switch (_mode)
			{
				case DIFF:
				{
					effected.getStat().mergeAdd(_addStat, _amount);
					break;
				}
				case PER:
				{
					effected.getStat().mergeMul(_mulStat, (_amount / 100) + 1);
					break;
				}
			}
		}
	}
}
