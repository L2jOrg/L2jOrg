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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.concurrent.Future;

import static org.l2j.gameserver.ai.CtrlIntention.*;
import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public class SummonAI extends PlayableAI implements Runnable {
    private static final int AVOID_RADIUS = 70;

    private volatile boolean _thinking; // to prevent recursive thinking
    private volatile boolean _startFollow = ((Summon) actor).getFollowStatus();
    private Creature _lastAttack = null;

    private volatile boolean _startAvoid;
    private volatile boolean _isDefending;
    private Future<?> _avoidTask = null;

    public SummonAI(Summon summon) {
        super(summon);
    }

    @Override
    protected void onIntentionIdle() {
        stopFollow();
        _startFollow = false;
        onIntentionActive();
    }

    @Override
    protected void onIntentionActive() {
        final Summon summon = (Summon) actor;
        if (_startFollow) {
            setIntention(AI_INTENTION_FOLLOW, summon.getOwner());
        } else {
            super.onIntentionActive();
        }
    }

    @Override
    synchronized void changeIntention(CtrlIntention intention, Object... args) {
        switch (intention) {
            case AI_INTENTION_ACTIVE:
            case AI_INTENTION_FOLLOW: {
                startAvoidTask();
                break;
            }
            default: {
                stopAvoidTask();
            }
        }

        super.changeIntention(intention, args);
    }

    private void thinkAttack() {
        final WorldObject target = getTarget();
        final Creature attackTarget = isCreature(target) ? (Creature) target : null;

        if (checkTargetLostOrDead(attackTarget)) {
            setTarget(null);
            ((Summon) actor).setFollowStatus(true);
            return;
        }
        if (maybeMoveToPawn(attackTarget, actor.getPhysicalAttackRange())) {
            return;
        }
        clientStopMoving(null);
        actor.doAutoAttack(attackTarget);
    }

    private void thinkCast() {
        final Summon summon = (Summon) actor;
        if (summon.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }

        final WorldObject target = _skill.getTarget(actor, _forceUse, _dontMove, false);
        if (checkTargetLost(target)) {
            setTarget(null);
            summon.setFollowStatus(true);
            return;
        }
        final boolean val = _startFollow;
        if (maybeMoveToPawn(target, actor.getMagicalAttackRange(_skill))) {
            return;
        }
        summon.setFollowStatus(false);
        setIntention(AI_INTENTION_IDLE);
        _startFollow = val;
        actor.doCast(_skill, _item, _forceUse, _dontMove);
    }

    private void thinkPickUp() {
        final WorldObject target = getTarget();
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, 36)) {
            return;
        }
        setIntention(AI_INTENTION_IDLE);
        getActor().doPickupItem(target);
    }

    private void thinkInteract() {
        final WorldObject target = getTarget();
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, 36)) {
            return;
        }
        setIntention(AI_INTENTION_IDLE);
    }

    @Override
    public void onEvtThink() {
        if (_thinking || actor.isCastingNow() || actor.isAllSkillsDisabled()) {
            return;
        }
        _thinking = true;
        try {
            switch (getIntention()) {
                case AI_INTENTION_ATTACK: {
                    thinkAttack();
                    break;
                }
                case AI_INTENTION_CAST: {
                    thinkCast();
                    break;
                }
                case AI_INTENTION_PICK_UP: {
                    thinkPickUp();
                    break;
                }
                case AI_INTENTION_INTERACT: {
                    thinkInteract();
                    break;
                }
            }
        } finally {
            _thinking = false;
        }
    }

    @Override
    protected void onEvtFinishCasting() {
        if (_lastAttack == null) {
            ((Summon) actor).setFollowStatus(_startFollow);
        } else {
            setIntention(AI_INTENTION_ATTACK, _lastAttack);
            _lastAttack = null;
        }
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {
        super.onEvtAttacked(attacker);

        if (_isDefending) {
            allServitorsDefend(attacker);
        } else {
            avoidAttack(attacker);
        }
    }

    @Override
    protected void onEvtEvaded(Creature attacker) {
        super.onEvtEvaded(attacker);

        if (_isDefending) {
            allServitorsDefend(attacker);
        } else {
            avoidAttack(attacker);
        }
    }

    private void avoidAttack(Creature attacker) {
        // Don't move while casting. It breaks casting animation, but still casts the skill... looks so bugged.
        if (actor.isCastingNow()) {
            return;
        }

        final Creature owner = getActor().getOwner();
        // trying to avoid if summon near owner
        if ((owner != null) && (owner != attacker) && isInsideRadius3D(owner, actor, 2 * AVOID_RADIUS)) {
            _startAvoid = true;
        }
    }

    public void defendAttack(Creature attacker) {
        // Cannot defend while attacking or casting.
        if (actor.isAttackingNow() || actor.isCastingNow()) {
            return;
        }

        final Summon summon = getActor();
        if ((summon.getOwner() != null) && (summon.getOwner() != attacker) && !summon.isMoving() && summon.canAttack(attacker, false))
        {
            summon.doAttack(attacker);
        }
    }

    @Override
    public void run() {
        if (_startAvoid) {
            _startAvoid = false;

            if (!_clientMoving && !actor.isDead() && !actor.isMovementDisabled() && (actor.getMoveSpeed() > 0)) {
                final int ownerX = ((Summon) actor).getOwner().getX();
                final int ownerY = ((Summon) actor).getOwner().getY();
                final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - actor.getY(), ownerX - actor.getX());

                final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
                final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
                if (GeoEngine.getInstance().canMoveToTarget(actor.getX(), actor.getY(), actor.getZ(), targetX, targetY, actor.getZ(), actor.getInstanceWorld())) {
                    moveTo(targetX, targetY, actor.getZ());
                }
            }
        }
    }

    public void notifyFollowStatusChange() {
        _startFollow = !_startFollow;
        switch (getIntention()) {
            case AI_INTENTION_ACTIVE:
            case AI_INTENTION_FOLLOW:
            case AI_INTENTION_IDLE:
            case AI_INTENTION_MOVE_TO:
            case AI_INTENTION_PICK_UP: {
                ((Summon) actor).setFollowStatus(_startFollow);
            }
        }
    }

    public void setStartFollowController(boolean val) {
        _startFollow = val;
    }

    private void allServitorsDefend(Creature attacker)
    {
        final Creature Owner = getActor().getOwner();
        if ((Owner != null) && Owner.getActingPlayer().hasServitors())
        {
            Owner.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI) summon.getAI()).isDefending()).forEach(summon -> ((SummonAI) summon.getAI()).defendAttack(attacker));
        }
        else
        {
            defendAttack(attacker);
        }
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
        if (getIntention() == AI_INTENTION_ATTACK) {
            _lastAttack = (getTarget() != null) && isCreature(getTarget()) ? (Creature) getTarget() : null;
        } else {
            _lastAttack = null;
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }

    private void startAvoidTask() {
        if (_avoidTask == null) {
            _avoidTask = ThreadPool.scheduleAtFixedRate(this, 100, 100);
        }
    }

    private void stopAvoidTask() {
        if (_avoidTask != null) {
            _avoidTask.cancel(false);
            _avoidTask = null;
        }
    }

    @Override
    public void stopAITask() {
        stopAvoidTask();
        super.stopAITask();
    }

    @Override
    public Summon getActor() {
        return (Summon) super.getActor();
    }

    /**
     * @return if the summon is defending itself or master.
     */
    public boolean isDefending() {
        return _isDefending;
    }

    /**
     * @param isDefending set the summon to defend itself and master, or be passive and avoid while being attacked.
     */
    public void setDefending(boolean isDefending) {
        _isDefending = isDefending;
    }
}
