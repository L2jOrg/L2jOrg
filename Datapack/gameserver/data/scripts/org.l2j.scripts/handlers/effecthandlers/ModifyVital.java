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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Modify vital effect implementation.
 * @author malyelfik
 */
public final class ModifyVital extends AbstractEffect
{
	// Modify types
	private enum ModifyType
	{
		DIFF,
		SET,
		PER;
	}
	
	// Effect parameters
	private final ModifyType _type;
	private final int _hp;
	private final int _mp;
	private final int _cp;
	
	public ModifyVital(StatsSet params)
	{
		_type = params.getEnum("type", ModifyType.class);
		if (_type != ModifyType.SET)
		{
			_hp = params.getInt("hp", 0);
			_mp = params.getInt("mp", 0);
			_cp = params.getInt("cp", 0);
		}
		else
		{
			_hp = params.getInt("hp", -1);
			_mp = params.getInt("mp", -1);
			_cp = params.getInt("cp", -1);
		}
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
	{
		if (effected.isDead())
		{
			return;
		}
		
		if (effector.isPlayer() && effected.isPlayer() && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY))
		{
			return;
		}
		
		switch (_type)
		{
			case DIFF:
			{
				effected.setCurrentCp(effected.getCurrentCp() + _cp);
				effected.setCurrentHp(effected.getCurrentHp() + _hp);
				effected.setCurrentMp(effected.getCurrentMp() + _mp);
				break;
			}
			case SET:
			{
				if (_cp >= 0)
				{
					effected.setCurrentCp(_cp);
				}
				if (_hp >= 0)
				{
					effected.setCurrentHp(_hp);
				}
				if (_mp >= 0)
				{
					effected.setCurrentMp(_mp);
				}
				break;
			}
			case PER:
			{
				effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * (_cp / 100)));
				effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * (_hp / 100)));
				effected.setCurrentMp(effected.getCurrentMp() + (effected.getMaxMp() * (_mp / 100)));
				break;
			}
		}
	}
}
