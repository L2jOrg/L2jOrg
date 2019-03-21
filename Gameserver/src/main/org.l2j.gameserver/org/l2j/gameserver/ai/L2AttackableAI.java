package org.l2j.gameserver.ai;


import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.enums.AISkillScope;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.model.AggroInfo;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.*;
import org.l2j.gameserver.model.effects.L2EffectType;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableFactionCall;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableHate;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * This class manages AI of L2Attackable.
 */
public class L2AttackableAI extends L2CharacterAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(L2AttackableAI.class);

    private static final int RANDOM_WALK_RATE = 30; // confirmed
    // private static final int MAX_DRIFT_RANGE = 300;
    private static final int MAX_ATTACK_TIMEOUT = 1200; // int ticks, i.e. 2min
    int lastBuffTick;
    /**
     * The L2Attackable AI task executed every 1s (call onEvtThink method).
     */
    private Future<?> _aiTask;
    /**
     * The delay after which the attacked is stopped.
     */
    private int _attackTimeout;
    /**
     * The L2Attackable aggro counter.
     */
    private int _globalAggro;
    /**
     * The flag used to indicate that a thinking action is in progress, to prevent recursive thinking.
     */
    private boolean _thinking;
    private int chaostime = 0;

    public L2AttackableAI(L2Attackable attackable) {
        super(attackable);
        _attackTimeout = Integer.MAX_VALUE;
        _globalAggro = -10; // 10 seconds timeout of ATTACK after respawn
    }

    /**
     * @param target The targeted WorldObject
     * @return {@code true} if target can be auto attacked due aggression.
     */
    private boolean isAggressiveTowards(L2Character target) {
        if ((target == null) || (getActiveChar() == null)) {
            return false;
        }

        // Check if the target isn't invulnerable
        if (target.isInvul()) {
            return false;
        }

        // Check if the target isn't a Folk or a Door
        if (target.isDoor()) {
            return false;
        }

        final L2Attackable me = getActiveChar();

        // Check if the target isn't dead, is in the Aggro range and is at the same height
        if (target.isAlikeDead()) {
            return false;
        }

        // Check if the target is a L2Playable
        if (target.isPlayable()) {
            // Check if the AI isn't a Raid Boss, can See Silent Moving players and the target isn't in silent move mode
            if (!(me.isRaid()) && !(me.canSeeThroughSilentMove()) && ((L2Playable) target).isSilentMovingAffected()) {
                return false;
            }
        }

        // Gets the player if there is any.
        final L2PcInstance player = target.getActingPlayer();
        if (player != null) {
            // Don't take the aggro if the GM has the access level below or equal to GM_DONT_TAKE_AGGRO
            if (!player.getAccessLevel().canTakeAggro()) {
                return false;
            }

            // check if the target is within the grace period for JUST getting up from fake death
            if (player.isRecentFakeDeath()) {
                return false;
            }

            if (me instanceof L2GuardInstance) {
                L2World.getInstance().forEachVisibleObjectInRange(me, L2GuardInstance.class, 500, guard ->
                {
                    if (guard.isAttackingNow() && (guard.getTarget() == player)) {
                        me.getAI().startFollow(player);
                        me.addDamageHate(player, 0, 10);
                    }
                });
                if (player.getReputation() < 0) {
                    return true;
                }
            }
        } else if (me.isMonster()) {
            // depending on config, do not allow mobs to attack _new_ players in peacezones,
            // unless they are already following those players from outside the peacezone.
            if (!Config.ALT_MOB_AGRO_IN_PEACEZONE && target.isInsideZone(ZoneId.PEACE)) {
                return false;
            }

            if (!me.isAggressive()) {
                return false;
            }
        }

        if (me.isChampion() && Config.CHAMPION_PASSIVE) {
            return false;
        }

        return target.isAutoAttackable(me) && GeoEngine.getInstance().canSeeTarget(me, target);
    }

    public void startAITask() {
        // If not idle - create an AI task (schedule onEvtThink repeatedly)
        if (_aiTask == null) {
            _aiTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(this::onEvtThink, 1000, 1000);
        }
    }

    @Override
    public void stopAITask() {
        if (_aiTask != null) {
            _aiTask.cancel(false);
            _aiTask = null;
        }
        super.stopAITask();
    }

    /**
     * Set the Intention of this L2CharacterAI and create an AI Task executed every 1s (call onEvtThink method) for this L2Attackable.<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : If actor _knowPlayer isn't EMPTY, AI_INTENTION_IDLE will be change in AI_INTENTION_ACTIVE</B></FONT>
     *
     * @param intention The new Intention to set to the AI
     * @param args      The first parameter of the Intention
     */
    @Override
    synchronized void changeIntention(CtrlIntention intention, Object... args) {
        if ((intention == CtrlIntention.AI_INTENTION_IDLE) || (intention == CtrlIntention.AI_INTENTION_ACTIVE)) {
            // Check if actor is not dead
            final L2Attackable npc = getActiveChar();
            if (!npc.isAlikeDead()) {
                // If its _knownPlayer isn't empty set the Intention to AI_INTENTION_ACTIVE
                if (!L2World.getInstance().getVisibleObjects(npc, L2PcInstance.class).isEmpty()) {
                    intention = CtrlIntention.AI_INTENTION_ACTIVE;
                } else if (npc.getSpawn() != null) {
                    final Location loc = npc.getSpawn();
                    final int range = Config.MAX_DRIFT_RANGE;

                    if (!npc.isInsideRadius3D(loc, range + range)) {
                        intention = CtrlIntention.AI_INTENTION_ACTIVE;
                    }
                }
            }

            if (intention == CtrlIntention.AI_INTENTION_IDLE) {
                // Set the Intention of this L2AttackableAI to AI_INTENTION_IDLE
                super.changeIntention(CtrlIntention.AI_INTENTION_IDLE);

                stopAITask();

                // Cancel the AI
                _actor.detachAI();

                return;
            }
        }

        // Set the Intention of this L2AttackableAI to intention
        super.changeIntention(intention, args);

        // If not idle - create an AI task (schedule onEvtThink repeatedly)
        startAITask();
    }

    @Override
    protected void changeIntentionToCast(Skill skill, L2Object target, L2ItemInstance item, boolean forceUse, boolean dontMove) {
        // Set the AI cast target
        setTarget(target);
        super.changeIntentionToCast(skill, target, item, forceUse, dontMove);
    }

    /**
     * Manage the Attack Intention : Stop current Attack (if necessary), Calculate attack timeout, Start a new Attack and Launch Think Event.
     *
     * @param target The L2Character to attack
     */
    @Override
    protected void onIntentionAttack(L2Character target) {
        // Calculate the attack timeout
        _attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getInstance().getGameTicks();

        // self and buffs
        if ((lastBuffTick + 30) < GameTimeController.getInstance().getGameTicks()) {
            for (Skill buff : getActiveChar().getTemplate().getAISkills(AISkillScope.BUFF)) {
                target = skillTargetReconsider(buff, true);
                if (target != null) {
                    setTarget(target);
                    _actor.doCast(buff);
                    LOGGER.debug(this + " used buff skill " + buff + " on " + _actor);
                    break;
                }
            }
            lastBuffTick = GameTimeController.getInstance().getGameTicks();
        }

        // Manage the Attack Intention : Stop current Attack (if necessary), Start a new Attack and Launch Think Event
        super.onIntentionAttack(target);
    }

    protected void thinkCast() {
        final L2Object target = _skill.getTarget(_actor, getTarget(), _forceUse, _dontMove, false);
        if (checkTargetLost(target)) {
            return;
        }
        if (maybeMoveToPawn(target, _actor.getMagicalAttackRange(_skill))) {
            return;
        }
        setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        _actor.doCast(_skill, _item, _forceUse, _dontMove);
    }

    /**
     * Manage AI standard thinks of a L2Attackable (called by onEvtThink). <B><U> Actions</U> :</B>
     * <ul>
     * <li>Update every 1s the _globalAggro counter to come close to 0</li>
     * <li>If the actor is Aggressive and can attack, add all autoAttackable L2Character in its Aggro Range to its _aggroList, chose a target and order to attack it</li>
     * <li>If the actor is a L2GuardInstance that can't attack, order to it to return to its home location</li>
     * <li>If the actor is a L2MonsterInstance that can't attack, order to it to random walk (1/100)</li>
     * </ul>
     */
    protected void thinkActive() {
        final L2Attackable npc = getActiveChar();
        L2Object target = getTarget();
        // Update every 1s the _globalAggro counter to come close to 0
        if (_globalAggro != 0) {
            if (_globalAggro < 0) {
                _globalAggro++;
            } else {
                _globalAggro--;
            }
        }

        // Add all autoAttackable L2Character in L2Attackable Aggro Range to its _aggroList with 0 damage and 1 hate
        // A L2Attackable isn't aggressive during 10s after its spawn because _globalAggro is set to -10
        if (_globalAggro >= 0) {
            if (npc.isAggressive() || (npc instanceof L2GuardInstance)) {
                final int range = npc instanceof L2GuardInstance ? 500 : npc.getAggroRange(); // TODO Make sure how guards behave towards players.
                L2World.getInstance().forEachVisibleObjectInRange(npc, L2Character.class, range, t ->
                {
                    // For each L2Character check if the target is autoattackable
                    if (isAggressiveTowards(t)) // check aggression
                    {
                        if (t.isPlayable()) {
                            final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnAttackableHate(getActiveChar(), t.getActingPlayer(), t.isSummon()), getActiveChar(), TerminateReturn.class);
                            if ((term != null) && term.terminate()) {
                                return;
                            }

                            // Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
                            final int hating = npc.getHating(t);

                            // Add the attacker to the L2Attackable _aggroList with 0 damage and 1 hate
                            if (hating == 0) {
                                npc.addDamageHate(t, 0, 1);
                            }
                            if (npc instanceof L2GuardInstance) {
                                L2World.getInstance().forEachVisibleObjectInRange(npc, L2GuardInstance.class, 500, guard ->
                                {
                                    guard.addDamageHate(t, 0, 10);
                                });
                            }
                        }
                    }
                });
            }

            // Chose a target from its aggroList
            L2Character hated;
            if (npc.isConfused() && (target != null) && target.isCharacter()) {
                hated = (L2Character) target; // effect handles selection
            } else {
                hated = npc.getMostHated();
            }

            // Order to the L2Attackable to attack the target
            if ((hated != null) && !npc.isCoreAIDisabled()) {
                // Get the hate level of the L2Attackable against this L2Character target contained in _aggroList
                final int aggro = npc.getHating(hated);

                if ((aggro + _globalAggro) > 0) {
                    // Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
                    if (!npc.isRunning()) {
                        npc.setRunning();
                    }

                    // Set the AI Intention to AI_INTENTION_ATTACK
                    setIntention(CtrlIntention.AI_INTENTION_ATTACK, hated);
                }

                return;
            }
        }

        // Chance to forget attackers after some time
        if ((npc.getCurrentHp() == npc.getMaxHp()) && (npc.getCurrentMp() == npc.getMaxMp()) && !npc.getAttackByList().isEmpty() && (Rnd.get(500) == 0)) {
            npc.clearAggroList();
            npc.getAttackByList().clear();
        }

        // Check if the mob should not return to spawn point
        if (!npc.canReturnToSpawnPoint()) {
            return;
        }

        // Check if the actor is a guard
        if (((npc instanceof L2GuardInstance) || (npc instanceof L2DefenderInstance)) && !npc.isWalker() && !npc.isRandomWalkingEnabled()) {
            // Order to the L2GuardInstance to return to its home location because there's no target to attack
            npc.returnHome();
        }

        // Minions following leader
        final L2Character leader = npc.getLeader();
        if ((leader != null) && !leader.isAlikeDead()) {
            final int offset;
            final int minRadius = 30;

            if (npc.isRaidMinion()) {
                offset = 500; // for Raids - need correction
            } else {
                offset = 200; // for normal minions - need correction :)
            }

            if (leader.isRunning()) {
                npc.setRunning();
            } else {
                npc.setWalking();
            }

            if (npc.calculateDistanceSq2D(leader) > (offset * offset)) {
                int x1 = Rnd.get(minRadius * 2, offset * 2); // x
                int y1 = Rnd.get(x1, offset * 2); // distance
                y1 = (int) Math.sqrt((y1 * y1) - (x1 * x1)); // y
                if (x1 > (offset + minRadius)) {
                    x1 = (leader.getX() + x1) - offset;
                } else {
                    x1 = (leader.getX() - x1) + minRadius;
                }
                if (y1 > (offset + minRadius)) {
                    y1 = (leader.getY() + y1) - offset;
                } else {
                    y1 = (leader.getY() - y1) + minRadius;
                }

                // Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
                moveTo(x1, y1, leader.getZ());
                return;
            } else if (Rnd.get(RANDOM_WALK_RATE) == 0) {
                for (Skill sk : npc.getTemplate().getAISkills(AISkillScope.BUFF)) {
                    target = skillTargetReconsider(sk, true);
                    if (target != null) {
                        setTarget(target);
                        npc.doCast(sk);
                        return;
                    }
                }
            }
        }
        // Order to the L2MonsterInstance to random walk (1/100)
        else if ((npc.getSpawn() != null) && (Rnd.get(RANDOM_WALK_RATE) == 0) && npc.isRandomWalkingEnabled()) {
            int x1 = 0;
            int y1 = 0;
            int z1 = 0;
            final int range = Config.MAX_DRIFT_RANGE;

            for (Skill sk : npc.getTemplate().getAISkills(AISkillScope.BUFF)) {
                target = skillTargetReconsider(sk, true);
                if (target != null) {
                    setTarget(target);
                    npc.doCast(sk);
                    return;
                }
            }

            x1 = npc.getSpawn().getX();
            y1 = npc.getSpawn().getY();
            z1 = npc.getSpawn().getZ();

            if (!npc.isInsideRadius2D(x1, y1, 0, range)) {
                npc.setisReturningToSpawnPoint(true);
            } else {
                final int deltaX = Rnd.get(range * 2); // x
                int deltaY = Rnd.get(deltaX, range * 2); // distance
                deltaY = (int) Math.sqrt((deltaY * deltaY) - (deltaX * deltaX)); // y
                x1 = (deltaX + x1) - range;
                y1 = (deltaY + y1) - range;
                z1 = npc.getZ();
            }

            // Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation (broadcast)
            final Location moveLoc = GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), x1, y1, z1, npc.getInstanceWorld());

            moveTo(moveLoc.getX(), moveLoc.getY(), moveLoc.getZ());
        }
    }

    /**
     * Manage AI attack thinks of a L2Attackable (called by onEvtThink). <B><U> Actions</U> :</B>
     * <ul>
     * <li>Update the attack timeout if actor is running</li>
     * <li>If target is dead or timeout is expired, stop this attack and set the Intention to AI_INTENTION_ACTIVE</li>
     * <li>Call all L2Object of its Faction inside the Faction Range</li>
     * <li>Chose a target and order to attack it with magic skill or physical attack</li>
     * </ul>
     * TODO: Manage casting rules to healer mobs (like Ant Nurses)
     */
    protected void thinkAttack() {
        final L2Attackable npc = getActiveChar();

        if ((npc == null) || npc.isCastingNow()) {
            return;
        }

        L2Character target = npc.getMostHated();
        if (getTarget() != target) {
            setTarget(target);
        }

        // Check if target is dead or if timeout is expired to stop this attack
        if ((target == null) || target.isAlikeDead()) {
            // Stop hating this target after the attack timeout or if target is dead
            npc.stopHating(target);
            return;
        }

        if (_attackTimeout < GameTimeController.getInstance().getGameTicks()) {
            // Set the AI Intention to AI_INTENTION_ACTIVE
            setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

            npc.setWalking();

            // Monster teleport to spawn
            if (npc.isMonster() && (npc.getSpawn() != null) && !npc.isInInstance()) {
                npc.teleToLocation(npc.getSpawn(), false);
            }
            return;
        }

        final int collision = npc.getTemplate().getCollisionRadius();

        // Handle all L2Object of its Faction inside the Faction Range

        final Set<Integer> clans = getActiveChar().getTemplate().getClans();
        if ((clans != null) && !clans.isEmpty()) {
            final int factionRange = npc.getTemplate().getClanHelpRange() + collision;
            // Go through all L2Object that belong to its faction
            try {
                final L2Character finalTarget = target;
                L2World.getInstance().forEachVisibleObjectInRange(npc, L2Npc.class, factionRange, called ->
                {
                    if (!getActiveChar().getTemplate().isClan(called.getTemplate().getClans())) {
                        return;
                    }

                    // Check if the L2Object is inside the Faction Range of the actor
                    if (called.hasAI()) {
                        if ((Math.abs(finalTarget.getZ() - called.getZ()) < 600) && npc.getAttackByList().stream().anyMatch(o -> o.get() == finalTarget) && ((called.getAI()._intention == CtrlIntention.AI_INTENTION_IDLE) || (called.getAI()._intention == CtrlIntention.AI_INTENTION_ACTIVE))) {
                            if (finalTarget.isPlayable()) {
                                // By default, when a faction member calls for help, attack the caller's attacker.
                                // Notify the AI with EVT_AGGRESSION
                                called.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, finalTarget, 1);
                                EventDispatcher.getInstance().notifyEventAsync(new OnAttackableFactionCall(called, getActiveChar(), finalTarget.getActingPlayer(), finalTarget.isSummon()), called);
                            } else if (called.isAttackable() && (called.getAI()._intention != CtrlIntention.AI_INTENTION_ATTACK)) {
                                ((L2Attackable) called).addDamageHate(finalTarget, 0, npc.getHating(finalTarget));
                                called.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, finalTarget);
                            }
                        }
                    }
                });
            } catch (NullPointerException e) {
                LOGGER.warn(": thinkAttack() faction call failed: " + e.getMessage());
            }
        }

        if (npc.isCoreAIDisabled()) {
            return;
        }

        final int combinedCollision = collision + target.getTemplate().getCollisionRadius();

        final List<Skill> aiSuicideSkills = npc.getTemplate().getAISkills(AISkillScope.SUICIDE);
        if (!aiSuicideSkills.isEmpty() && ((int) ((npc.getCurrentHp() / npc.getMaxHp()) * 100) < 30) && npc.hasSkillChance()) {
            final Skill skill = aiSuicideSkills.get(Rnd.get(aiSuicideSkills.size()));
            if (SkillCaster.checkUseConditions(npc, skill) && checkSkillTarget(skill, target)) {
                npc.doCast(skill);
                LOGGER.debug(this + " used suicide skill " + skill);
                return;
            }
        }

        // ------------------------------------------------------
        // In case many mobs are trying to hit from same place, move a bit, circling around the target
        // Note from Gnacik:
        // On l2js because of that sometimes mobs don't attack player only running around player without any sense, so decrease chance for now
        if (!npc.isMovementDisabled() && (Rnd.get(100) <= 3)) {
            for (L2Attackable nearby : L2World.getInstance().getVisibleObjects(npc, L2Attackable.class)) {
                if (npc.isInsideRadius2D(nearby, collision) && (nearby != target)) {
                    int newX = combinedCollision + Rnd.get(40);
                    if (Rnd.nextBoolean()) {
                        newX += target.getX();
                    } else {
                        newX = target.getX() - newX;
                    }
                    int newY = combinedCollision + Rnd.get(40);
                    if (Rnd.nextBoolean()) {
                        newY += target.getY();
                    } else {
                        newY = target.getY() - newY;
                    }

                    if (!npc.isInsideRadius2D(newX, newY, 0, collision)) {
                        final int newZ = npc.getZ() + 30;

                        // Mobius: Verify destination. Prevents wall collision issues and fixes monsters not avoiding obstacles.
                        moveTo(GeoEngine.getInstance().canMoveToTargetLoc(npc.getX(), npc.getY(), npc.getZ(), newX, newY, newZ, npc.getInstanceWorld()));
                    }
                    return;
                }
            }
        }
        // Dodge if its needed
        if (!npc.isMovementDisabled() && (npc.getTemplate().getDodge() > 0)) {
            if (Rnd.get(100) <= npc.getTemplate().getDodge()) {
                // Micht: kepping this one otherwise we should do 2 sqrt
                final double distance2 = npc.calculateDistanceSq2D(target);
                if (Math.sqrt(distance2) <= (60 + combinedCollision)) {
                    int posX = npc.getX();
                    int posY = npc.getY();
                    final int posZ = npc.getZ() + 30;

                    if (target.getX() < posX) {
                        posX += 300;
                    } else {
                        posX -= 300;
                    }

                    if (target.getY() < posY) {
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

        // ------------------------------------------------------------------------------
        // BOSS/Raid Minion Target Reconsider
        if (npc.isRaid() || npc.isRaidMinion()) {
            chaostime++;
            boolean changeTarget = false;
            if ((npc instanceof L2RaidBossInstance) && (chaostime > Config.RAID_CHAOS_TIME)) {
                final double multiplier = ((L2MonsterInstance) npc).hasMinions() ? 200 : 100;
                changeTarget = Rnd.get(100) <= (100 - ((npc.getCurrentHp() * multiplier) / npc.getMaxHp()));
            } else if ((npc instanceof L2GrandBossInstance) && (chaostime > Config.GRAND_CHAOS_TIME)) {
                final double chaosRate = 100 - ((npc.getCurrentHp() * 300) / npc.getMaxHp());
                changeTarget = ((chaosRate <= 10) && (Rnd.get(100) <= 10)) || ((chaosRate > 10) && (Rnd.get(100) <= chaosRate));
            } else if (chaostime > Config.MINION_CHAOS_TIME) {
                changeTarget = Rnd.get(100) <= (100 - ((npc.getCurrentHp() * 200) / npc.getMaxHp()));
            }

            if (changeTarget) {
                target = targetReconsider(true);
                if (target != null) {
                    setTarget(target);
                    chaostime = 0;
                    return;
                }
            }
        }

        if (target == null) {
            target = targetReconsider(false);
            if (target == null) {
                return;
            }

            setTarget(target);
        }

        if (npc.hasSkillChance()) {
            // First use the most important skill - heal. Even reconsider target.
            if (!npc.getTemplate().getAISkills(AISkillScope.HEAL).isEmpty()) {
                final Skill healSkill = npc.getTemplate().getAISkills(AISkillScope.HEAL).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.HEAL).size()));
                if (SkillCaster.checkUseConditions(npc, healSkill)) {
                    final L2Character healTarget = skillTargetReconsider(healSkill, false);
                    if (healTarget != null) {
                        final double healChance = (100 - healTarget.getCurrentHpPercent()) * 1.5; // Ensure heal chance is always 100% if HP is below 33%.
                        if ((Rnd.get(100) < healChance) && checkSkillTarget(healSkill, healTarget)) {
                            setTarget(healTarget);
                            npc.doCast(healSkill);
                            LOGGER.debug(this + " used heal skill " + healSkill + " with target " + getTarget());
                            return;
                        }
                    }
                }
            }

            // Then use the second most important skill - buff. Even reconsider target.
            if (!npc.getTemplate().getAISkills(AISkillScope.BUFF).isEmpty()) {
                final Skill buffSkill = npc.getTemplate().getAISkills(AISkillScope.BUFF).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.BUFF).size()));
                if (SkillCaster.checkUseConditions(npc, buffSkill)) {
                    final L2Character buffTarget = skillTargetReconsider(buffSkill, true);
                    if (checkSkillTarget(buffSkill, buffTarget)) {
                        setTarget(buffTarget);
                        npc.doCast(buffSkill);
                        LOGGER.debug(this + " used buff skill " + buffSkill + " with target " + getTarget());
                        return;
                    }
                }
            }

            // Then try to immobolize target if moving.
            if (target.isMoving() && !npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).isEmpty()) {
                final Skill immobolizeSkill = npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.IMMOBILIZE).size()));
                if (SkillCaster.checkUseConditions(npc, immobolizeSkill) && checkSkillTarget(immobolizeSkill, target)) {
                    npc.doCast(immobolizeSkill);
                    LOGGER.debug(this + " used immobolize skill " + immobolizeSkill + " with target " + getTarget());
                    return;
                }
            }

            // Then try to mute target if he is casting.
            if (target.isCastingNow() && !npc.getTemplate().getAISkills(AISkillScope.COT).isEmpty()) {
                final Skill muteSkill = npc.getTemplate().getAISkills(AISkillScope.COT).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.COT).size()));
                if (SkillCaster.checkUseConditions(npc, muteSkill) && checkSkillTarget(muteSkill, target)) {
                    npc.doCast(muteSkill);
                    LOGGER.debug(this + " used mute skill " + muteSkill + " with target " + getTarget());
                    return;
                }
            }

            // Try cast short range skill.
            if (!npc.getShortRangeSkills().isEmpty()) {
                final Skill shortRangeSkill = npc.getShortRangeSkills().get(Rnd.get(npc.getShortRangeSkills().size()));
                if (SkillCaster.checkUseConditions(npc, shortRangeSkill) && checkSkillTarget(shortRangeSkill, target)) {
                    npc.doCast(shortRangeSkill);
                    LOGGER.debug(this + " used short range skill " + shortRangeSkill + " with target " + getTarget());
                    return;
                }
            }

            // Try cast long range skill.
            if (!npc.getLongRangeSkills().isEmpty()) {
                final Skill longRangeSkill = npc.getLongRangeSkills().get(Rnd.get(npc.getLongRangeSkills().size()));
                if (SkillCaster.checkUseConditions(npc, longRangeSkill) && checkSkillTarget(longRangeSkill, target)) {
                    npc.doCast(longRangeSkill);
                    LOGGER.debug(this + " used long range skill " + longRangeSkill + " with target " + getTarget());
                    return;
                }
            }

            // Finally, if none succeed, try to cast any skill.
            if (!npc.getTemplate().getAISkills(AISkillScope.GENERAL).isEmpty()) {
                final Skill generalSkill = npc.getTemplate().getAISkills(AISkillScope.GENERAL).get(Rnd.get(npc.getTemplate().getAISkills(AISkillScope.GENERAL).size()));
                if (SkillCaster.checkUseConditions(npc, generalSkill) && checkSkillTarget(generalSkill, target)) {
                    npc.doCast(generalSkill);
                    LOGGER.debug(this + " used general skill " + generalSkill + " with target " + getTarget());
                    return;
                }
            }
        }

        // Check if target is within range or move.
        final int range = npc.getPhysicalAttackRange() + combinedCollision;
        if (npc.calculateDistance2D(target) > range) {
            if (checkTarget(target)) {
                moveToPawn(target, range);
                return;
            }

            target = targetReconsider(false);
            if (target == null) {
                return;
            }

            setTarget(target);
        }

        // Attacks target
        _actor.doAutoAttack(target);
    }

    private boolean checkSkillTarget(Skill skill, L2Object target) {
        if (target == null) {
            return false;
        }

        // Check if target is valid and within cast range.
        if (skill.getTarget(getActiveChar(), target, false, getActiveChar().isMovementDisabled(), false) == null) {
            return false;
        }

        if (!Util.checkIfInRange(skill.getCastRange(), getActiveChar(), target, true)) {
            return false;
        }

        if (target.isCharacter()) {
            // Skip if target is already affected by such skill.
            if (skill.isContinuous()) {
                if (((L2Character) target).getEffectList().hasAbnormalType(skill.getAbnormalType(), i -> (i.getSkill().getAbnormalLvl() >= skill.getAbnormalLvl()))) {
                    return false;
                }

                // There are cases where bad skills (negative effect points) are actually buffs and NPCs cast them on players, but they shouldn't.
                if ((!skill.isDebuff() || !skill.isBad()) && target.isAutoAttackable(getActiveChar())) {
                    return false;
                }
            }

            // Check if target had buffs if skill is bad cancel, or debuffs if skill is good cancel.
            if (skill.hasEffectType(L2EffectType.DISPEL, L2EffectType.DISPEL_BY_SLOT)) {
                if (skill.isBad()) {
                    if (((L2Character) target).getEffectList().getBuffCount() == 0) {
                        return false;
                    }
                } else if (((L2Character) target).getEffectList().getDebuffCount() == 0) {
                    return false;
                }
            }

            // Check for damaged targets if using healing skill.
            return (((L2Character) target).getCurrentHp() != ((L2Character) target).getMaxHp()) || !skill.hasEffectType(L2EffectType.HEAL);
        }

        return true;
    }

    private boolean checkTarget(L2Object target) {
        if (target == null) {
            return false;
        }

        final L2Attackable npc = getActiveChar();
        if (target.isCharacter()) {
            if (((L2Character) target).isDead()) {
                return false;
            }

            if (npc.isMovementDisabled()) {
                if (!npc.isInsideRadius2D(target, npc.getPhysicalAttackRange() + npc.getTemplate().getCollisionRadius() + ((L2Character) target).getTemplate().getCollisionRadius())) {
                    return false;
                }

                if (!GeoEngine.getInstance().canSeeTarget(npc, target)) {
                    return false;
                }
            }

            return target.isAutoAttackable(npc);
        }

        // fixes monsters not avoiding obstacles
        return true; // GeoEngine.getInstance().canMoveToTarget(npc.getX(), npc.getY(), npc.getZ(), target.getX(), target.getY(), target.getZ(), npc.getInstanceWorld());
    }

    private L2Character skillTargetReconsider(Skill skill, boolean insideCastRange) {
        // Check if skill can be casted.
        final L2Attackable npc = getActiveChar();
        if (!SkillCaster.checkUseConditions(npc, skill)) {
            return null;
        }

        // There are cases where bad skills (negative effect points) are actually buffs and NPCs cast them on players, but they shouldn't.
        final boolean isBad = skill.isContinuous() ? skill.isDebuff() : skill.isBad();

        // Check current target first.
        final int range = insideCastRange ? skill.getCastRange() + getActiveChar().getTemplate().getCollisionRadius() : 2000; // TODO need some forget range

        Stream<L2Character> stream;
        if (isBad) {
            //@formatter:off
            stream = npc.getAggroList().values().stream()
                    .map(AggroInfo::getAttacker)
                    .filter(c -> checkSkillTarget(skill, c))
                    .sorted(Comparator.comparingInt(npc::getHating).reversed());
            //@formatter:on
        } else {
            stream = L2World.getInstance().getVisibleObjectsInRange(npc, L2Character.class, range, c -> checkSkillTarget(skill, c)).stream();

            // Maybe add self to the list of targets since getVisibleObjects doesn't return yourself.
            if (checkSkillTarget(skill, npc)) {
                stream = Stream.concat(stream, Stream.of(npc));
            }

            // For heal skills sort by hp missing.
            if (skill.hasEffectType(L2EffectType.HEAL)) {
                stream = stream.sorted(Comparator.comparingInt(L2Character::getCurrentHpPercent));
            }
        }

        // Return any target.
        return stream.findFirst().orElse(null);

    }

    private L2Character targetReconsider(boolean randomTarget) {
        final L2Attackable npc = getActiveChar();

        if (randomTarget) {
            Stream<L2Character> stream = npc.getAggroList().values().stream().map(AggroInfo::getAttacker).filter(this::checkTarget);

            // If npc is aggressive, add characters within aggro range too
            if (npc.isAggressive()) {
                stream = Stream.concat(stream, L2World.getInstance().getVisibleObjectsInRange(npc, L2Character.class, npc.getAggroRange(), this::checkTarget).stream());
            }

            return stream.findAny().orElse(null);
        }

        //@formatter:off
        return npc.getAggroList().values().stream()
                .filter(a -> checkTarget(a.getAttacker()))
                .sorted(Comparator.comparingInt(AggroInfo::getHate))
                .map(AggroInfo::getAttacker)
                .findFirst()
                .orElse(npc.isAggressive() ? L2World.getInstance().getVisibleObjectsInRange(npc, L2Character.class, npc.getAggroRange(), this::checkTarget).stream().findAny().orElse(null) : null);
        //@formatter:on
    }

    /**
     * Manage AI thinking actions of a L2Attackable.
     */
    @Override
    protected void onEvtThink() {
        // Check if the actor can't use skills and if a thinking action isn't already in progress
        if (_thinking || getActiveChar().isAllSkillsDisabled()) {
            return;
        }

        // Start thinking action
        _thinking = true;

        try {
            // Manage AI thinks of a L2Attackable
            switch (getIntention()) {
                case AI_INTENTION_ACTIVE: {
                    thinkActive();
                    break;
                }
                case AI_INTENTION_ATTACK: {
                    thinkAttack();
                    break;
                }
                case AI_INTENTION_CAST: {
                    thinkCast();
                    break;
                }
            }
        } catch (Exception e) {
            // LOGGER.warn(": " + this.getActor().getName() + " - onEvtThink() failed!");
        } finally {
            // Stop thinking action
            _thinking = false;
        }
    }

    /**
     * Launch actions corresponding to the Event Attacked.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Init the attack : Calculate the attack timeout, Set the _globalAggro to 0, Add the attacker to the actor _aggroList</li>
     * <li>Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance</li>
     * <li>Set the Intention to AI_INTENTION_ATTACK</li>
     * </ul>
     *
     * @param attacker The L2Character that attacks the actor
     */
    @Override
    protected void onEvtAttacked(L2Character attacker) {
        final L2Attackable me = getActiveChar();
        final L2Object target = getTarget();
        // Calculate the attack timeout
        _attackTimeout = MAX_ATTACK_TIMEOUT + GameTimeController.getInstance().getGameTicks();

        // Set the _globalAggro to 0 to permit attack even just after spawn
        if (_globalAggro < 0) {
            _globalAggro = 0;
        }

        // Add the attacker to the _aggroList of the actor
        me.addDamageHate(attacker, 0, 1);

        // Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
        if (!me.isRunning()) {
            me.setRunning();
        }

        if (!getActiveChar().isCoreAIDisabled()) {
            // Set the Intention to AI_INTENTION_ATTACK
            if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
                setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
            } else if (me.getMostHated() != target) {
                setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
            }
        }

        if (me.isMonster()) {
            L2MonsterInstance master = (L2MonsterInstance) me;

            if (master.hasMinions()) {
                master.getMinionList().onAssist(me, attacker);
            }

            master = master.getLeader();
            if ((master != null) && master.hasMinions()) {
                master.getMinionList().onAssist(me, attacker);
            }
        }

        super.onEvtAttacked(attacker);
    }

    /**
     * Launch actions corresponding to the Event Aggression.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Add the target to the actor _aggroList or update hate if already present</li>
     * <li>Set the actor Intention to AI_INTENTION_ATTACK (if actor is L2GuardInstance check if it isn't too far from its home location)</li>
     * </ul>
     *
     * @param aggro The value of hate to add to the actor against the target
     */
    @Override
    protected void onEvtAggression(L2Character target, int aggro) {
        final L2Attackable me = getActiveChar();
        if (me.isDead()) {
            return;
        }

        if (target != null) {
            // Add the target to the actor _aggroList or update hate if already present
            me.addDamageHate(target, 0, aggro);

            // Set the actor AI Intention to AI_INTENTION_ATTACK
            if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
                // Set the L2Character movement type to run and send Server->Client packet ChangeMoveType to all others L2PcInstance
                if (!me.isRunning()) {
                    me.setRunning();
                }

                setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            }

            if (me.isMonster()) {
                L2MonsterInstance master = (L2MonsterInstance) me;

                if (master.hasMinions()) {
                    master.getMinionList().onAssist(me, target);
                }

                master = master.getLeader();
                if ((master != null) && master.hasMinions()) {
                    master.getMinionList().onAssist(me, target);
                }
            }
        }
    }

    @Override
    protected void onIntentionActive() {
        // Cancel attack timeout
        _attackTimeout = Integer.MAX_VALUE;
        super.onIntentionActive();
    }

    public void setGlobalAggro(int value) {
        _globalAggro = value;
    }

    @Override
    public L2Object getTarget() {
        // NPCs share their regular target with AI target.
        return _actor.getTarget();
    }

    @Override
    public void setTarget(L2Object target) {
        // NPCs share their regular target with AI target.
        _actor.setTarget(target);
    }

    public L2Attackable getActiveChar() {
        return (L2Attackable) _actor;
    }
}
