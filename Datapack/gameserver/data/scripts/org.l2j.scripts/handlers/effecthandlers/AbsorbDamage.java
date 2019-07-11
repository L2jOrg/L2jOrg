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
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.DamageReturn;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sdw
 */
public class AbsorbDamage extends AbstractEffect
{
	private final double _damage;
	private static final Map<Integer, Double> _damageHolder = new ConcurrentHashMap<>();
	
	public AbsorbDamage(StatsSet params)
	{
		_damage = params.getDouble("damage", 0);
	}
	
	private DamageReturn onDamageReceivedEvent(OnCreatureDamageReceived event)
	{
		// DOT effects are not taken into account.
		if (event.isDamageOverTime())
		{
			return null;
		}
		
		final int objectId = event.getTarget().getObjectId();
		
		final double damageLeft = _damageHolder.get(objectId) != null ? _damageHolder.get(objectId) : 0;
		final double newDamageLeft = Math.max(damageLeft - event.getDamage(), 0);
		final double newDamage = Math.max(event.getDamage() - damageLeft, 0);
		
		_damageHolder.put(objectId, newDamageLeft);
		
		return new DamageReturn(false, true, false, newDamage);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
		_damageHolder.remove(effected.getObjectId());
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
	{
		_damageHolder.put(effected.getObjectId(), _damage);
		effected.addListener(new FunctionEventListener(effected, EventType.ON_CREATURE_DAMAGE_RECEIVED, (OnCreatureDamageReceived event) -> onDamageReceivedEvent(event), this));
	}
}
