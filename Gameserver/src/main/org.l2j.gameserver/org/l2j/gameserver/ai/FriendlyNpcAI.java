package org.l2j.gameserver.ai;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;

import static org.l2j.gameserver.ai.CtrlIntention.*;


/**
 * @author Sdw
 */
public class FriendlyNpcAI extends L2AttackableAI {
    public FriendlyNpcAI(L2Attackable attackable) {
        super(attackable);
    }

    @Override
    protected void onEvtAttacked(L2Character attacker) {

    }

    @Override
    protected void onEvtAggression(L2Character target, int aggro) {

    }

    @Override
    protected void onIntentionAttack(L2Character target) {
        if (target == null) {
            clientActionFailed();
            return;
        }

        if (getIntention() == AI_INTENTION_REST) {
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isControlBlocked()) {
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
        final L2Attackable npc = getActiveChar();
        if (npc.isCastingNow() || npc.isCoreAIDisabled()) {
            return;
        }

        final L2Object target = getTarget();
        final L2Character originalAttackTarget = (target != null) && target.isCharacter() ? (L2Character) target : null;
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
            for (L2Attackable nearby : L2World.getInstance().getVisibleObjects(npc, L2Attackable.class)) {
                if (npc.isInsideRadius2D(nearby, collision) && (nearby != originalAttackTarget)) {
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

                    if (!npc.isInsideRadius2D(newX, newY, 0, collision)) {
                        final int newZ = npc.getZ() + 30;
                        if (GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ, npc.getInstanceWorld())) {
                            moveTo(newX, newY, newZ);
                        }
                    }
                    return;
                }
            }
        }
        // Dodge if its needed
        if (!npc.isMovementDisabled() && (npc.getTemplate().getDodge() > 0)) {
            if (Rnd.get(100) <= npc.getTemplate().getDodge()) {
                final double distance2 = npc.calculateDistanceSq2D(originalAttackTarget);
                if (Math.sqrt(distance2) <= (60 + combinedCollision)) {
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

        final double dist = npc.calculateDistance2D(originalAttackTarget);
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

        _actor.doAutoAttack(originalAttackTarget);
    }

    @Override
    protected void thinkCast() {
        final L2Object target = _skill.getTarget(_actor, _forceUse, _dontMove, false);
        if (checkTargetLost(target)) {
            setTarget(null);
            return;
        }
        if (maybeMoveToPawn(target, _actor.getMagicalAttackRange(_skill))) {
            return;
        }
        _actor.doCast(_skill, _item, _forceUse, _dontMove);
    }
}
