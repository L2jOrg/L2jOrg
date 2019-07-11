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

import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.targets.AffectScope;

/**
 * @author Nik
 */
public class Party implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(L2Character activeChar, L2Object target, Skill skill, Consumer<? super L2Object> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		if (target.isPlayable())
		{
			final Player player = target.getActingPlayer();
			final org.l2j.gameserver.model.L2Party party = player.getParty();
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<L2Playable> filter = plbl ->
			{
				// Range skills appear to not affect you unless you are the main target.
				if ((plbl == activeChar) && (target != activeChar))
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
					final org.l2j.gameserver.model.L2Party targetParty = p.getParty();
					if ((party == null) || (targetParty == null) || (party.getLeaderObjectId() != targetParty.getLeaderObjectId()))
					{
						return false;
					}
				}
				
				if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, p))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Affect object of origin since its skipped in the forEachVisibleObjectInRange method.
			if (filter.test((L2Playable) target))
			{
				action.accept(target);
			}
			
			// Check and add targets.
			L2World.getInstance().forEachVisibleObjectInRange(target, L2Playable.class, affectRange, c ->
			{
				if (filter.test(c))
				{
					action.accept(c);
				}
			});
		}
		else if (target.isNpc())
		{
			final L2Npc npc = (L2Npc) target;
			
			// Create the target filter.
			final AtomicInteger affected = new AtomicInteger(0);
			final Predicate<L2Npc> filter = n ->
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
				if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, n))
				{
					return false;
				}
				
				affected.incrementAndGet();
				return true;
			};
			
			// Add object of origin since its skipped in the getVisibleObjects method.
			if (filter.test(npc))
			{
				action.accept(npc);
			}
			
			// Check and add targets.
			L2World.getInstance().forEachVisibleObjectInRange(npc, L2Npc.class, affectRange, n ->
			{
				if (n == activeChar)
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
