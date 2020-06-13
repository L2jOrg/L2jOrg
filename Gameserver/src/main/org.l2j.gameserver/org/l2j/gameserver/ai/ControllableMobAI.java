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
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.MobGroup;
import org.l2j.gameserver.model.MobGroupTable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.*;

/**
 * AI for controllable mobs
 *
 * @author littlecrow
 */
public final class ControllableMobAI extends AttackableAI {
    public static final int AI_IDLE = 1;
    public static final int AI_NORMAL = 2;
    public static final int AI_FORCEATTACK = 3;
    public static final int AI_FOLLOW = 4;
    public static final int AI_CAST = 5;
    public static final int AI_ATTACK_GROUP = 6;

    private int _alternateAI;

    private boolean _isThinking; // to prevent thinking recursively
    private boolean _isNotMoving;

    private Creature _forcedTarget;
    private MobGroup _targetGroup;

    public ControllableMobAI(ControllableMob controllableMob) {
        super(controllableMob);
        setAlternateAI(AI_IDLE);
    }

    protected void thinkFollow() {
        final Attackable me = (Attackable) actor;

        if (!GameUtils.checkIfInRange(MobGroupTable.FOLLOW_RANGE, me, getForcedTarget(), true)) {
            final int signX = Rnd.nextBoolean() ? -1 : 1;
            final int signY = Rnd.nextBoolean() ? -1 : 1;
            final int randX = Rnd.get(MobGroupTable.FOLLOW_RANGE);
            final int randY = Rnd.get(MobGroupTable.FOLLOW_RANGE);

            moveTo(getForcedTarget().getX() + (signX * randX), getForcedTarget().getY() + (signY * randY), getForcedTarget().getZ());
        }
    }

    @Override
    public void onEvtThink() {
        if (_isThinking) {
            return;
        }

        setThinking(true);

        try {
            switch (_alternateAI) {
                case AI_IDLE: {
                    if (getIntention() != AI_INTENTION_ACTIVE) {
                        setIntention(AI_INTENTION_ACTIVE);
                    }
                    break;
                }
                case AI_FOLLOW: {
                    thinkFollow();
                    break;
                }
                case AI_CAST: {
                    thinkCast();
                    break;
                }
                case AI_FORCEATTACK: {
                    thinkForceAttack();
                    break;
                }
                case AI_ATTACK_GROUP: {
                    thinkAttackGroup();
                    break;
                }
                default: {
                    if (getIntention() == AI_INTENTION_ACTIVE) {
                        thinkActive();
                    } else if (getIntention() == AI_INTENTION_ATTACK) {
                        thinkAttack();
                    }
                    break;
                }
            }
        } finally {
            setThinking(false);
        }
    }

    @Override
    protected void thinkCast() {
        WorldObject target = _skill.getTarget(actor, _forceUse, _dontMove, false);
        if (!isCreature(target) || ((Creature) target).isAlikeDead()) {
            target = _skill.getTarget(actor, findNextRndTarget(), _forceUse, _dontMove, false);
        }

        if (target == null) {
            return;
        }

        setTarget(target);

        if (!actor.isMuted()) {
            int max_range = 0;
            // check distant skills

            for (Skill sk : actor.getAllSkills()) {
                if (GameUtils.checkIfInRange(sk.getCastRange(), actor, target, true) && !actor.isSkillDisabled(sk) && (actor.getCurrentMp() > actor.getStats().getMpConsume(sk))) {
                    actor.doCast(sk);
                    return;
                }

                max_range = Math.max(max_range, sk.getCastRange());
            }

            if (!_isNotMoving) {
                moveToPawn(target, max_range);
            }

            return;
        }
    }

    protected void thinkAttackGroup() {
        final Creature target = getForcedTarget();
        if ((target == null) || target.isAlikeDead()) {
            // try to get next group target
            setForcedTarget(findNextGroupTarget());
            clientStopMoving(null);
        }

        if (target == null) {
            return;
        }

        setTarget(target);
        // as a response, we put the target in a forcedattack mode
        final ControllableMob theTarget = (ControllableMob) target;
        final ControllableMobAI ctrlAi = (ControllableMobAI) theTarget.getAI();
        ctrlAi.forceAttack(actor);

        final double dist2 = calculateDistanceSq2D(actor, target);
        final int range = actor.getPhysicalAttackRange() + actor.getTemplate().getCollisionRadius() + target.getTemplate().getCollisionRadius();
        int max_range = range;

        if (!actor.isMuted() && (dist2 > ((range + 20) * (range + 20)))) {
            // check distant skills
            for (Skill sk : actor.getAllSkills()) {
                final int castRange = sk.getCastRange();

                if (((castRange * castRange) >= dist2) && !actor.isSkillDisabled(sk) && (actor.getCurrentMp() > actor.getStats().getMpConsume(sk))) {
                    actor.doCast(sk);
                    return;
                }

                max_range = Math.max(max_range, castRange);
            }

            if (!_isNotMoving) {
                moveToPawn(target, range);
            }

            return;
        }
        actor.doAutoAttack(target);
    }

    protected void thinkForceAttack() {
        if ((getForcedTarget() == null) || getForcedTarget().isAlikeDead()) {
            clientStopMoving(null);
            setIntention(AI_INTENTION_ACTIVE);
            setAlternateAI(AI_IDLE);
        }

        setTarget(getForcedTarget());
        final double dist2 = calculateDistanceSq2D(actor, getForcedTarget());
        final int range = actor.getPhysicalAttackRange() + actor.getTemplate().getCollisionRadius() + getForcedTarget().getTemplate().getCollisionRadius();
        int max_range = range;

        if (!actor.isMuted() && (dist2 > ((range + 20) * (range + 20)))) {
            // check distant skills
            for (Skill sk : actor.getAllSkills()) {
                final int castRange = sk.getCastRange();

                if (((castRange * castRange) >= dist2) && !actor.isSkillDisabled(sk) && (actor.getCurrentMp() > actor.getStats().getMpConsume(sk))) {
                    actor.doCast(sk);
                    return;
                }

                max_range = Math.max(max_range, castRange);
            }

            if (!_isNotMoving) {
                moveToPawn(getForcedTarget(), actor.getPhysicalAttackRange()/* range */);
            }

            return;
        }

        actor.doAutoAttack(getForcedTarget());
    }

    @Override
    protected void thinkAttack() {
        Creature target = getForcedTarget();
        if ((target == null) || target.isAlikeDead()) {
            if (target != null) {
                // stop hating
                final Attackable npc = (Attackable) actor;
                npc.stopHating(target);
            }

            setIntention(AI_INTENTION_ACTIVE);
        } else {
            // notify aggression
            final Creature finalTarget = target;
            if (((Npc) actor).getTemplate().getClans() != null) {
                World.getInstance().forEachVisibleObject(actor, Npc.class, npc ->
                {
                    if (!npc.isInMyClan((Npc) actor)) {
                        return;
                    }

                    if (isInsideRadius3D(actor, npc, npc.getTemplate().getClanHelpRange())) {
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, finalTarget, 1);
                    }
                });
            }

            setTarget(target);
            final double dist2 = calculateDistanceSq2D(actor, target);
            final int range = actor.getPhysicalAttackRange() + actor.getTemplate().getCollisionRadius() + target.getTemplate().getCollisionRadius();
            int max_range = range;

            if (!actor.isMuted() && (dist2 > ((range + 20) * (range + 20)))) {
                // check distant skills
                for (Skill sk : actor.getAllSkills()) {
                    final int castRange = sk.getCastRange();

                    if (((castRange * castRange) >= dist2) && !actor.isSkillDisabled(sk) && (actor.getCurrentMp() > actor.getStats().getMpConsume(sk))) {
                        actor.doCast(sk);
                        return;
                    }

                    max_range = Math.max(max_range, castRange);
                }

                moveToPawn(target, range);
                return;
            }

            // Force mobs to attack anybody if confused.
            Creature hated;

            if (actor.isConfused()) {
                hated = findNextRndTarget();
            } else {
                hated = target;
            }

            if (hated == null) {
                setIntention(AI_INTENTION_ACTIVE);
                return;
            }

            if (hated != target) {
                target = hated;
            }

            if (!actor.isMuted() && (Rnd.get(5) == 3)) {
                for (Skill sk : actor.getAllSkills()) {
                    final int castRange = sk.getCastRange();

                    if (((castRange * castRange) >= dist2) && !actor.isSkillDisabled(sk) && (actor.getCurrentMp() < actor.getStats().getMpConsume(sk))) {
                        actor.doCast(sk);
                        return;
                    }
                }
            }

            actor.doAutoAttack(target);
        }
    }

    @Override
    protected void thinkActive() {
        Creature hated;

        if (actor.isConfused()) {
            hated = findNextRndTarget();
        } else {
            final WorldObject target = actor.getTarget();
            hated = isCreature(target) ? (Creature) target : null;
        }

        if (hated != null) {
            actor.setRunning();
            setIntention(AI_INTENTION_ATTACK, hated);
        }
    }

    private boolean checkAutoAttackCondition(Creature target) {
        if (!isAttackable(actor)) {
            return false;
        }
        final Attackable me = (Attackable) actor;

        if (isNpc(target) || isDoor(target)) {
            return false;
        }

        if (target.isAlikeDead() || !isInsideRadius2D(me, target, me.getAggroRange()) || (Math.abs(actor.getZ() - target.getZ()) > 100)) {
            return false;
        }

        // Check if the target isn't invulnerable
        if (target.isInvul()) {
            return false;
        }

        // Spawn protection (only against mobs)
        if (isPlayer(target) && ((Player) target).isSpawnProtected()) {
            return false;
        }

        // Check if the target is a Playable
        if (isPlayable(target)) {
            // Check if the target isn't in silent move mode
            if (((Playable) target).isSilentMovingAffected()) {
                return false;
            }
        }

        if (isNpc(target)) {
            return false;
        }

        return me.isAggressive();
    }

    private Creature findNextRndTarget() {
        final List<Creature> potentialTarget = new ArrayList<>();
        World.getInstance().forEachVisibleObject(actor, Creature.class, target ->
        {
            if (GameUtils.checkIfInShortRange(((Attackable) actor).getAggroRange(), actor, target, true) && checkAutoAttackCondition(target)) {
                potentialTarget.add(target);
            }
        });

        return !potentialTarget.isEmpty() ? potentialTarget.get(Rnd.get(potentialTarget.size())) : null;
    }

    private ControllableMob findNextGroupTarget() {
        return getGroupTarget().getRandomMob();
    }

    public int getAlternateAI() {
        return _alternateAI;
    }

    public void setAlternateAI(int _alternateai) {
        _alternateAI = _alternateai;
    }

    public void forceAttack(Creature target) {
        setAlternateAI(AI_FORCEATTACK);
        setForcedTarget(target);
    }

    public void forceAttackGroup(MobGroup group) {
        setForcedTarget(null);
        setGroupTarget(group);
        setAlternateAI(AI_ATTACK_GROUP);
    }

    public void stop() {
        setAlternateAI(AI_IDLE);
        clientStopMoving(null);
    }

    public void move(int x, int y, int z) {
        moveTo(x, y, z);
    }

    public void follow(Creature target) {
        setAlternateAI(AI_FOLLOW);
        setForcedTarget(target);
    }

    public boolean isThinking() {
        return _isThinking;
    }

    public void setThinking(boolean isThinking) {
        _isThinking = isThinking;
    }

    public boolean isNotMoving() {
        return _isNotMoving;
    }

    public void setNotMoving(boolean isNotMoving) {
        _isNotMoving = isNotMoving;
    }

    private Creature getForcedTarget() {
        return _forcedTarget;
    }

    private void setForcedTarget(Creature forcedTarget) {
        _forcedTarget = forcedTarget;
    }

    private MobGroup getGroupTarget() {
        return _targetGroup;
    }

    private void setGroupTarget(MobGroup targetGroup) {
        _targetGroup = targetGroup;
    }
}
