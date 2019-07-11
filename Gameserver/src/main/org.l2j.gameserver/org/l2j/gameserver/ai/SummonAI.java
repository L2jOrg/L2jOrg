package org.l2j.gameserver.ai;

import org.l2j.commons.util.Rnd;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.concurrent.Future;

import static org.l2j.gameserver.ai.CtrlIntention.*;

public class SummonAI extends PlayableAI implements Runnable {
    private static final int AVOID_RADIUS = 70;

    private volatile boolean _thinking; // to prevent recursive thinking
    private volatile boolean _startFollow = ((Summon) _actor).getFollowStatus();
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
        final Summon summon = (Summon) _actor;
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
        final Creature attackTarget = (target != null) && target.isCharacter() ? (Creature) target : null;

        if (checkTargetLostOrDead(attackTarget)) {
            setTarget(null);
            ((Summon) _actor).setFollowStatus(true);
            return;
        }
        if (maybeMoveToPawn(attackTarget, _actor.getPhysicalAttackRange())) {
            return;
        }
        clientStopMoving(null);
        _actor.doAutoAttack(attackTarget);
    }

    private void thinkCast() {
        final Summon summon = (Summon) _actor;
        if (summon.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }

        final WorldObject target = _skill.getTarget(_actor, _forceUse, _dontMove, false);
        if (checkTargetLost(target)) {
            setTarget(null);
            summon.setFollowStatus(true);
            return;
        }
        final boolean val = _startFollow;
        if (maybeMoveToPawn(target, _actor.getMagicalAttackRange(_skill))) {
            return;
        }
        summon.setFollowStatus(false);
        setIntention(AI_INTENTION_IDLE);
        _startFollow = val;
        _actor.doCast(_skill, _item, _forceUse, _dontMove);
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
    protected void onEvtThink() {
        if (_thinking || _actor.isCastingNow() || _actor.isAllSkillsDisabled()) {
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
            ((Summon) _actor).setFollowStatus(_startFollow);
        } else {
            setIntention(AI_INTENTION_ATTACK, _lastAttack);
            _lastAttack = null;
        }
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {
        super.onEvtAttacked(attacker);

        if (_isDefending) {
            defendAttack(attacker);
        } else {
            avoidAttack(attacker);
        }
    }

    @Override
    protected void onEvtEvaded(Creature attacker) {
        super.onEvtEvaded(attacker);

        if (_isDefending) {
            defendAttack(attacker);
        } else {
            avoidAttack(attacker);
        }
    }

    private void avoidAttack(Creature attacker) {
        // Don't move while casting. It breaks casting animation, but still casts the skill... looks so bugged.
        if (_actor.isCastingNow()) {
            return;
        }

        final Creature owner = getActor().getOwner();
        // trying to avoid if summon near owner
        if ((owner != null) && (owner != attacker) && owner.isInsideRadius3D(_actor, 2 * AVOID_RADIUS)) {
            _startAvoid = true;
        }
    }

    public void defendAttack(Creature attacker) {
        // Cannot defend while attacking or casting.
        if (_actor.isAttackingNow() || _actor.isCastingNow()) {
            return;
        }

        final Summon summon = getActor();
        if ((summon.getOwner() != null) && (summon.getOwner() != attacker) && !summon.isMoving() && summon.canAttack(attacker, false) && summon.getOwner().isInsideRadius3D(_actor, 2 * AVOID_RADIUS)) {
            summon.doAutoAttack(attacker);
        }
    }

    @Override
    public void run() {
        if (_startAvoid) {
            _startAvoid = false;

            if (!_clientMoving && !_actor.isDead() && !_actor.isMovementDisabled() && (_actor.getMoveSpeed() > 0)) {
                final int ownerX = ((Summon) _actor).getOwner().getX();
                final int ownerY = ((Summon) _actor).getOwner().getY();
                final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());

                final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
                final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
                if (GeoEngine.getInstance().canMoveToTarget(_actor.getX(), _actor.getY(), _actor.getZ(), targetX, targetY, _actor.getZ(), _actor.getInstanceWorld())) {
                    moveTo(targetX, targetY, _actor.getZ());
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
                ((Summon) _actor).setFollowStatus(_startFollow);
            }
        }
    }

    public void setStartFollowController(boolean val) {
        _startFollow = val;
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
        if (getIntention() == AI_INTENTION_ATTACK) {
            _lastAttack = (getTarget() != null) && getTarget().isCharacter() ? (Creature) getTarget() : null;
        } else {
            _lastAttack = null;
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }

    private void startAvoidTask() {
        if (_avoidTask == null) {
            _avoidTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 100, 100);
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
