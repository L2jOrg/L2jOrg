/*
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
package org.l2j.gameserver.ai;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.ai.CtrlIntention.*;
import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.MathUtil.calculateDistance2D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius2D;


/**
 * @author Sdw
 */
public class FriendlyNpcAI extends AttackableAI {
    public FriendlyNpcAI(Attackable attackable) {
        super(attackable);
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {

    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {

    }

    @Override
    protected void onIntentionAttack(Creature target) {
        if (target == null) {
            clientActionFailed();
            return;
        }

        if (getIntention() == AI_INTENTION_REST) {
            clientActionFailed();
            return;
        }

        if (actor.isAllSkillsDisabled() || actor.isCastingNow() || actor.isControlBlocked()) {
            clientActionFailed();
            return;
        }

        // Set the Intention of this AbstractAI to AI_INTENTION_ATTACK
        changeIntention(AI_INTENTION_ATTACK, target);

        // Set the AI attack target
        setTarget(target);

        stopFollow();

        // Launch the Think Event
        notifyEvent(CtrlEvent.EVT_THINK, null);
    }

    @Override
    protected void thinkAttack() {
        final Attackable npc = getActiveChar();
        if (npc.isCastingNow() || npc.isCoreAIDisabled()) {
            return;
        }

        final WorldObject target = getTarget();
        final Creature originalAttackTarget = isCreature(target) ? (Creature) target : null;
        // Check if target is dead or if timeout is expired to stop this attack
        if ((originalAttackTarget == null) || originalAttackTarget.isAlikeDead()) {
            // Stop hating this target after the attack timeout or if target is dead
            if (originalAttackTarget != null) {
                npc.stopHating(originalAttackTarget);
            }

            // Set the AI Intention to AI_INTENTION_ACTIVE
            setIntention(AI_INTENTION_ACTIVE);

            npc.setWalking();
            return;
        }

        final int collision = npc.getTemplate().getCollisionRadius();

        setTarget(originalAttackTarget);

        final int combinedCollision = collision + originalAttackTarget.getTemplate().getCollisionRadius();

        if (!npc.isMovementDisabled() && (Rnd.get(100) <= 3)) {

            World.getInstance().forAnyVisibleObject(npc, Attackable.class, nearby -> moteToTargetIfNeed(npc, originalAttackTarget, collision, combinedCollision), nearby -> !nearby.equals(originalAttackTarget) && isInsideRadius2D(npc, nearby, collision));

        }
        // Dodge if its needed
        if (!npc.isMovementDisabled() && (npc.getTemplate().getDodge() > 0)) {
            if (Rnd.get(100) <= npc.getTemplate().getDodge()) {

                if(isInsideRadius2D(npc, originalAttackTarget, 60 + combinedCollision)) {
                    int posX = npc.getX();
                    int posY = npc.getY();
                    final int posZ = npc.getZ() + 30;

                    if (originalAttackTarget.getX() < posX) {
                        posX += 300;
                    } else {
                        posX -= 300;
                    }

                    if (originalAttackTarget.getY() < posY) {
                        posY += 300;
                    } else {
                        posY -= 300;
                    }

                    if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), posX, posY, posZ, npc.getInstanceWorld())) {
                        setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(posX, posY, posZ, 0));
                    }
                    return;
                }
            }
        }

        final double dist = calculateDistance2D(npc, originalAttackTarget);
        final int dist2 = (int) dist - collision;
        int range = npc.getPhysicalAttackRange() + combinedCollision;
        if (originalAttackTarget.isMoving()) {
            range += 50;
            if (npc.isMoving()) {
                range += 50;
            }
        }

        if ((dist2 > range) || !GeoEngine.getInstance().canSeeTarget(npc, originalAttackTarget)) {
            if (originalAttackTarget.isMoving()) {
                range -= 100;
            }
            if (range < 5) {
                range = 5;
            }
            moveToPawn(originalAttackTarget, range);
            return;
        }

        actor.doAutoAttack(originalAttackTarget);
    }

    private void moteToTargetIfNeed(Attackable npc, Creature originalAttackTarget, int collision, int combinedCollision) {
        int newX = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newX += originalAttackTarget.getX();
        } else {
            newX = originalAttackTarget.getX() - newX;
        }

        int newY = combinedCollision + Rnd.get(40);
        if (Rnd.nextBoolean()) {
            newY += originalAttackTarget.getY();
        } else {
            newY = originalAttackTarget.getY() - newY;
        }

        if (!isInsideRadius2D(npc, newX, newY, collision)) {
            final int newZ = npc.getZ() + 30;
            if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ, npc.getInstanceWorld())) {
                moveTo(newX, newY, newZ);
            }
        }
    }

    @Override
    protected void thinkCast() {
        final WorldObject target = _skill.getTarget(actor, _forceUse, _dontMove, false);
        if (checkTargetLost(target)) {
            setTarget(null);
            return;
        }
        if (maybeMoveToPawn(target, actor.getMagicalAttackRange(_skill))) {
            return;
        }
        actor.doCast(_skill, _item, _forceUse, _dontMove);
    }
}
