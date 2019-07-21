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

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

import static org.l2j.gameserver.util.MathUtil.calculateAngleFrom;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * Check if this effect is not counted as being stunned.
 * @author UnAfraid
 */
public final class KnockBack extends AbstractEffect
{
	private final int _distance;
	private final int _speed;
	private final int _delay;
	private final int _animationSpeed;
	private final boolean _knockDown;
	private final FlyType _type;
	
	public KnockBack(StatsSet params)
	{
		_distance = params.getInt("distance", 50);
		_speed = params.getInt("speed", 0);
		_delay = params.getInt("delay", 0);
		_animationSpeed = params.getInt("animationSpeed", 0);
		_knockDown = params.getBoolean("knockDown", false);
		_type = params.getEnum("type", FlyType.class, _knockDown ? FlyType.PUSH_DOWN_HORIZONTAL : FlyType.PUSH_HORIZONTAL);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return _knockDown || Formulas.calcProbability(100, effector, effected, skill);
	}
	
	@Override
	public boolean isInstant()
	{
		return !_knockDown;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.KNOCK;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!_knockDown)
		{
			knockBack(effector, effected);
		}
	}
	
	@Override
	public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (_knockDown)
		{
			knockBack(effector, effected);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	private void knockBack(Creature effector, Creature effected)
	{
		if ((effected == null) || effected.isRaid())
		{
			return;
		}

		final double radians = Math.toRadians(calculateAngleFrom(effector, effected));
		final int x = (int) (effected.getX() + (_distance * Math.cos(radians)));
		final int y = (int) (effected.getY() + (_distance * Math.sin(radians)));
		final int z = effected.getZ();
		final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceWorld());
		
		effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		effected.broadcastPacket(new FlyToLocation(effected, loc, _type, _speed, _delay, _animationSpeed));
		if (_knockDown)
		{
			effected.setHeading(calculateHeadingFrom(effected, effector));
		}
		effected.setXYZ(loc);
		effected.broadcastPacket(new ValidateLocation(effected));
		effected.revalidateZone(true);
	}
}
