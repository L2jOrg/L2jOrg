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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.serverpackets.DeleteObject;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class PlayerAI extends PlayableAI {
    private boolean _thinking; // to prevent recursive thinking

    private IntentionCommand _nextIntention = null;

    public PlayerAI(Player player) {
        super(player);
    }

    private void saveNextIntention(CtrlIntention intention, Object arg0, Object arg1) {
        _nextIntention = new IntentionCommand(intention, arg0, arg1);
    }

    @Override
    public IntentionCommand getNextIntention() {
        return _nextIntention;
    }

    /**
     * Saves the current Intention for this PlayerAI if necessary and calls changeIntention in AbstractAI.
     *
     * @param intention The new Intention to set to the AI
     * @param args      The first parameter of the Intention
     */
    @Override
    protected synchronized void changeIntention(CtrlIntention intention, Object... args) {
        final Object localArg0 = args.length > 0 ? args[0] : null;
        final Object localArg1 = args.length > 1 ? args[1] : null;

        final Object globalArg0 = (_intentionArgs != null) && (_intentionArgs.length > 0) ? _intentionArgs[0] : null;
        final Object globalArg1 = (_intentionArgs != null) && (_intentionArgs.length > 1) ? _intentionArgs[1] : null;

        // do nothing unless CAST intention
        // however, forget interrupted actions when starting to use an offensive skill
        if ((intention != CtrlIntention.AI_INTENTION_CAST) || ((Skill) args[0]).isBad()) {
            _nextIntention = null;
            super.changeIntention(intention, args);
            return;
        }

        // do nothing if next intention is same as current one.
        if ((intention == this.intention) && (globalArg0 == localArg0) && (globalArg1 == localArg1)) {
            super.changeIntention(intention, args);
            return;
        }

        // save current intention so it can be used after cast
        saveNextIntention(this.intention, globalArg0, globalArg1);
        super.changeIntention(intention, args);
    }

    /**
     * Launch actions corresponding to the Event ReadyToAct.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Launch actions corresponding to the Event Think</li>
     * </ul>
     */
    @Override
    protected void onEvtReadyToAct() {
        // Launch actions corresponding to the Event Think
        if (_nextIntention != null) {
            setIntention(_nextIntention._crtlIntention, _nextIntention._arg0, _nextIntention._arg1);
            _nextIntention = null;
        }
        super.onEvtReadyToAct();
    }

    /**
     * Launch actions corresponding to the Event Cancel.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop an AI Follow Task</li>
     * <li>Launch actions corresponding to the Event Think</li>
     * </ul>
     */
    @Override
    protected void onEvtCancel() {
        _nextIntention = null;
        super.onEvtCancel();
    }

    /**
     * Finalize the casting of a skill. This method overrides CreatureAI method.<br>
     * <B>What it does:</B><br>
     * Check if actual intention is set to CAST and, if so, retrieves latest intention before the actual CAST and set it as the current intention for the player.
     */
    @Override
    protected void onEvtFinishCasting() {
        if (getIntention() == CtrlIntention.AI_INTENTION_CAST) {
            // run interrupted or next intention

            final IntentionCommand nextIntention = _nextIntention;
            if (nextIntention != null) {
                if (nextIntention._crtlIntention != CtrlIntention.AI_INTENTION_CAST) // previous state shouldn't be casting
                {
                    setIntention(nextIntention._crtlIntention, nextIntention._arg0, nextIntention._arg1);
                } else {
                    setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
            } else {
                // set intention to idle if skill doesn't change intention.
                setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }
        }
    }

    @Override
    protected void onEvtAttacked(Creature attacker) {
        super.onEvtAttacked(attacker);

        // Summons in defending mode defend its master when attacked.
        if (actor.getActingPlayer().hasServitors()) {
            actor.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI) summon.getAI()).isDefending()).forEach(summon -> ((SummonAI) summon.getAI()).defendAttack(attacker));
        }
    }

    @Override
    protected void onEvtEvaded(Creature attacker) {
        super.onEvtEvaded(attacker);

        // Summons in defending mode defend its master when attacked.
        if (actor.getActingPlayer().hasServitors()) {
            actor.getActingPlayer().getServitors().values().stream().filter(summon -> ((SummonAI) summon.getAI()).isDefending()).forEach(summon -> ((SummonAI) summon.getAI()).defendAttack(attacker));
        }
    }

    @Override
    protected void onIntentionRest() {
        if (getIntention() != CtrlIntention.AI_INTENTION_REST) {
            changeIntention(CtrlIntention.AI_INTENTION_REST);
            setTarget(null);
            clientStopMoving(null);
        }
    }

    @Override
    protected void onIntentionActive() {
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    /**
     * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Set the Intention of this AI to AI_INTENTION_MOVE_TO</li>
     * <li>Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)</li>
     * </ul>
     * @param loc
     */
    @Override
    protected void onIntentionMoveTo(ILocational loc) {
        if (getIntention() == CtrlIntention.AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the Player actor
            clientActionFailed();
            return;
        }

        if (actor.isAllSkillsDisabled() || actor.isCastingNow() || actor.isAttackingNow()) {
            clientActionFailed();
            saveNextIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc, null);
            return;
        }

        // Set the Intention of this AbstractAI to AI_INTENTION_MOVE_TO
        changeIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

        // Abort the attack of the Creature and send Server->Client ActionFailed packet
        actor.abortAttack();

        // Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
        moveTo(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    protected void clientNotifyDead() {
        _clientMovingToPawnOffset = 0;
        _clientMoving = false;

        super.clientNotifyDead();
    }

    private void thinkAttack() {
        final WorldObject target = getTarget();
        if (!isCreature(target)) {
            return;
        }
        if (checkTargetLostOrDead((Creature) target)) {
            // Notify the target
            setTarget(null);
            return;
        }
        if (maybeMoveToPawn(target, actor.getPhysicalAttackRange())) {
            return;
        }

        clientStopMoving(null);
        actor.doAutoAttack((Creature) target);
    }

    private void thinkCast() {
        final WorldObject target = _skill.getTarget(actor, _forceUse, _dontMove, false);
        if ((_skill.getTargetType() == TargetType.GROUND) && isPlayer(actor)) {
            if (maybeMoveToPosition(((Player) actor).getCurrentSkillWorldPosition(), actor.getMagicalAttackRange(_skill))) {
                return;
            }
        } else {
            if (checkTargetLost(target)) {
                if (_skill.isBad() && (target != null)) {
                    // Notify the target
                    setTarget(null);
                }
                return;
            }
            if ((target != null) && maybeMoveToPawn(target, actor.getMagicalAttackRange(_skill))) {
                return;
            }
        }

        actor.doCast(_skill, _item, _forceUse, _dontMove);
    }

    private void thinkPickUp() {
        if (actor.isAllSkillsDisabled() || actor.isCastingNow()) {
            return;
        }
        final WorldObject target = getTarget();
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, 36)) {
            return;
        }
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
        getActor().doPickupItem(target);
    }

    private void thinkInteract() {
        if (actor.isAllSkillsDisabled() || actor.isCastingNow()) {
            return;
        }
        final WorldObject target = getTarget();
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, 36)) {
            return;
        }
        if (!(target instanceof StaticWorldObject)) {
            getActor().doInteract((Creature) target);
        }
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public void onEvtThink() {
        if (_thinking && (getIntention() != CtrlIntention.AI_INTENTION_CAST)) {
            return;
        }

        _thinking = true;
        try {
            if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                thinkAttack();
            } else if (getIntention() == CtrlIntention.AI_INTENTION_CAST) {
                thinkCast();
            } else if (getIntention() == CtrlIntention.AI_INTENTION_PICK_UP) {
                thinkPickUp();
            } else if (getIntention() == CtrlIntention.AI_INTENTION_INTERACT) {
                thinkInteract();
            }
        } finally {
            _thinking = false;
        }
    }

    @Override
    protected void onEvtForgetObject(WorldObject object) {
        super.onEvtForgetObject(object);
        actor.sendPacket(new DeleteObject(object));
    }

    @Override
    public Player getActor() {
        return (Player) super.getActor();
    }
}
