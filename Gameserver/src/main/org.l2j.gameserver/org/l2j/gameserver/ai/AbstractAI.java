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
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.taskmanager.CreatureFollowTaskManager;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static org.l2j.gameserver.ai.CtrlIntention.*;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Mother class of all objects AI in the world.<br>
 * AbastractAI :<br>
 * <li>CreatureAI</li>
 */
public abstract class AbstractAI implements Ctrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAI.class);

    /**
     * The character that this AI manages
     */
    protected final Creature actor;
    /**
     * Current long-term intention
     */
    protected CtrlIntention intention = AI_INTENTION_IDLE;
    /**
     * Current long-term intention parameter
     */
    protected Object[] _intentionArgs = null;
    /**
     * Flags about client's state, in order to know which messages to send
     */
    protected volatile boolean _clientMoving;
    /**
     * Flags about client's state, in order to know which messages to send
     */
    protected int _clientMovingToPawnOffset;
    /**
     * Different internal state flags
     */
    protected int _moveToPawnTimeout;
    /**
     * The skill we are currently casting by INTENTION_CAST
     */
    Skill _skill;
    Item _item;
    boolean _forceUse;
    boolean _dontMove;
    /**
     * Flags about client's state, in order to know which messages to send
     */
    private volatile boolean _clientAutoAttacking;
    /**
     * Different targets this AI maintains
     */
    private WorldObject _target;
    private Future<?> _followTask = null;

    protected AbstractAI(Creature creature) {
        actor = creature;
    }

    private NextAction _nextAction;

    /**
     * @return the _nextAction
     */
    public NextAction getNextAction()
    {
        return _nextAction;
    }

    /**
     * @param nextAction the next action to set.
     */
    public void setNextAction(NextAction nextAction)
    {
        _nextAction = nextAction;
    }

    /**
     * @return the Creature managed by this Accessor AI.
     */
    @Override
    public Creature getActor() {
        return actor;
    }

    /**
     * @return the current Intention.
     */
    @Override
    public CtrlIntention getIntention() {
        return intention;
    }

    /**
     * Launch the CreatureAI onIntention method corresponding to the new Intention.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT>
     *
     * @param intention The new Intention to set to the AI
     */
    @Override
    public final void setIntention(CtrlIntention intention) {
        setIntention(intention, null, null);
    }

    /**
     * Set the Intention of this AbstractAI.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is USED by AI classes</B></FONT><B><U><br>
     * Overridden in </U> : </B><BR>
     * <B>AttackableAI</B> : Create an AI Task executed every 1s (if necessary)<BR>
     * <B>PlayerAI</B> : Stores the current AI intention parameters to later restore it if necessary.
     *
     * @param intention The new Intention to set to the AI
     * @param args      The first parameter of the Intention
     */
    synchronized void changeIntention(CtrlIntention intention, Object... args) {
        this.intention = intention;
        _intentionArgs = args;
    }

    /**
     * Launch the CreatureAI onIntention method corresponding to the new Intention.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Stop the FOLLOW mode if necessary</B></FONT>
     *
     * @param intention The new Intention to set to the AI
     * @param args      The first parameters of the Intention (optional target)
     */
    @Override
    @SafeVarargs
    public final void setIntention(CtrlIntention intention, Object... args) {
        // Stop the follow mode if necessary
        if ((intention != AI_INTENTION_FOLLOW) && (intention != AI_INTENTION_ATTACK)) {
            stopFollow();
        }

        // Launch the onIntention method of the CreatureAI corresponding to the new Intention
        switch (intention) {
            case AI_INTENTION_IDLE: {
                onIntentionIdle();
                break;
            }
            case AI_INTENTION_ACTIVE: {
                onIntentionActive();
                break;
            }
            case AI_INTENTION_REST: {
                onIntentionRest();
                break;
            }
            case AI_INTENTION_ATTACK: {
                onIntentionAttack((Creature) args[0]);
                break;
            }
            case AI_INTENTION_CAST: {
                onIntentionCast((Skill) args[0], (WorldObject) args[1], args.length > 2 ? (Item) args[2] : null, args.length > 3 && (boolean) args[3], args.length > 4 && (boolean) args[4]);
                break;
            }
            case AI_INTENTION_MOVE_TO: {
                onIntentionMoveTo((ILocational) args[0]);
                break;
            }
            case AI_INTENTION_FOLLOW: {
                onIntentionFollow((Creature) args[0]);
                break;
            }
            case AI_INTENTION_PICK_UP: {
                onIntentionPickUp((WorldObject) args[0]);
                break;
            }
            case AI_INTENTION_INTERACT: {
                onIntentionInteract((WorldObject) args[0]);
                break;
            }
        }

        // If do move or follow intention drop next action.
        if ((_nextAction != null) && _nextAction.getIntentions().contains(intention)) {
            _nextAction = null;
        }
    }

    /**
     * Launch the CreatureAI onEvt method corresponding to the Event.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned period)</B></FONT>
     *
     * @param evt The event whose the AI must be notified
     */
    @Override
    public final void notifyEvent(CtrlEvent evt) {
        notifyEvent(evt, null, null);
    }

    /**
     * Launch the CreatureAI onEvt method corresponding to the Event. <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned period)</B></FONT>
     *
     * @param evt  The event whose the AI must be notified
     * @param arg0 The first parameter of the Event (optional target)
     */
    @Override
    public final void notifyEvent(CtrlEvent evt, Object arg0) {
        notifyEvent(evt, arg0, null);
    }

    /**
     * Launch the CreatureAI onEvt method corresponding to the Event. <FONT COLOR=#FF0000><B> <U>Caution</U> : The current general intention won't be change (ex : If the character attack and is stunned, he will attack again after the stunned period)</B></FONT>
     *
     * @param evt  The event whose the AI must be notified
     * @param arg0 The first parameter of the Event (optional target)
     * @param arg1 The second parameter of the Event (optional target)
     */
    @Override
    public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1) {
        if ((!actor.isSpawned() && !actor.isTeleporting()) || !actor.hasAI()) {
            return;
        }

        switch (evt) {
            case EVT_THINK: {
                onEvtThink();
                break;
            }
            case EVT_ATTACKED: {
                onEvtAttacked((Creature) arg0);
                break;
            }
            case EVT_AGGRESSION: {
                onEvtAggression((Creature) arg0, ((Number) arg1).intValue());
                break;
            }
            case EVT_ACTION_BLOCKED: {
                onEvtActionBlocked((Creature) arg0);
                break;
            }
            case EVT_ROOTED: {
                onEvtRooted((Creature) arg0);
                break;
            }
            case EVT_CONFUSED: {
                onEvtConfused((Creature) arg0);
                break;
            }
            case EVT_MUTED: {
                onEvtMuted((Creature) arg0);
                break;
            }
            case EVT_EVADED: {
                onEvtEvaded((Creature) arg0);
                break;
            }
            case EVT_READY_TO_ACT: {
                if (!actor.isCastingNow()) {
                    onEvtReadyToAct();
                }
                break;
            }
            case EVT_ARRIVED: {
                // happens e.g. from stopmove but we don't process it if we're casting
                if (!actor.isCastingNow()) {
                    onEvtArrived();
                }
                break;
            }
            case EVT_ARRIVED_REVALIDATE: {
                // this is disregarded if the char is not moving any more
                if (actor.isMoving()) {
                    onEvtArrivedRevalidate();
                }
                break;
            }
            case EVT_ARRIVED_BLOCKED: {
                onEvtArrivedBlocked((Location) arg0);
                break;
            }
            case EVT_FORGET_OBJECT: {
                onEvtForgetObject((WorldObject) arg0);
                break;
            }
            case EVT_CANCEL: {
                onEvtCancel();
                break;
            }
            case EVT_DEAD: {
                onEvtDead();
                break;
            }
            case EVT_FAKE_DEATH: {
                onEvtFakeDeath();
                break;
            }
            case EVT_FINISH_CASTING: {
                onEvtFinishCasting();
                break;
            }
        }

        // Do next action.
        if ((_nextAction != null) && _nextAction.getEvents().contains(evt)) {
            _nextAction.doAction();
        }
    }

    protected abstract void onIntentionIdle();

    protected abstract void onIntentionActive();

    protected abstract void onIntentionRest();

    protected abstract void onIntentionAttack(Creature target);

    protected abstract void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove);

    protected abstract void onIntentionMoveTo(ILocational destination);

    protected abstract void onIntentionFollow(Creature target);

    protected abstract void onIntentionPickUp(WorldObject item);

    protected abstract void onIntentionInteract(WorldObject object);

    protected abstract void onEvtThink();

    protected abstract void onEvtAttacked(Creature attacker);

    protected abstract void onEvtAggression(Creature target, int aggro);

    protected abstract void onEvtActionBlocked(Creature attacker);

    protected abstract void onEvtRooted(Creature attacker);

    protected abstract void onEvtConfused(Creature attacker);

    protected abstract void onEvtMuted(Creature attacker);

    protected abstract void onEvtEvaded(Creature attacker);

    protected abstract void onEvtReadyToAct();

    protected abstract void onEvtArrived();

    protected abstract void onEvtArrivedRevalidate();

    protected abstract void onEvtArrivedBlocked(Location blocked_at_pos);

    protected abstract void onEvtForgetObject(WorldObject object);

    protected abstract void onEvtCancel();

    protected abstract void onEvtDead();

    protected abstract void onEvtFakeDeath();

    protected abstract void onEvtFinishCasting();

    /**
     * Cancel action client side by sending Server->Client packet ActionFailed to the Player actor. <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     */
    protected void clientActionFailed() {
        if (isPlayer(actor)) {
            actor.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    /**
     * Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     *
     * @param pawn
     * @param offset
     */
    public void moveToPawn(WorldObject pawn, int offset) {
        // Check if actor can move
        if (!actor.isMovementDisabled() && !actor.isAttackingNow() && !actor.isCastingNow()) {
            if (offset < 10) {
                offset = 10;
            }

            // prevent possible extra calls to this function (there is none?),
            // also don't send movetopawn packets too often
            if (_clientMoving && (_target == pawn)) {
                if (_clientMovingToPawnOffset == offset) {
                    if (WorldTimeController.getInstance().getGameTicks() < _moveToPawnTimeout) {
                        return;
                    }
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
            _target = pawn;
            _moveToPawnTimeout = WorldTimeController.getInstance().getGameTicks();
            _moveToPawnTimeout += 1000 / WorldTimeController.MILLIS_IN_TICK;

            if (pawn == null) {
                return;
            }

            // Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
            actor.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);

            if (!actor.isMoving()) {
                clientActionFailed();
                return;
            }

            // Send a Server->Client packet MoveToPawn/CharMoveToLocation to the actor and all Player in its _knownPlayers
            if (isCreature(pawn)) {
                if (actor.isOnGeodataPath()) {
                    actor.broadcastPacket(new MoveToLocation(actor));
                    _clientMovingToPawnOffset = 0;
                } else {
                    actor.broadcastPacket(new MoveToPawn(actor, pawn, offset));
                }
            } else {
                actor.broadcastPacket(new MoveToLocation(actor));
            }
        } else {
            clientActionFailed();
        }
    }

    public void moveTo(ILocational loc) {
        moveTo(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     *
     * @param x
     * @param y
     * @param z
     */
    protected void moveTo(int x, int y, int z) {
        // Check if actor can move
        if (!actor.isMovementDisabled()) {
            // Set AI movement data
            _clientMoving = true;
            _clientMovingToPawnOffset = 0;

            // Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
            actor.moveToLocation(x, y, z, 0);

            // Send a Server->Client packet CharMoveToLocation to the actor and all Player in its _knownPlayers
            actor.broadcastPacket(new MoveToLocation(actor));
        } else {
            clientActionFailed();
        }
    }

    /**
     * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     *
     * @param loc
     */
    public void clientStopMoving(Location loc) {
        // Stop movement of the Creature
        if (actor.isMoving()) {
            actor.stopMove(loc);
        }

        _clientMovingToPawnOffset = 0;
        _clientMoving = false;
    }

    /**
     * Client has already arrived to target, no need to force StopMove packet.
     */
    protected void clientStoppedMoving() {
        if (_clientMovingToPawnOffset > 0) // movetoPawn needs to be stopped
        {
            _clientMovingToPawnOffset = 0;
            actor.broadcastPacket(new StopMove(actor));
        }
        _clientMoving = false;
    }

    public boolean isAutoAttacking() {
        return _clientAutoAttacking;
    }

    public void setAutoAttacking(boolean isAutoAttacking) {
        if (isSummon(actor)) {
            final Summon summon = (Summon) actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().setAutoAttacking(isAutoAttacking);
            }
            return;
        }
        _clientAutoAttacking = isAutoAttacking;
    }

    /**
     * Start the actor Auto Attack client side by sending Server->Client packet AutoAttackStart <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     */
    public void clientStartAutoAttack() {
        if (isSummon(actor)) {
            final Summon summon = (Summon) actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().clientStartAutoAttack();
            }
            return;
        }
        if (!_clientAutoAttacking) {
            if (isPlayer(actor) && actor.hasSummon()) {
                final Summon pet = actor.getPet();
                if (pet != null) {
                    pet.broadcastPacket(new AutoAttackStart(pet.getObjectId()));
                }
                actor.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStart(s.getObjectId())));
            }
            // Send a Server->Client packet AutoAttackStart to the actor and all Player in its _knownPlayers
            actor.broadcastPacket(new AutoAttackStart(actor.getObjectId()));
            setAutoAttacking(true);
        }
        AttackStanceTaskManager.getInstance().addAttackStanceTask(actor);
    }

    /**
     * Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     */
    void clientStopAutoAttack() {
        if (isSummon(actor)) {
            final Summon summon = (Summon) actor;
            if (summon.getOwner() != null) {
                summon.getOwner().getAI().clientStopAutoAttack();
            }
            return;
        }
        if (isPlayer(actor)) {
            if (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(actor) && isAutoAttacking()) {
                AttackStanceTaskManager.getInstance().addAttackStanceTask(actor);
            }
        } else if (_clientAutoAttacking) {
            actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
            setAutoAttacking(false);
        }
    }

    /**
     * Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die <I>(broadcast)</I>.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     */
    protected void clientNotifyDead() {
        // Send a Server->Client packet Die to the actor and all Player in its _knownPlayers
        final Die msg = new Die(actor);
        actor.broadcastPacket(msg);

        // Init AI
        intention = AI_INTENTION_IDLE;
        _target = null;

        // Cancel the follow task if necessary
        stopFollow();
    }

    /**
     * Update the state of this actor client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the Player player.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
     *
     * @param player The L2PcIstance to notify with state of this Creature
     */
    public void describeStateToPlayer(Player player) {
        if (actor.isVisibleFor(player)) {
            if (_clientMoving) {
                if ((_clientMovingToPawnOffset != 0) && isFollowing()) {
                    // Send a Server->Client packet MoveToPawn to the actor and all Player in its _knownPlayers
                    player.sendPacket(new MoveToPawn(actor, _target, _clientMovingToPawnOffset));
                } else {
                    // Send a Server->Client packet CharMoveToLocation to the actor and all Player in its _knownPlayers
                    player.sendPacket(new MoveToLocation(actor));
                }
            }
        }
    }

    public boolean isFollowing() {
        return isCreature(_target) && (intention == AI_INTENTION_FOLLOW);
    }

    /**
     * Create and Launch an AI Follow Task to execute every 1s.
     *
     * @param target The Creature to follow
     */
    public synchronized void startFollow(Creature target) {
        startFollow(target, -1);
    }

    /**
     * Create and Launch an AI Follow Task to execute every 0.5s, following at specified range.
     *
     * @param target The Creature to follow
     * @param range
     */
    public synchronized void startFollow(Creature target, int range) {
        stopFollow();
        setTarget(target);
        if (range == -1)
        {
            CreatureFollowTaskManager.getInstance().addNormalFollow(actor, range);
        }
        else
        {
            CreatureFollowTaskManager.getInstance().addAttackFollow(actor, range);
        }
    }

    /**
     * Stop an AI Follow Task.
     */
    public synchronized void stopFollow() {
        CreatureFollowTaskManager.getInstance().remove(actor);
    }

    public WorldObject getTarget() {
        return _target;
    }

    public void setTarget(WorldObject target) {
        _target = target;
    }

    /**
     * Stop all Ai tasks and futures.
     */
    public void stopAITask() {
        stopFollow();
    }

    @Override
    public String toString() {
        return "Actor: " + actor;
    }
}
