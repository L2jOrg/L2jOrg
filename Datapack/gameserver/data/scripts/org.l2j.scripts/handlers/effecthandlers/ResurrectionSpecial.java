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
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Resurrection Special effect implementation.
 * @author Zealar
 */
public final class ResurrectionSpecial extends AbstractEffect
{
	private final int _power;
	private final Set<Integer> _instanceId;
	
	public ResurrectionSpecial(StatsSet params)
	{
		_power = params.getInt("power", 0);
		
		final String instanceIds = params.getString("instanceId", null);
		if ((instanceIds != null) && !instanceIds.isEmpty())
		{
			_instanceId = new HashSet<>();
			for (String id : instanceIds.split(";"))
			{
				_instanceId.add(Integer.parseInt(id));
			}
		}
		else
		{
			_instanceId = Collections.<Integer> emptySet();
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESURRECTION_SPECIAL;
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.RESURRECTION_SPECIAL.getMask();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!isPlayer(effected) && !isPet(effected))
		{
			return;
		}
		
		final Player caster = effector.getActingPlayer();
		final Instance instance = caster.getInstanceWorld();
		if (!_instanceId.isEmpty() && ((instance == null) || !_instanceId.contains(instance.getTemplateId())))
		{
			return;
		}
		
		if (isPlayer(effected))
		{
			effected.getActingPlayer().reviveRequest(caster, skill, false, _power);
		}
		else if (isPet(effected))
		{
			final Pet pet = (Pet) effected;
			effected.getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, _power);
		}
	}
}