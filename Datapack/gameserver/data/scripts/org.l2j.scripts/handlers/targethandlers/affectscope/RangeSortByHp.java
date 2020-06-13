/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
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
package handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Range sorted by lowest to highest hp percent affect scope implementation.
 * @author Nik
 */
public class RangeSortByHp implements IAffectScopeHandler
{
    @Override
    public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
    {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
        final int affectRange = skill.getAffectRange();
        final int affectLimit = skill.getAffectLimit();

        // Target checks.
        final AtomicInteger affected = new AtomicInteger(0);

        final Predicate<Creature> filter = c -> {

            if ( (affectLimit > 0 && affected.get() >= affectLimit) || c.isDead() )
            {
                return false;
            }


            // Range skills appear to not affect you unless you are the main target.
            if ((c == activeChar) && (target != activeChar))
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

        if(isCreature(target) && filter.test((Creature) target)) {
            action.accept(target);
        }

        World.getInstance().forVisibleOrderedObjectsInRange(target, Creature.class, affectRange, affectLimit > 0 ? affectLimit : Integer.MAX_VALUE, filter, Comparator.comparingInt(Creature::getCurrentHpPercent), action);

    }

    @Override
    public Enum<AffectScope> getAffectScopeType()
    {
        return AffectScope.RANGE_SORT_BY_HP;
    }
}
