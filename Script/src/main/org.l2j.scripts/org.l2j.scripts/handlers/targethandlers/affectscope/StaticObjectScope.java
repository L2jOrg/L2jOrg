/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Static Object affect scope implementation. Used to detect hidden doors.
 * @author Nik
 */
public class StaticObjectScope implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
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
			
			if (!isDoor(c) && !(c instanceof StaticWorldObject))
			{
				return false;
			}
			
			if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, c))
			{
				return false;
			}
			
			affected.incrementAndGet();
			return true;
		};
		
		// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
		if (isCreature(target) && filter.test((Creature) target))
		{
			action.accept(target);
		}
		
		// Check and add targets.
		World.getInstance().forEachVisibleObjectInRange(target, Creature.class, affectRange, c ->
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
		return AffectScope.STATIC_OBJECT_SCOPE;
	}
}
