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
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Doppelganger;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.MoveToLocation;
import org.l2j.gameserver.world.WorldTimeController;

import static org.l2j.gameserver.util.GameUtils.isCreature;

public class DoppelgangerAI extends CreatureAI {
    private volatile boolean _thinking; // to prevent recursive thinking
    private volatile boolean _startFollow;
    private Creature _lastAttack = null;

    public DoppelgangerAI(Doppelganger clone) {
        super(clone);
    }

    @Override
    protected void onIntentionIdle() {
        stopFollow();
        _startFollow = false;
        onIntentionActive();
    }

    @Override
    protected void onIntentionActive() {
        if (_startFollow) {
            setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getActor().getSummoner());
        } else {
            super.onIntentionActive();
        }
    }

    private void thinkAttack() {
        final WorldObject target = getTarget();
        final Creature attackTarget = isCreature(target) ? (Creature) target : null;

        if (checkTargetLostOrDead(attackTarget)) {
            setTarget(null);
            return;
        }
        if (maybeMoveToPawn(target, actor.getPhysicalAttackRange())) {
            return;
        }
        clientStopMoving(null);
        actor.doAutoAttack(attackTarget);
    }

    private void thinkCast() {
        if (actor.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }

        final WorldObject target = _skill.getTarget(actor, _forceUse, _dontMove, false);

        if (checkTargetLost(target)) {
            setTarget(null);
            return;
        }
        final boolean val = _startFollow;
        if (maybeMoveToPawn(target, actor.getMagicalAttackRange(_skill))) {
            return;
        }
        getActor().followSummoner(false);
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
        _startFollow = val;
        actor.doCast(_skill, _item, _forceUse, _dontMove);
    }

    private void thinkInteract() {
        final WorldObject target = getTarget();
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, 36)) {
            return;
        }
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
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
            getActor().followSummoner(_startFollow);
        } else {
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, _lastAttack);
            _lastAttack = null;
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
                getActor().followSummoner(_startFollow);
            }
        }
    }

    public void setStartFollowController(boolean val) {
        _startFollow = val;
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
        if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
            _lastAttack = isCreature(getTarget()) ? (Creature) getTarget() : null;
        } else {
            _lastAttack = null;
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }

    @Override
    public void moveToPawn(WorldObject pawn, int offset) {
        // Check if actor can move
        if (!actor.isMovementDisabled() && (actor.getMoveSpeed() > 0)) {
            if (offset < 10) {
                offset = 10;
            }

            // prevent possible extra calls to this function (there is none?),
            // also don't send movetopawn packets too often
            boolean sendPacket = true;
            if (_clientMoving && (getTarget() == pawn)) {
                if (_clientMovingToPawnOffset == offset) {
                    if (WorldTimeController.getInstance().getGameTicks() < _moveToPawnTimeout) {
                        return;
                    }
                    sendPacket = false;
                } else if (actor.isOnGeodataPath()) {
                    // minimum time to calculate new route is 2 seconds
                    if (WorldTimeController.getInstance().getGameTicks() < (_moveToPawnTimeout + 10)) {
                        return;
                    }
                }
            }

            // Set AI movement data
            _clientMoving = true;
            _clientMovingToPawnOffset = offset;
            setTarget(pawn);
            _moveToPawnTimeout = WorldTimeController.getInstance().getGameTicks();
            _moveToPawnTimeout += 1000 / WorldTimeController.MILLIS_IN_TICK;

            if (pawn == null) {
                return;
            }

            // Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
            // actor.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);
            final Location loc = new Location(pawn.getX() + Rnd.get(-offset, offset), pawn.getY() + Rnd.get(-offset, offset), pawn.getZ());
            actor.moveToLocation(loc.getX(), loc.getY(), loc.getZ(), 0);

            if (!actor.isMoving()) {
                clientActionFailed();
                return;
            }

            // Doppelgangers always send MoveToLocation packet.
            if (sendPacket) {
                actor.broadcastPacket(new MoveToLocation(actor));
            }
        } else {
            clientActionFailed();
        }
    }

    @Override
    public Doppelganger getActor() {
        return (Doppelganger) super.getActor();
    }
}
