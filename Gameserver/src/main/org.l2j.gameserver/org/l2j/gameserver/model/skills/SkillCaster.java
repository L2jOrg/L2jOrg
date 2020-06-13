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
package org.l2j.gameserver.model.skills;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.holders.SkillUseHolder;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.options.OptionsSkillHolder;
import org.l2j.gameserver.model.options.OptionsSkillType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author Nik
 */
public class SkillCaster implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillCaster.class);

    private final WeakReference<Creature> _caster;
    private final WeakReference<WorldObject> _target;
    private final Skill _skill;
    private final Item _item;
    private final SkillCastingType _castingType;
    private int _hitTime;
    private int _cancelTime;
    private int _coolTime;
    private Collection<WorldObject> _targets;
    private ScheduledFuture<?> _task;
    private int _phase;

    private SkillCaster(Creature caster, WorldObject target, Skill skill, Item item, SkillCastingType castingType, boolean ctrlPressed, boolean shiftPressed) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(skill);
        Objects.requireNonNull(castingType);

        _caster = new WeakReference<>(caster);
        _target = new WeakReference<>(target);
        _skill = skill;
        _item = item;
        _castingType = castingType;

        calcSkillTiming(caster, skill);
    }

    /**
     * Checks if the caster can cast the specified skill on the given target with the selected parameters.
     *
     * @param caster       the creature trying to cast
     * @param target       the selected target for cast
     * @param skill        the skill being cast
     * @param item         the reference item which requests the skill cast
     * @param castingType  the type of casting
     * @param ctrlPressed  force casting
     * @param shiftPressed dont move while casting
     * @return {@code SkillCaster} object containing casting data if casting has started or {@code null} if casting was not started.
     */
    public static SkillCaster castSkill(Creature caster, WorldObject target, Skill skill, Item item, SkillCastingType castingType, boolean ctrlPressed, boolean shiftPressed) {
        return castSkill(caster, target, skill, item, castingType, ctrlPressed, shiftPressed, -1);
    }

    /**
     * Checks if the caster can cast the specified skill on the given target with the selected parameters.
     *
     * @param caster       the creature trying to cast
     * @param target       the selected target for cast
     * @param skill        the skill being cast
     * @param item         the reference item which requests the skill cast
     * @param castingType  the type of casting
     * @param ctrlPressed  force casting
     * @param shiftPressed dont move while casting
     * @param castTime     custom cast time in milliseconds or -1 for default.
     * @return {@code SkillCaster} object containing casting data if casting has started or {@code null} if casting was not started.
     */
    public static SkillCaster castSkill(Creature caster, WorldObject target, Skill skill, Item item, SkillCastingType castingType, boolean ctrlPressed, boolean shiftPressed, int castTime) {
        if ((caster == null) || (skill == null) || (castingType == null)) {
            return null;
        }

        if (!checkUseConditions(caster, skill, castingType)) {
            return null;
        }

        // Check true aiming target of the skill.
        target = skill.getTarget(caster, target, ctrlPressed, shiftPressed, false);
        if (target == null) {
            return null;
        }

        // You should not heal/buff monsters without pressing the ctrl button.
        if (isPlayer(caster) && isMonster(target) && (skill.getEffectPoint() > 0) && !ctrlPressed) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return null;
        }

        if ((skill.getCastRange() > 0) && !GameUtils.checkIfInRange(skill.getCastRange(), caster, target, false)) {
            return null;
        }

        // Schedule a thread that will execute 500ms before casting time is over (for animation issues and retail handling).
        final SkillCaster skillCaster = new SkillCaster(caster, target, skill, item, castingType, ctrlPressed, shiftPressed);
        skillCaster.run();
        return skillCaster;
    }

    public static void callSkill(Creature caster, WorldObject target, Collection<WorldObject> targets, Skill skill, Item item) {
        // Launch the magic skill in order to calculate its effects
        try {
            // Mobius: Disabled characters should not be able to finish bad skills.
            if (caster.isAttackingDisabled() && skill.isBad()) {
                return;
            }

            // Check if the toggle skill effects are already in progress on the Creature
            if (skill.isToggle() && caster.isAffectedBySkill(skill.getId())) {
                return;
            }

            // Initial checks
            for (WorldObject obj : targets) {
                if (!isCreature(obj)) {
                    continue;
                }

                final Creature creature = (Creature) obj;

                // Check raid monster/minion attack and check buffing characters who attack raid monsters. Raid is still affected by skills.
                if (!Config.RAID_DISABLE_CURSE && creature.isRaid() && creature.giveRaidCurse() && (caster.getLevel() >= (creature.getLevel() + 9))) {
                    if (skill.isBad() || ((creature.getTarget() == caster) && ((Attackable) creature).getAggroList().containsKey(caster))) {
                        // Skills such as Summon Battle Scar too can trigger magic silence.
                        final CommonSkill curse = skill.isBad() ? CommonSkill.RAID_CURSE2 : CommonSkill.RAID_CURSE;
                        final Skill curseSkill = curse.getSkill();
                        if (curseSkill != null) {
                            curseSkill.applyEffects(creature, caster);
                        }
                    }
                }

                // Static skills not trigger any chance skills
                if (!skill.isStatic()) {
                    final Weapon activeWeapon = caster.getActiveWeaponItem();
                    // Launch weapon Special ability skill effect if available
                    if ((activeWeapon != null) && !creature.isDead()) {
                        activeWeapon.applyConditionalSkills(caster, creature, skill, ItemSkillType.ON_MAGIC_SKILL);
                    }

                    if (caster.hasTriggerSkills()) {
                        for (OptionsSkillHolder holder : caster.getTriggerSkills().values()) {
                            if ((skill.isMagic() && (holder.getSkillType() == OptionsSkillType.MAGIC)) || (skill.isPhysical() && (holder.getSkillType() == OptionsSkillType.ATTACK))) {
                                if (Rnd.get(100) < holder.getChance()) {
                                    triggerCast(caster, creature, holder.getSkill(), null, false);
                                }
                            }
                        }
                    }
                }
            }

            // Launch the magic skill and calculate its effects
            skill.activateSkill(caster, item, targets.toArray(new WorldObject[0]));

            final Player player = caster.getActingPlayer();
            if (player != null) {
                for (WorldObject obj : targets) {
                    if (!isCreature(obj)) {
                        continue;
                    }

                    if (skill.isBad()) {
                        if (isPlayable(obj)) {
                            // Update pvpflag.
                            player.updatePvPStatus((Creature) obj);

                            if (isSummon(obj)) {
                                ((Summon) obj).updateAndBroadcastStatus(1);
                            }
                        } else if (isAttackable(obj)) {
                            // Add hate to the attackable, and put it in the attack list.
                            ((Attackable) obj).addDamageHate(caster, 0, -skill.getEffectPoint());
                            ((Creature) obj).addAttackerToAttackByList(caster);
                        }

                        // notify target AI about the attack
                        if (((Creature) obj).hasAI() && !skill.hasAnyEffectType(EffectType.HATE)) {
                            ((Creature) obj).getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
                        }
                    }
                    // Self casting should not increase PvP time.
                    else if (obj != player)
                    {
                        // Supporting monsters or players results in pvpflag.
                        if (((skill.getEffectPoint() > 0) && isMonster(obj)) //
                                || (isPlayable(obj) && ((obj.getActingPlayer().getPvpFlag() > 0) //
                                || (((Creature) obj).getReputation() < 0) //
                        )))
                        {
                            player.updatePvPStatus();
                        }
                    }
                }

                // Mobs in range 1000 see spell
                World.getInstance().forEachVisibleObjectInRange(player, Npc.class, 1000, npcMob ->
                {
                    EventDispatcher.getInstance().notifyEventAsync(new OnNpcSkillSee(npcMob, player, skill, isSummon(caster), targets.toArray(WorldObject[]::new)), npcMob);

                    // On Skill See logic
                    if (isAttackable(npcMob)) {
                        final Attackable attackable = (Attackable) npcMob;

                        if (skill.getEffectPoint() > 0) {
                            if (attackable.hasAI() && (attackable.getAI().getIntention() == AI_INTENTION_ATTACK)) {
                                final WorldObject npcTarget = attackable.getTarget();
                                for (WorldObject skillTarget : targets) {
                                    if ((npcTarget == skillTarget) || (npcMob == skillTarget)) {
                                        final Creature originalCaster = isSummon(caster) ? caster : player;
                                        attackable.addDamageHate(originalCaster, 0, (skill.getEffectPoint() * 150) / (attackable.getLevel() + 7));
                                    }
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.warn(caster + " callSkill() failed.", e);
        }
    }

    public static void triggerCast(Creature activeChar, Creature target, Skill skill) {
        triggerCast(activeChar, target, skill, null, true);
    }

    public static void triggerCast(Creature activeChar, WorldObject target, Skill skill, Item item, boolean ignoreTargetType) {
        try {
            if ((activeChar == null) || (skill == null)) {
                return;
            }

            if (skill.checkCondition(activeChar, target)) {
                if (activeChar.isSkillDisabled(skill)) {
                    return;
                }

                if (skill.getReuseDelay() > 0) {
                    activeChar.disableSkill(skill, skill.getReuseDelay());
                }

                if (!ignoreTargetType) {
                    final WorldObject objTarget = skill.getTarget(activeChar, false, false, false);
                    if (isCreature(objTarget)) {
                        target = objTarget;
                    }
                }

                final WorldObject[] targets = skill.getTargetsAffected(activeChar, target).toArray(new WorldObject[0]);

                if (!skill.isNotBroadcastable()) {
                    activeChar.broadcastPacket(new MagicSkillUse(activeChar, target, skill.getDisplayId(), skill.getLevel(), 0, 0));
                }

                // Launch the magic skill and calculate its effects
                skill.activateSkill(activeChar, item, targets);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed simultaneous cast: ", e);
        }
    }

    /**
     * Checks general conditions for casting a skill through the regular casting type.
     *
     * @param caster the caster checked if can cast the given skill.
     * @param skill  the skill to be check if it can be casted by the given caster or not.
     * @return {@code true} if the caster can proceed with casting the given skill, {@code false} otherwise.
     */
    public static boolean checkUseConditions(Creature caster, Skill skill) {
        return checkUseConditions(caster, skill, SkillCastingType.NORMAL);
    }

    /**
     * Checks general conditions for casting a skill.
     *
     * @param caster      the caster checked if can cast the given skill.
     * @param skill       the skill to be check if it can be casted by the given caster or not.
     * @param castingType used to check if caster is currently casting this type of cast.
     * @return {@code true} if the caster can proceed with casting the given skill, {@code false} otherwise.
     */
    public static boolean checkUseConditions(Creature caster, Skill skill, SkillCastingType castingType) {
        if (caster == null) {
            return false;
        }

        if ((skill == null) || caster.isSkillDisabled(skill) || ((skill.isFlyType() && caster.isMovementDisabled()))) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillUse(caster, skill, skill.isWithoutAction()), caster, TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if creature is already casting
        if ((castingType != null) && caster.isCastingNow(castingType)) {
            caster.sendPacket(ActionFailed.get(castingType));
            return false;
        }

        // Check if the caster has enough MP
        if (caster.getCurrentMp() < (caster.getStats().getMpConsume(skill) + caster.getStats().getMpInitialConsume(skill))) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Check if the caster has enough HP
        if (caster.getCurrentHp() <= skill.getHpConsume()) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        // Skill mute checks.
        if (!skill.isStatic()) {
            // Check if the skill is a magic spell and if the Creature is not muted
            if (skill.isMagic()) {
                if (caster.isMuted()) {
                    caster.sendPacket(ActionFailed.STATIC_PACKET);
                    return false;
                }
            } else if (caster.isPhysicalMuted()) // Check if the skill is physical and if the Creature is not physical_muted
            {
                caster.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        }

        // Check if the caster's weapon is limited to use only its own skills
        final Weapon weapon = caster.getActiveWeaponItem();
        if ((weapon != null) && weapon.useWeaponSkillsOnly() && !caster.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            final List<ItemSkillHolder> weaponSkills = weapon.getSkills(ItemSkillType.NORMAL);
            if ((weaponSkills != null) && !weaponSkills.stream().anyMatch(sh -> sh.getSkillId() == skill.getId())) {
                caster.sendPacket(SystemMessageId.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPON_S_SKILL);
                return false;
            }
        }

        // Check if a spell consumes an item.
        if ((skill.getItemConsumeId() > 0) && (skill.getItemConsumeCount() > 0) && (caster.getInventory() != null)) {
            // Get the Item consumed by the spell
            final Item requiredItem = caster.getInventory().getItemByItemId(skill.getItemConsumeId());
            if ((requiredItem == null) || (requiredItem.getCount() < skill.getItemConsumeCount())) {
                if (skill.hasAnyEffectType(EffectType.SUMMON)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.SUMMONING_A_SERVITOR_COSTS_S2_S1);
                    sm.addItemName(skill.getItemConsumeId());
                    sm.addInt(skill.getItemConsumeCount());
                    caster.sendPacket(sm);
                } else {
                    caster.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL));
                }
                return false;
            }
        }

        if (isPlayer(caster)) {
            final Player player = caster.getActingPlayer();
            if (player.inObserverMode()) {
                return false;
            }

            if (player.isInOlympiadMode() && skill.isBlockedInOlympiad()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_SKILL_IN_A_OLYMPIAD_MATCH);
                return false;
            }

            // Check for skill reuse (fixes macro right click press exploit).
            if (caster.hasSkillReuse(skill.getReuseHashCode())) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE);
                sm.addSkillName(skill);
                caster.sendPacket(sm);
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        final boolean instantCast = (_castingType == SkillCastingType.SIMULTANEOUS) || _skill.isAbnormalInstant() || _skill.isWithoutAction() || _skill.isToggle();

        // Skills with instant cast are never launched.
        if (instantCast) {
            triggerCast(_caster.get(), _target.get(), _skill, _item, false);
            return;
        }

        long nextTaskDelay = 0;
        boolean hasNextPhase = false;
        switch (_phase++) {
            case 0: // Start skill casting.
            {
                hasNextPhase = startCasting();
                nextTaskDelay = _hitTime;
                break;
            }
            case 1: // Launch the skill.
            {
                hasNextPhase = launchSkill();
                nextTaskDelay = _cancelTime;
                break;
            }
            case 2: // Finish launching and apply effects.
            {
                hasNextPhase = finishSkill();
                nextTaskDelay = _coolTime;
                break;
            }
        }

        // Reschedule next task if we have such.
        if (hasNextPhase) {
            _task = ThreadPool.schedule(this, nextTaskDelay);
        } else {
            // Stop casting if there is no next phase.
            stopCasting(false);
        }
    }

    public boolean startCasting() {
        final Creature caster = _caster.get();
        final WorldObject target = _target.get();

        if ((caster == null) || (target == null)) {
            return false;
        }

        _coolTime = Formulas.calcAtkSpd(caster, _skill, _skill.getCoolTime()); // TODO Get proper formula of this.
        final int displayedCastTime = _hitTime + _cancelTime; // For client purposes, it must be displayed to player the skill casting time + launch time.
        final boolean instantCast = (_castingType == SkillCastingType.SIMULTANEOUS) || _skill.isAbnormalInstant() || _skill.isWithoutAction();

        // Add this SkillCaster to the creature so it can be marked as casting.
        if (!instantCast) {
            caster.addSkillCaster(_castingType, this);
        }

        // Disable the skill during the re-use delay and create a task EnableSkill with Medium priority to enable it at the end of the re-use delay
        int reuseDelay = caster.getStats().getReuseTime(_skill);
        if (reuseDelay > 10) {
            if (!_skill.isStatic()  && _skill.getOperateType() == SkillOperateType.A1 && Formulas.calcSkillMastery(caster, _skill)) {
                reuseDelay = 100;
                caster.sendPacket(SystemMessageId.A_SKILL_IS_READY_TO_BE_USED_AGAIN);
            }

            if (reuseDelay > 30000) {
                caster.addTimeStamp(_skill, reuseDelay);
            } else {
                caster.disableSkill(_skill, reuseDelay);
            }
        }

        // Stop movement when casting. Except instant cast.
        if (!instantCast) {
            caster.getAI().clientStopMoving(null);
        }

        if (target != caster) {
            // Face the target
            caster.setHeading(calculateHeadingFrom(caster, target));
            caster.broadcastPacket(new ExRotation(caster.getObjectId(), caster.getHeading())); // TODO: Not sent in retail. Probably moveToPawn is enough

            // Send MoveToPawn packet to trigger Blue Bubbles on target become Red, but don't do it while (double) casting, because that will screw up animation... some fucked up stuff, right?
            if (isPlayer(caster) && !caster.isCastingNow() && isCreature(target)) {
                caster.sendPacket(new MoveToPawn(caster, target, (int) MathUtil.calculateDistance2D(caster, target)));
                caster.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }

        // Stop effects since we started casting (except for skills without action). It should be sent before casting bar and mana consume.
        if (!_skill.isWithoutAction()) {
            caster.stopEffectsOnAction();
        }

        // Consume skill initial MP needed for cast. Retail sends it regardless if > 0 or not.
        final int initmpcons = caster.getStats().getMpInitialConsume(_skill);
        if (initmpcons > 0) {
            if (initmpcons > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }

            caster.getStatus().reduceMp(initmpcons);
            final StatusUpdate su = new StatusUpdate(caster);
            su.addUpdate(StatusUpdateType.CUR_MP, (int) caster.getCurrentMp());
            caster.sendPacket(su);
        }

        // Send a packet starting the casting.
        final int actionId = isSummon(caster) ? ActionManager.getInstance().getSkillActionId(_skill.getId()) : -1;
        if (!_skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillUse(caster, target, _skill.getDisplayId(), _skill.getDisplayLevel(), displayedCastTime, reuseDelay, _skill.getReuseDelayGroup(), actionId, _castingType));
        }

        if (isPlayer(caster) && !instantCast) {

            caster.sendPacket(_skill.getId() != 2046 ? SystemMessage.getSystemMessage(SystemMessageId.YOU_USE_S1).addSkillName(_skill) : SystemMessage.getSystemMessage(SystemMessageId.SUMMONING_YOUR_PET));
        }

        // Consume reagent item.
        if ((_skill.getItemConsumeId() > 0) && (_skill.getItemConsumeCount() > 0) && (caster.getInventory() != null)) {
            // Get the Item consumed by the spell.
            final Item requiredItem = caster.getInventory().getItemByItemId(_skill.getItemConsumeId());
            if (_skill.isBad() || (requiredItem.getAction() == ActionType.NONE)) // Non reagent items are removed at finishSkill or item handler.
            {
                caster.destroyItem(_skill.toString(), requiredItem.getObjectId(), _skill.getItemConsumeCount(), caster, false);
            }
        }

        // Trigger any skill cast start effects.
        if (isCreature(target)) {
            _skill.applyEffectScope(EffectScope.START, new BuffInfo(caster, (Creature) target, _skill, false, _item, null), true, false);
        }

        // Start channeling if skill is channeling.
        if (_skill.isChanneling()) {
            caster.getSkillChannelizer().startChanneling(_skill);
        }

        return true;
    }

    public boolean launchSkill() {
        final Creature caster = _caster.get();
        final WorldObject target = _target.get();

        if ((caster == null) || (target == null)) {
            return false;
        }

        if ((_skill.getEffectRange() > 0) && !GameUtils.checkIfInRange(_skill.getEffectRange(), caster, target, true)) {
            if (isPlayer(caster)) {
                caster.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return false;
        }

        // Gather list of affected targets by this skill.
        _targets = _skill.getTargetsAffected(caster, target);

        // Finish flying by setting the target location after picking targets. Packet is sent before MagicSkillLaunched.
        if (_skill.isFlyType()) {
            handleSkillFly(caster, target);
        }

        // Display animation of launching skill upon targets.
        if (!_skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillLaunched(caster, _skill.getDisplayId(), _skill.getDisplayLevel(), _castingType, _targets));
        }
        return true;
    }

    public boolean finishSkill() {
        final Creature caster = _caster.get();
        final WorldObject target = _target.get();

        if ((caster == null) || (target == null)) {
            return false;
        }

        final StatusUpdate su = new StatusUpdate(caster);

        // Consume the required MP or stop casting if not enough.
        final double mpConsume = _skill.getMpConsume() > 0 ? caster.getStats().getMpConsume(_skill) : 0;
        if (mpConsume > 0) {
            if (mpConsume > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }

            caster.getStatus().reduceMp(mpConsume);
            su.addUpdate(StatusUpdateType.CUR_MP, (int) caster.getCurrentMp());
        }

        // Consume the required HP or stop casting if not enough.
        final double consumeHp = _skill.getHpConsume();
        if (consumeHp > 0) {
            if (consumeHp >= caster.getCurrentHp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
                return false;
            }

            caster.getStatus().reduceHp(consumeHp, caster, true);
            su.addUpdate(StatusUpdateType.CUR_HP, (int) caster.getCurrentHp());
        }

        // Send HP/MP consumption packet if any attribute is set.
        if (su.hasUpdates()) {
            caster.sendPacket(su);
        }

        if (isPlayer(caster)) {
            // Consume Souls if necessary.
            if ((_skill.getMaxSoulConsumeCount() > 0) && !caster.getActingPlayer().decreaseSouls(_skill.getMaxSoulConsumeCount(), _skill)) {
                return false;
            }

            // Consume charges if necessary.
            if ((_skill.getChargeConsumeCount() > 0) && !caster.getActingPlayer().decreaseCharges(_skill.getChargeConsumeCount())) {
                return false;
            }
        }

        // Consume skill reduced item on success.
        if ((_item != null) && (_item.getAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS) && (_skill.getItemConsumeId() > 0) && (_skill.getItemConsumeCount() > 0)) {
            if (!caster.destroyItem(_skill.toString(), _item.getObjectId(), _skill.getItemConsumeCount(), target, true)) {
                return false;
            }
        }

        // Notify skill is casted.
        EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillFinishCast(caster, target, _skill, _skill.isWithoutAction()), caster);

        // Call the skill's effects and AI interraction and stuff.
        callSkill(caster, target, _targets, _skill, _item);

        // Start attack stance.
        if (!_skill.isWithoutAction()) {
            if (_skill.isBad() && (_skill.getTargetType() != TargetType.DOOR_TREASURE)) {
                caster.getAI().clientStartAutoAttack();
            }
        }

        // Notify DP Scripts
        caster.notifyQuestEventSkillFinished(_skill, target);

        // On each repeat recharge shots before cast.
        if(_skill.useSoulShot()) {
            caster.consumeAndRechargeShots(ShotType.SOULSHOTS, _targets.size());
        }
        if(_skill.useSpiritShot()) {
            caster.consumeAndRechargeShots(ShotType.SPIRITSHOTS, _targets.size());
        }
        return true;
    }

    /**
     * Stops this casting and cleans all cast parameters.<br>
     *
     * @param aborted if {@code true}, server will send packets to the player, notifying him that the skill has been aborted.
     */
    public void stopCasting(boolean aborted) {
        // Cancel the task and unset it.
        if (_task != null) {
            _task.cancel(false);
            _task = null;
        }

        final Creature caster = _caster.get();
        final WorldObject target = _target.get();
        if (caster == null) {
            return;
        }

        caster.removeSkillCaster(_castingType);

        if (caster.isChanneling()) {
            caster.getSkillChannelizer().stopChanneling();
        }

        // If aborted, broadcast casting aborted.
        if (aborted) {
            caster.broadcastPacket(new MagicSkillCanceld(caster.getObjectId())); // broadcast packet to stop animations client-side
            caster.sendPacket(ActionFailed.get(_castingType)); // send an "action failed" packet to the caster
        }

        // If there is a queued skill, launch it and wipe the queue.
        if (isPlayer(caster)) {
            final Player currPlayer = caster.getActingPlayer();
            final SkillUseHolder queuedSkill = currPlayer.getQueuedSkill();

            if (queuedSkill != null) {
                ThreadPool.execute(() ->
                {
                    currPlayer.setQueuedSkill(null, null, false, false);
                    currPlayer.useMagic(queuedSkill.getSkill(), queuedSkill.getItem(), queuedSkill.isCtrlPressed(), queuedSkill.isShiftPressed());
                });

                return;
            }
        }
        // Attack target after skill use.
        if ((_skill.getNextAction() != NextActionType.NONE) && (caster.getAI().getNextIntention() == null))
        {
            if ((_skill.getNextAction() == NextActionType.ATTACK) && (target != null) && (target != caster) && target.isAutoAttackable(caster))
            {
                caster.getAI().setIntention(AI_INTENTION_ATTACK, target);
            }
            else if ((_skill.getNextAction() == NextActionType.CAST) && (target != null) && (target != caster) && target.isAutoAttackable(caster))
            {
                caster.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, _skill, target, _item, false, false);
            }
            else
            {
                caster.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING);
            }
        }
        else
        {
            caster.getAI().notifyEvent(CtrlEvent.EVT_FINISH_CASTING);
        }
    }

    private void calcSkillTiming(Creature creature, Skill skill) {
        final double timeFactor = Formulas.calcSkillTimeFactor(creature, skill);
        final double cancelTime = Formulas.calcSkillCancelTime(creature, skill);
        if (skill.getOperateType().isChanneling()) {
            _hitTime = (int) Math.max(skill.getHitTime() - cancelTime, 0);
            _cancelTime = 2866;
        } else {
            _hitTime = (int) Math.max((skill.getHitTime() / timeFactor) - cancelTime, 0);
            _cancelTime = (int) cancelTime;
        }
        _coolTime = (int) (skill.getCoolTime() / timeFactor); // cooltimeMillis / timeFactor
    }

    /**
     * @return the skill that is casting.
     */
    public Skill getSkill() {
        return _skill;
    }

    /**
     * @return the creature casting the skill.
     */
    public Creature getCaster() {
        return _caster.get();
    }

    /**
     * @return the target this skill is being cast on.
     */
    public WorldObject getTarget() {
        return _target.get();
    }

    /**
     * @return the item that has been used in this casting.
     */
    public Item getItem() {
        return _item;
    }

    /**
     * @return {@code true} if casting can be aborted through regular means such as cast break while being attacked or while cancelling target, {@code false} otherwise.
     */
    public boolean canAbortCast() {
        return getCaster().getTarget() == null; // When targets are allocated, that means skill is already launched, therefore cannot be aborted.
    }

    /**
     * @return the type of this caster, which also defines the casting display bar on the player.
     */
    public SkillCastingType getCastingType() {
        return _castingType;
    }

    public boolean isNormalFirstType() {
        return _castingType == SkillCastingType.NORMAL;
    }

    public boolean isNormalSecondType() {
        return _castingType == SkillCastingType.NORMAL_SECOND;
    }

    public boolean isAnyNormalType() {
        return (_castingType == SkillCastingType.NORMAL) || (_castingType == SkillCastingType.NORMAL_SECOND);
    }

    @Override
    public String toString() {
        return super.toString() + " [caster: " + _caster.get() + " skill: " + _skill + " target: " + _target.get() + " type: " + _castingType + "]";
    }

    private void handleSkillFly(Creature creature, WorldObject target) {
        int x = 0;
        int y = 0;
        int z = 0;
        FlyToLocation.FlyType flyType = FlyToLocation.FlyType.CHARGE;
        switch (_skill.getOperateType()) {
            case DA4:
            case DA5: {
                final double course = _skill.getOperateType() == SkillOperateType.DA4 ? Math.toRadians(270) : Math.toRadians(90);
                final double radian = Math.toRadians(convertHeadingToDegree(target.getHeading()));
                double nRadius = creature.getCollisionRadius();
                if (isCreature(target)) {
                    nRadius += ((Creature) target).getCollisionRadius();
                }
                x = target.getX() + (int) (Math.cos(Math.PI + radian + course) * nRadius);
                y = target.getY() + (int) (Math.sin(Math.PI + radian + course) * nRadius);
                z = target.getZ();
                break;
            }
            case DA3: {
                flyType = FlyToLocation.FlyType.WARP_BACK;
                final double radian = Math.toRadians(convertHeadingToDegree(creature.getHeading()));
                x = creature.getX() + (int) (Math.cos(Math.PI + radian) * _skill.getCastRange());
                y = creature.getY() + (int) (Math.sin(Math.PI + radian) * _skill.getCastRange());
                z = creature.getZ();
                break;
            }
            case DA2:
            case DA1: {
                if (creature == target) {
                    final double course = Math.toRadians(180);
                    final double radian = Math.toRadians(convertHeadingToDegree(creature.getHeading()));
                    x = creature.getX() + (int) (Math.cos(Math.PI + radian + course) * _skill.getCastRange());
                    y = creature.getY() + (int) (Math.sin(Math.PI + radian + course) * _skill.getCastRange());
                    z = creature.getZ();
                } else {
                    final var radius = creature.getCollisionRadius() + ( target instanceof Creature c ? c.getCollisionRadius() : 0);
                    final var angle = MathUtil.calculateAngleFrom(creature, target);
                    x = (int) (target.getX() + (radius * Math.cos(angle)));
                    y = (int) (target.getY() + (radius * Math.sin(angle)));
                    z = target.getZ();
                }
                break;
            }
        }

        final Location destination = creature.isFlying() ? new Location(x, y, z) : GeoEngine.getInstance().canMoveToTargetLoc(creature.getX(), creature.getY(), creature.getZ(), x, y, z, creature.getInstanceWorld());

        creature.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        creature.broadcastPacket(new FlyToLocation(creature, destination, flyType, 0, 0, 333));
        creature.setXYZ(destination);
        creature.revalidateZone(true);
    }
}
