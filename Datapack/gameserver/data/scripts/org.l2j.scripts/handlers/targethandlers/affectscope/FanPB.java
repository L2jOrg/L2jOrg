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
package handlers.targethandlers.affectscope;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.util.GameUtils;

/**
 * Fan point blank affect scope implementation. Gathers objects in a certain angle of circular area around yourself without taking target into account.
 * @author Nik
 */
public class FanPB implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, L2Object target, Skill skill, Consumer<? super L2Object> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final double headingAngle = GameUtils.convertHeadingToDegree(activeChar.getHeading());
		final int fanStartAngle = skill.getFanRange()[1];
		final int fanRadius = skill.getFanRange()[2];
		final int fanAngle = skill.getFanRange()[3];
		final double fanHalfAngle = fanAngle / 2; // Half left and half right.
		final int affectLimit = skill.getAffectLimit();
		
		// Target checks.
		final AtomicInteger affected = new AtomicInteger(0);
		final Predicate<Creature> filter = c ->
		{
			if ((affectLimit > 0) && (affected.get() >= affectLimit))
			{
				return false;
			}
			if (c.isDead())
			{
				return false;
			}
			if (Math.abs(GameUtils.calculateAngleFrom(activeChar, c) - (headingAngle + fanStartAngle)) > fanHalfAngle)
			{
				return false;
			}
			if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, c))
			{
				return false;
			}
			if (!GeoEngine.getInstance().canSeeTarget(activeChar, c))
			{
				return false;
			}
			
			affected.incrementAndGet();
			return true;
		};
		
		// Check and add targets.
		L2World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, fanRadius, c ->
		{
			if (filter.test(c))
			{
				action.accept(c);
			}
		});
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.FAN_PB;
	}
}
