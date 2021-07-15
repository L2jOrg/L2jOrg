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
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * @author Nik
 */
public class Party implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature creature, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		if (isPlayable(target))
		{
			final Player player = target.getActingPlayer();
			final org.l2j.gameserver.model.Party party = player.getParty();
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<Playable> filter = plbl ->
			{
				// Range skills appear to not affect you unless you are the main target.
				if ((plbl == creature) && (target != creature))
				{
					return false;
				}
				
				if ((affectLimit > 0) && (affected.get() >= affectLimit))
				{
					return false;
				}
				
				final Player p = plbl.getActingPlayer();
				if ((p == null) || p.isDead())
				{
					return false;
				}
				
				if (p != player)
				{
					final org.l2j.gameserver.model.Party targetParty = p.getParty();
					if ((party == null) || (targetParty == null) || (party.getLeaderObjectId() != targetParty.getLeaderObjectId()))
					{
						return false;
					}
				}
				
				if ((affectObject != null) && !affectObject.checkAffectedObject(creature, p))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Affect object of origin since its skipped in the forEachVisibleObjectInRange method.
			if (filter.test((Playable) target))
			{
				action.accept(target);
			}
			
			// Check and add targets.
			World.getInstance().forEachVisibleObjectInRange(target, Playable.class, affectRange, c ->
			{
				if (filter.test(c))
				{
					action.accept(c);
				}
			});
		}
		else if (isNpc(target))
		{
			final Npc npc = (Npc) target;
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<Npc> filter = n ->
			{
				if ((affectLimit > 0) && (affected.get() >= affectLimit))
				{
					return false;
				}
				if (n.isDead())
				{
					return false;
				}
				if (n.isAutoAttackable(npc))
				{
					return false;
				}
				if ((affectObject != null) && !affectObject.checkAffectedObject(creature, n))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Add object of origin since its skipped in the getObjects method.
			if (filter.test(npc))
			{
				action.accept(npc);
			}
			
			// Check and add targets.
			World.getInstance().forEachVisibleObjectInRange(npc, Npc.class, affectRange, n ->
			{
				if (n == creature)
				{
					return;
				}
				
				if (filter.test(n))
				{
					action.accept(n);
				}
			});
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.PARTY;
	}
}
