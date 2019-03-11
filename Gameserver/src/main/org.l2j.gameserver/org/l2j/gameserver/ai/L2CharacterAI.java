package org.l2j.gameserver.ai;


import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.effects.L2EffectType;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveFinished;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.AutoAttackStop;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.l2j.gameserver.ai.CtrlIntention.*;


/**
 * This class manages AI of L2Character.<br>
 * L2CharacterAI :
 * <ul>
 * <li>L2AttackableAI</li>
 * <li>L2DoorAI</li>
 * <li>L2PlayerAI</li>
 * <li>L2SummonAI</li>
 * </ul>
 */
public class L2CharacterAI extends AbstractAI {
    private static final Logger LOGGER = Logger.getLogger(L2CharacterAI.class.getName());

    public L2CharacterAI(L2Character creature) {
        super(creature);
    }

    public IntentionCommand getNextIntention() {
        return null;
    }

    @Override
    protected void onEvtAttacked(L2Character attacker) {
        clientStartAutoAttack();
    }

    /**
     * Manage the Idle Intention : Stop Attack, Movement and Stand Up the actor.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Set the AI Intention to AI_INTENTION_IDLE</li>
     * <li>Init cast and attack target</li>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Stand up the actor server side AND client side by sending Server->Client packet ChangeWaitType (broadcast)</li>
     * </ul>
     */
    @Override
    protected void onIntentionIdle() {
        // Set the AI Intention to AI_INTENTION_IDLE
        changeIntention(AI_INTENTION_IDLE);

        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(null);

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

    }

    /**
     * Manage the Active Intention : Stop Attack, Movement and Launch Think Event.<br>
     * <B><U> Actions</U> : <I>if the Intention is not already Active</I></B>
     * <ul>
     * <li>Set the AI Intention to AI_INTENTION_ACTIVE</li>
     * <li>Init cast and attack target</li>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Launch the Think Event</li>
     * </ul>
     */
    @Override
    protected void onIntentionActive() {
        // Check if the Intention is not already Active
        if (getIntention() != AI_INTENTION_ACTIVE) {
            // Set the AI Intention to AI_INTENTION_ACTIVE
            changeIntention(AI_INTENTION_ACTIVE);

            // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
            clientStopMoving(null);

            // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
            clientStopAutoAttack();

            // Also enable random animations for this L2Character if allowed
            // This is only for mobs - town npcs are handled in their constructor
            if (_actor.isAttackable()) {
                ((L2Npc) _actor).startRandomAnimationTask();
            }

            // Launch the Think Event
            onEvtThink();
        }
    }

    /**
     * Manage the Rest Intention.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Set the AI Intention to AI_INTENTION_IDLE</li>
     * </ul>
     */
    @Override
    protected void onIntentionRest() {
        // Set the AI Intention to AI_INTENTION_IDLE
        setIntention(AI_INTENTION_IDLE);
    }

    /**
     * Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Set the Intention of this AI to AI_INTENTION_ATTACK</li>
     * <li>Set or change the AI attack target</li>
     * <li>Start the actor Auto Attack client side by sending Server->Client packet AutoAttackStart (broadcast)</li>
     * <li>Launch the Think Event</li>
     * </ul>
     * <B><U> Overridden in</U> :</B>
     * <ul>
     * <li>L2AttackableAI : Calculate attack timeout</li>
     * </ul>
     */
    @Override
    protected void onIntentionAttack(L2Character target) {
        if ((target == null) || !target.isTargetable()) {
            clientActionFailed();
            return;
        }

        if (getIntention() == AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow() || _actor.isControlBlocked()) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        // Check if the Intention is already AI_INTENTION_ATTACK
        if (getIntention() == AI_INTENTION_ATTACK) {
            // Check if the AI already targets the L2Character
            if (getTarget() != target) {
                // Set the AI attack target (change target)
                setTarget(target);

                // stopFollow();

                // Launch the Think Event
                notifyEvent(CtrlEvent.EVT_THINK, null);

            } else {
                clientActionFailed(); // else client freezes until cancel target
            }
        } else {
            // Set the Intention of this AbstractAI to AI_INTENTION_ATTACK
            changeIntention(AI_INTENTION_ATTACK, target);

            // Set the AI attack target
            setTarget(target);

            // stopFollow();

            // Launch the Think Event
            notifyEvent(CtrlEvent.EVT_THINK, null);
        }
    }

    /**
     * Manage the Cast Intention : Stop current Attack, Init the AI in order to cast and Launch Think Event.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Set the AI cast target</li>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor</li>
     * <li>Set the AI skill used by INTENTION_CAST</li>
     * <li>Set the Intention of this AI to AI_INTENTION_CAST</li>
     * <li>Launch the Think Event</li>
     * </ul>
     */
    @Override
    protected void onIntentionCast(Skill skill, L2Object target, L2ItemInstance item, boolean forceUse, boolean dontMove) {
        if ((getIntention() == AI_INTENTION_REST) && skill.isMagic()) {
            clientActionFailed();
            return;
        }

        if (_actor.isAttackingNow()) {
            ThreadPoolManager.getInstance().schedule(new CastTask(_actor, skill, target, item, forceUse, dontMove), _actor.getAttackEndTime() - TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis()));
        } else {
            changeIntentionToCast(skill, target, item, forceUse, dontMove);
        }
    }

    protected void changeIntentionToCast(Skill skill, L2Object target, L2ItemInstance item, boolean forceUse, boolean dontMove) {
        // Set the AI skill used by INTENTION_CAST
        _skill = skill;

        // Set the AI item that triggered this skill
        _item = item;

        // Set the ctrl/shift pressed parameters
        _forceUse = forceUse;
        _dontMove = dontMove;

        // Change the Intention of this AbstractAI to AI_INTENTION_CAST
        changeIntention(AI_INTENTION_CAST, skill);

        // Launch the Think Event
        notifyEvent(CtrlEvent.EVT_THINK, null);
    }

    /**
     * Manage the Move To Intention : Stop current Attack and Launch a Move to Location Task.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Set the Intention of this AI to AI_INTENTION_MOVE_TO</li>
     * <li>Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)</li>
     * </ul>
     */
    @Override
    protected void onIntentionMoveTo(Location loc) {
        if (getIntention() == AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow()) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        // Set the Intention of this AbstractAI to AI_INTENTION_MOVE_TO
        changeIntention(AI_INTENTION_MOVE_TO, loc);

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

        // Abort the attack of the L2Character and send Server->Client ActionFailed packet
        _actor.abortAttack();

        // Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
        moveTo(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Manage the Follow Intention : Stop current Attack and Launch a Follow Task.<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Stop the actor auto-attack server side AND client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Set the Intention of this AI to AI_INTENTION_FOLLOW</li>
     * <li>Create and Launch an AI Follow Task to execute every 1s</li>
     * </ul>
     */
    @Override
    protected void onIntentionFollow(L2Character target) {
        if (getIntention() == AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow()) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isMovementDisabled() || (_actor.getMoveSpeed() <= 0)) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        // Dead actors can`t follow
        if (_actor.isDead()) {
            clientActionFailed();
            return;
        }

        // do not follow yourself
        if (_actor == target) {
            clientActionFailed();
            return;
        }

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

        // Set the Intention of this AbstractAI to AI_INTENTION_FOLLOW
        changeIntention(AI_INTENTION_FOLLOW, target);

        // Create and Launch an AI Follow Task to execute every 1s
        startFollow(target);
    }

    /**
     * Manage the PickUp Intention : Set the pick up target and Launch a Move To Pawn Task (offset=20).<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Set the AI pick up target</li>
     * <li>Set the Intention of this AI to AI_INTENTION_PICK_UP</li>
     * <li>Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li>
     * </ul>
     */
    @Override
    protected void onIntentionPickUp(L2Object object) {
        if (getIntention() == AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow()) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

        if (object.isItem() && (((L2ItemInstance) object).getItemLocation() != ItemLocation.VOID)) {
            return;
        }

        // Set the Intention of this AbstractAI to AI_INTENTION_PICK_UP
        changeIntention(AI_INTENTION_PICK_UP, object);

        // Set the AI pick up target
        setTarget(object);
        if ((object.getX() == 0) && (object.getY() == 0)) // TODO: Find the drop&spawn bug
        {
            LOGGER.warning("Object in coords 0,0 - using a temporary fix");
            object.setXYZ(getActor().getX(), getActor().getY(), getActor().getZ() + 5);
        }

        // Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
        moveToPawn(object, 20);
    }

    /**
     * Manage the Interact Intention : Set the interact target and Launch a Move To Pawn Task (offset=60).<br>
     * <B><U> Actions</U> : </B>
     * <ul>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Set the AI interact target</li>
     * <li>Set the Intention of this AI to AI_INTENTION_INTERACT</li>
     * <li>Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li>
     * </ul>
     */
    @Override
    protected void onIntentionInteract(L2Object object) {
        if (getIntention() == AI_INTENTION_REST) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        if (_actor.isAllSkillsDisabled() || _actor.isCastingNow()) {
            // Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor
            clientActionFailed();
            return;
        }

        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        clientStopAutoAttack();

        if (getIntention() != AI_INTENTION_INTERACT) {
            // Set the Intention of this AbstractAI to AI_INTENTION_INTERACT
            changeIntention(AI_INTENTION_INTERACT, object);

            // Set the AI interact target
            setTarget(object);

            // Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
            moveToPawn(object, 60);
        }
    }

    /**
     * Do nothing.
     */
    @Override
    protected void onEvtThink() {
        // do nothing
    }

    /**
     * Do nothing.
     */
    @Override
    protected void onEvtAggression(L2Character target, int aggro) {
        // do nothing
    }

    /**
     * Launch actions corresponding to the Event Stunned then onAttacked Event.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Break an attack and send Server->Client ActionFailed packet and a System Message to the L2Character</li>
     * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character</li>
     * <li>Launch actions corresponding to the Event onAttacked (only for L2AttackableAI after the stunning periode)</li>
     * </ul>
     */
    @Override
    protected void onEvtActionBlocked(L2Character attacker) {
        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        _actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(_actor)) {
            AttackStanceTaskManager.getInstance().removeAttackStanceTask(_actor);
        }

        // Stop Server AutoAttack also
        setAutoAttacking(false);

        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(null);
    }

    /**
     * Launch actions corresponding to the Event Rooted.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Launch actions corresponding to the Event onAttacked</li>
     * </ul>
     */
    @Override
    protected void onEvtRooted(L2Character attacker) {
        // Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)
        // _actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
        // if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(_actor))
        // AttackStanceTaskManager.getInstance().removeAttackStanceTask(_actor);

        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(null);

        // Launch actions corresponding to the Event onAttacked
        onEvtAttacked(attacker);
    }

    /**
     * Launch actions corresponding to the Event Confused.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Launch actions corresponding to the Event onAttacked</li>
     * </ul>
     */
    @Override
    protected void onEvtConfused(L2Character attacker) {
        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(null);

        // Launch actions corresponding to the Event onAttacked
        onEvtAttacked(attacker);
    }

    /**
     * Launch actions corresponding to the Event Muted.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character</li>
     * </ul>
     */
    @Override
    protected void onEvtMuted(L2Character attacker) {
        // Break a cast and send Server->Client ActionFailed packet and a System Message to the L2Character
        onEvtAttacked(attacker);
    }

    /**
     * Do nothing.
     */
    @Override
    protected void onEvtEvaded(L2Character attacker) {
        // do nothing
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
        onEvtThink();
    }

    /**
     * Launch actions corresponding to the Event Arrived.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>Launch actions corresponding to the Event Think</li>
     * </ul>
     */
    @Override
    protected void onEvtArrived() {
        getActor().revalidateZone(true);

        if (getActor().moveToNextRoutePoint()) {
            return;
        }

        if (getActor().isAttackable()) {
            ((L2Attackable) getActor()).setisReturningToSpawnPoint(false);
        }
        clientStoppedMoving();

        // If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE
        if (getIntention() == AI_INTENTION_MOVE_TO) {
            setIntention(AI_INTENTION_ACTIVE);
        }

        if (_actor.isNpc()) {
            final L2Npc npc = (L2Npc) _actor;
            WalkingManager.getInstance().onArrived(npc); // Walking Manager support

            // Notify to scripts
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcMoveFinished(npc), npc);
        }

        // Launch actions corresponding to the Event Think
        onEvtThink();
    }

    /**
     * Launch actions corresponding to the Event ArrivedRevalidate.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Launch actions corresponding to the Event Think</li>
     * </ul>
     */
    @Override
    protected void onEvtArrivedRevalidate() {
        // Launch actions corresponding to the Event Think
        onEvtThink();
    }

    /**
     * Launch actions corresponding to the Event ArrivedBlocked.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>Launch actions corresponding to the Event Think</li>
     * </ul>
     */
    @Override
    protected void onEvtArrivedBlocked(Location blocked_at_loc) {
        // If the Intention was AI_INTENTION_MOVE_TO, set the Intention to AI_INTENTION_ACTIVE
        if ((getIntention() == AI_INTENTION_MOVE_TO) || (getIntention() == AI_INTENTION_CAST)) {
            setIntention(AI_INTENTION_ACTIVE);
        }

        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(blocked_at_loc);

        // Launch actions corresponding to the Event Think
        onEvtThink();
    }

    /**
     * Launch actions corresponding to the Event ForgetObject.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>If the object was targeted and the Intention was AI_INTENTION_INTERACT or AI_INTENTION_PICK_UP, set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>If the object was targeted to attack, stop the auto-attack, cancel target and set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>If the object was targeted to cast, cancel target and set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>If the object was targeted to follow, stop the movement, cancel AI Follow Task and set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>If the targeted object was the actor , cancel AI target, stop AI Follow Task, stop the movement and set the Intention to AI_INTENTION_IDLE</li>
     * </ul>
     */
    @Override
    protected void onEvtForgetObject(L2Object object) {
        final L2Object target = getTarget();

        // Stop any casting pointing to this object.
        getActor().abortCast(sc -> sc.getTarget() == object);

        // If the object was targeted and the Intention was AI_INTENTION_INTERACT or AI_INTENTION_PICK_UP, set the Intention to AI_INTENTION_ACTIVE
        if (target == object) {
            setTarget(null);

            if (isFollowing()) {
                // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
                clientStopMoving(null);

                // Stop an AI Follow Task
                stopFollow();
            }
            // Stop any intention that has target we want to forget.
            if (getIntention() != AI_INTENTION_MOVE_TO) {
                setIntention(AI_INTENTION_ACTIVE);
            }
        }

        // Check if the targeted object was the actor
        if (_actor == object) {
            // Cancel AI target
            setTarget(null);

            // Stop an AI Follow Task
            stopFollow();

            // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
            clientStopMoving(null);

            // Set the Intention of this AbstractAI to AI_INTENTION_IDLE
            changeIntention(AI_INTENTION_IDLE);
        }
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
        _actor.abortCast();

        // Stop an AI Follow Task
        stopFollow();

        if (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(_actor)) {
            _actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
        }

        // Launch actions corresponding to the Event Think
        onEvtThink();
    }

    /**
     * Launch actions corresponding to the Event Dead.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop an AI Follow Task</li>
     * <li>Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die (broadcast)</li>
     * </ul>
     */
    @Override
    protected void onEvtDead() {
        // Stop an AI Tasks
        stopAITask();

        // Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die (broadcast)
        clientNotifyDead();

        if (!_actor.isPlayable() && !_actor.isFakePlayer()) {
            _actor.setWalking();
        }
    }

    /**
     * Launch actions corresponding to the Event Fake Death.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Stop an AI Follow Task</li>
     * </ul>
     */
    @Override
    protected void onEvtFakeDeath() {
        // Stop an AI Follow Task
        stopFollow();

        // Stop the actor movement and send Server->Client packet StopMove/StopRotation (broadcast)
        clientStopMoving(null);

        // Init AI
        _intention = AI_INTENTION_IDLE;
        setTarget(null);
    }

    /**
     * Do nothing.
     */
    @Override
    protected void onEvtFinishCasting() {
        // do nothing
    }

    protected boolean maybeMoveToPosition(ILocational worldPosition, int offset) {
        if (worldPosition == null) {
            LOGGER.warning("maybeMoveToPosition: worldPosition == NULL!");
            return false;
        }

        if (offset < 0) {
            return false; // skill radius -1
        }

        if (!_actor.isInsideRadius2D(worldPosition, offset + _actor.getTemplate().getCollisionRadius())) {
            if (_actor.isMovementDisabled() || (_actor.getMoveSpeed() <= 0)) {
                return true;
            }

            if (!_actor.isRunning() && !(this instanceof L2PlayerAI) && !(this instanceof L2SummonAI)) {
                _actor.setRunning();
            }

            stopFollow();

            int x = _actor.getX();
            int y = _actor.getY();

            final double dx = worldPosition.getX() - x;
            final double dy = worldPosition.getY() - y;

            double dist = Math.hypot(dx, dy);

            final double sin = dy / dist;
            final double cos = dx / dist;

            dist -= offset - 5;

            x += (int) (dist * cos);
            y += (int) (dist * sin);

            moveTo(x, y, worldPosition.getZ());
            return true;
        }

        if (isFollowing()) {
            stopFollow();
        }

        return false;
    }

    /**
     * Manage the Move to Pawn action in function of the distance and of the Interact area.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Get the distance between the current position of the L2Character and the target (x,y)</li>
     * <li>If the distance > offset+20, move the actor (by running) to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)</li>
     * <li>If the distance <= offset+20, Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * </ul>
     * <B><U> Example of use </U> :</B>
     * <ul>
     * <li>L2PLayerAI, L2SummonAI</li>
     * </ul>
     *
     * @param target The targeted L2Object
     * @param offset The Interact area radius
     * @return True if a movement must be done
     */
    protected boolean maybeMoveToPawn(L2Object target, int offset) {
        // Get the distance between the current position of the L2Character and the target (x,y)
        if (target == null) {
            LOGGER.warning("maybeMoveToPawn: target == NULL!");
            return false;
        }
        if (offset < 0) {
            return false; // skill radius -1
        }

        offset += _actor.getTemplate().getCollisionRadius();
        if (target.isCharacter()) {
            offset += ((L2Character) target).getTemplate().getCollisionRadius();
        }

        if (!_actor.isInsideRadius2D(target, offset)) {
            // Caller should be L2Playable and thinkAttack/thinkCast/thinkInteract/thinkPickUp
            if (isFollowing()) {
                // allow larger hit range when the target is moving (check is run only once per second)
                if (!_actor.isInsideRadius2D(target, offset + 100)) {
                    return true;
                }
                stopFollow();
                return false;
            }

            if (_actor.isMovementDisabled() || (_actor.getMoveSpeed() <= 0)) {
                // If player is trying attack target but he cannot move to attack target
                // change his intention to idle
                if (_actor.getAI().getIntention() == AI_INTENTION_ATTACK) {
                    _actor.getAI().setIntention(AI_INTENTION_IDLE);
                }

                return true;
            }

            // while flying there is no move to cast
            if ((_actor.getAI().getIntention() == AI_INTENTION_CAST) && _actor.isPlayer() && _actor.checkTransformed(transform -> !transform.isCombat())) {
                _actor.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
                _actor.sendPacket(ActionFailed.STATIC_PACKET);
                return true;
            }

            // If not running, set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
            if (!_actor.isRunning() && !(this instanceof L2PlayerAI) && !(this instanceof L2SummonAI)) {
                _actor.setRunning();
            }

            stopFollow();
            if (target.isCharacter() && !target.isDoor()) {
                if (((L2Character) target).isMoving()) {
                    offset -= 100;
                }
                if (offset < 5) {
                    offset = 5;
                }

                startFollow((L2Character) target, offset);
            } else {
                // Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn (broadcast)
                moveToPawn(target, offset);
            }
            return true;
        }

        if (isFollowing()) {
            stopFollow();
        }

        // Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)
        // clientStopMoving(null);
        return false;
    }

    /**
     * Modify current Intention and actions if the target is lost or dead.<br>
     * <B><U> Actions</U> : <I>If the target is lost or dead</I></B>
     * <ul>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE</li>
     * </ul>
     * <B><U> Example of use </U> :</B>
     * <ul>
     * <li>L2PLayerAI, L2SummonAI</li>
     * </ul>
     *
     * @param target The targeted L2Object
     * @return True if the target is lost or dead (false if fakedeath)
     */
    protected boolean checkTargetLostOrDead(L2Character target) {
        if ((target == null) || target.isAlikeDead()) {
            // check if player is fakedeath
            if ((target != null) && target.isPlayer() && ((L2PcInstance) target).isFakeDeath()) {
                target.stopFakeDeath(true);
                return false;
            }

            // Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
            setIntention(AI_INTENTION_ACTIVE);
            return true;
        }
        return false;
    }

    /**
     * Modify current Intention and actions if the target is lost.<br>
     * <B><U> Actions</U> : <I>If the target is lost</I></B>
     * <ul>
     * <li>Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop (broadcast)</li>
     * <li>Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation (broadcast)</li>
     * <li>Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE</li>
     * </ul>
     * <B><U> Example of use </U> :</B>
     * <ul>
     * <li>L2PLayerAI, L2SummonAI</li>
     * </ul>
     *
     * @param target The targeted L2Object
     * @return True if the target is lost
     */
    protected boolean checkTargetLost(L2Object target) {
        // check if player is fakedeath
        if ((target != null) && target.isPlayer()) {
            final L2PcInstance target2 = (L2PcInstance) target; // convert object to chara

            if (target2.isFakeDeath()) {
                target2.stopFakeDeath(true);
                return false;
            }
        }
        if (target == null) {
            // Set the Intention of this AbstractAI to AI_INTENTION_ACTIVE
            setIntention(AI_INTENTION_ACTIVE);
            return true;
        }
        if ((_actor != null) && (_skill != null) && _skill.isBad() && (_skill.getAffectRange() > 0) && !GeoEngine.getInstance().canSeeTarget(_actor, target)) {
            setIntention(AI_INTENTION_ACTIVE);
            return true;
        }
        return false;
    }

    public static class IntentionCommand {
        protected final CtrlIntention _crtlIntention;
        protected final Object _arg0;
        protected final Object _arg1;

        protected IntentionCommand(CtrlIntention pIntention, Object pArg0, Object pArg1) {
            _crtlIntention = pIntention;
            _arg0 = pArg0;
            _arg1 = pArg1;
        }

        public CtrlIntention getCtrlIntention() {
            return _crtlIntention;
        }
    }

    /**
     * Cast Task
     *
     * @author Zoey76
     */
    public static class CastTask implements Runnable {
        private final L2Character _activeChar;
        private final L2Object _target;
        private final Skill _skill;
        private final L2ItemInstance _item;
        private final boolean _forceUse;
        private final boolean _dontMove;

        public CastTask(L2Character actor, Skill skill, L2Object target, L2ItemInstance item, boolean forceUse, boolean dontMove) {
            _activeChar = actor;
            _target = target;
            _skill = skill;
            _item = item;
            _forceUse = forceUse;
            _dontMove = dontMove;
        }

        @Override
        public void run() {
            if (_activeChar.isAttackingNow()) {
                _activeChar.abortAttack();
            }
            _activeChar.getAI().changeIntentionToCast(_skill, _target, _item, _forceUse, _dontMove);
        }
    }

    protected class SelfAnalysis {
        public boolean isMage = false;
        public boolean isBalanced;
        public boolean isArcher = false;
        public boolean isHealer = false;
        public boolean isFighter = false;
        public boolean cannotMoveOnLand = false;
        public Set<Skill> generalSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> buffSkills = ConcurrentHashMap.newKeySet();
        public int lastBuffTick = 0;
        public Set<Skill> debuffSkills = ConcurrentHashMap.newKeySet();
        public int lastDebuffTick = 0;
        public Set<Skill> cancelSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> healSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> generalDisablers = ConcurrentHashMap.newKeySet();
        public Set<Skill> sleepSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> rootSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> muteSkills = ConcurrentHashMap.newKeySet();
        public Set<Skill> resurrectSkills = ConcurrentHashMap.newKeySet();
        public boolean hasHealOrResurrect = false;
        public boolean hasLongRangeSkills = false;
        public boolean hasLongRangeDamageSkills = false;
        public int maxCastRange = 0;

        public SelfAnalysis() {
        }

        public void init() {
            switch (((L2NpcTemplate) _actor.getTemplate()).getAIType()) {
                case FIGHTER: {
                    isFighter = true;
                    break;
                }
                case MAGE: {
                    isMage = true;
                    break;
                }
                case CORPSE:
                case BALANCED: {
                    isBalanced = true;
                    break;
                }
                case ARCHER: {
                    isArcher = true;
                    break;
                }
                case HEALER: {
                    isHealer = true;
                    break;
                }
                default: {
                    isFighter = true;
                    break;
                }
            }
            // water movement analysis
            if (_actor.isNpc()) {
                switch (_actor.getId()) {
                    case 20314: // great white shark
                    case 20849: // Light Worm
                    {
                        cannotMoveOnLand = true;
                        break;
                    }
                    default: {
                        cannotMoveOnLand = false;
                        break;
                    }
                }
            }
            // skill analysis
            for (Skill sk : _actor.getAllSkills()) {
                if (sk.isPassive()) {
                    continue;
                }
                final int castRange = sk.getCastRange();
                boolean hasLongRangeDamageSkill = false;

                if (sk.isContinuous()) {
                    if (!sk.isDebuff()) {
                        buffSkills.add(sk);
                    } else {
                        debuffSkills.add(sk);
                    }
                    continue;
                }

                if (sk.hasEffectType(L2EffectType.DISPEL, L2EffectType.DISPEL_BY_SLOT)) {
                    cancelSkills.add(sk);
                } else if (sk.hasEffectType(L2EffectType.HEAL)) {
                    healSkills.add(sk);
                    hasHealOrResurrect = true;
                } else if (sk.hasEffectType(L2EffectType.SLEEP)) {
                    sleepSkills.add(sk);
                } else if (sk.hasEffectType(L2EffectType.BLOCK_ACTIONS)) {
                    // hardcoding petrification until improvements are made to
                    // EffectTemplate... petrification is totally different for
                    // AI than paralyze
                    switch (sk.getId()) {
                        case 367:
                        case 4111:
                        case 4383:
                        case 4616:
                        case 4578: {
                            sleepSkills.add(sk);
                            break;
                        }
                        default: {
                            generalDisablers.add(sk);
                            break;
                        }
                    }
                } else if (sk.hasEffectType(L2EffectType.ROOT)) {
                    rootSkills.add(sk);
                } else if (sk.hasEffectType(L2EffectType.BLOCK_CONTROL)) {
                    debuffSkills.add(sk);
                } else if (sk.hasEffectType(L2EffectType.MUTE)) {
                    muteSkills.add(sk);
                } else if (sk.hasEffectType(L2EffectType.RESURRECTION)) {
                    resurrectSkills.add(sk);
                    hasHealOrResurrect = true;
                } else {
                    generalSkills.add(sk);
                    hasLongRangeDamageSkill = true;
                }

                if (castRange > 70) {
                    hasLongRangeSkills = true;
                    if (hasLongRangeDamageSkill) {
                        hasLongRangeDamageSkills = true;
                    }
                }
                if (castRange > maxCastRange) {
                    maxCastRange = castRange;
                }
            }
            // Because of missing skills, some mages/balanced cannot play like mages
            if (!hasLongRangeDamageSkills && isMage) {
                isBalanced = true;
                isMage = false;
                isFighter = false;
            }
            if (!hasLongRangeSkills && (isMage || isBalanced)) {
                isBalanced = false;
                isMage = false;
                isFighter = true;
            }
            if (generalSkills.isEmpty() && isMage) {
                isBalanced = true;
                isMage = false;
            }
        }
    }
}
