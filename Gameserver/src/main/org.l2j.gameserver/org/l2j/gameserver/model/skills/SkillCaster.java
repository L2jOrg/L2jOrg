/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.*;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.SkillUseHolder;
import org.l2j.gameserver.engine.item.Weapon;
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
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * @author Nik
 */
public class SkillCaster implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillCaster.class);

    private final WeakReference<Creature> caster;
    private final WeakReference<WorldObject> target;
    private final Skill skill;
    private final Item item;
    private final SkillCastingType castingType;
    private final boolean instantCast;
    private int hitTime;
    private int cancelTime;
    private int coolTime;
    private Collection<WorldObject> targets;
    private ScheduledFuture<?> _task;
    private int phase;

    private SkillCaster(Creature caster, WorldObject target, Skill skill, Item item, SkillCastingType castingType, boolean forceUse, boolean dontMove) {
        Objects.requireNonNull(caster);
        Objects.requireNonNull(skill);
        Objects.requireNonNull(castingType);

        this.caster = new WeakReference<>(caster);
        this.target = new WeakReference<>(target);
        this.skill = skill;
        this.item = item;
        this.castingType = castingType;
        instantCast = castingType == SkillCastingType.SIMULTANEOUS || skill.isAbnormalInstant() || skill.isWithoutAction() || skill.isToggle();

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
     * @param forceUse  force casting
     * @param dontMove dont move while casting
     * @param castTime     custom cast time in milliseconds or -1 for default.
     * @return {@code SkillCaster} object containing casting data if casting has started or {@code null} if casting was not started.
     */
    public static SkillCaster castSkill(Creature caster, WorldObject target, Skill skill, Item item, SkillCastingType castingType, boolean forceUse, boolean dontMove, int castTime) {
        if (isNull(castingType)) {
            return null;
        }

        if (!checkUseConditions(caster, skill, castingType)) {
            return null;
        }

        target = skill.getTarget(caster, target, forceUse, dontMove, false);
        if (isNull(target)) {
            return null;
        }

        if (isPlayer(caster) && isMonster(target) && skill.getEffectPoint() > 0 && !forceUse) {
            caster.sendPacket(SystemMessageId.INVALID_TARGET);
            return null;
        }

        if (skill.getCastRange() > 0 && !GameUtils.checkIfInRange(skill.getCastRange(), caster, target, false)) {
            return null;
        }

        final SkillCaster skillCaster = new SkillCaster(caster, target, skill, item, castingType, forceUse, dontMove);
        skillCaster.run();
        return skillCaster;
    }

    private void callSkill(Creature caster, Collection<WorldObject> targets, Skill skill, Item item) {
        try {
            if (caster.isAttackingDisabled() && skill.isBad()) {
                return;
            }

            if (skill.isToggle() && caster.isAffectedBySkill(skill.getId())) {
                return;
            }

            for (WorldObject obj : targets) {
                if (!(obj instanceof Creature creature)) {
                    continue;
                }

                checkRaidCurse(caster, skill, creature);
                triggerSkills(caster, skill, creature);
            }

            final Player player = caster.getActingPlayer();
            if (nonNull(player)) {
                for (WorldObject obj : targets) {
                    if (!(obj instanceof Creature creature)) {
                        continue;
                    }
                    notifyTarget(caster, skill, player, creature);
                }
                notifyNpcSkillSee(caster, targets, skill, player);
            }

            skill.activateSkill(caster, item, targets.toArray(new WorldObject[0]));
        } catch (Exception e) {
            LOGGER.warn(caster + " callSkill() failed.", e);
        }
    }

    private void notifyTarget(Creature caster, Skill skill, Player player, Creature creature) {
        if (skill.isBad()) {
            if (creature instanceof Playable playable) {
                player.updatePvPStatus(playable);

                if (playable instanceof Summon summon) {
                    summon.updateAndBroadcastStatus(1);
                }
            } else if (creature instanceof Attackable attackable) {
                attackable.addDamageHate(caster, 0, -skill.getEffectPoint());
                attackable.addAttackerToAttackByList(caster);
            }

            if (creature.hasAI() && !skill.hasAnyEffectType(EffectType.HATE)) {
                creature.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, caster);
            }
        }
        else if (creature != player) {
            // Supporting monsters or players results in pvpflag.
            if (skill.getEffectPoint() > 0 && isMonster(creature)
                    || (isPlayable(creature) && creature.getActingPlayer().getPvpFlag() > 0)
                    || creature.getReputation() < 0) {
                player.updatePvPStatus();
            }
        }
    }

    private void notifyNpcSkillSee(Creature caster, Collection<WorldObject> targets, Skill skill, Player player) {
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

    private static void triggerSkills(Creature caster, Skill skill, Creature creature) {
        if (!skill.isStatic()) {
            final Weapon activeWeapon = caster.getActiveWeaponItem();
            if (nonNull(activeWeapon) && !creature.isDead()) {
                activeWeapon.applyConditionalSkills(caster, creature, skill, ItemSkillType.ON_MAGIC_SKILL);
            }

            caster.forEachTriggerSkill(holder -> triggerCast(caster, skill, creature, holder));
        }
    }

    private static void triggerCast(Creature caster, Skill skill, Creature creature, OptionsSkillHolder holder) {
        if ((skill.isMagic() && (holder.getSkillType() == OptionsSkillType.MAGIC)) || (skill.isPhysical() && (holder.getSkillType() == OptionsSkillType.ATTACK))) {
            if (Rnd.chance(holder.getChance())) {
                triggerCast(caster, creature, holder.getSkill(), null, false);
            }
        }
    }

    private static void checkRaidCurse(Creature caster, Skill skill, Creature creature) {
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
                    activeChar.broadcastPacket(new MagicSkillUse(activeChar, target, skill, 0));
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
    private static boolean checkUseConditions(Creature caster, Skill skill, SkillCastingType castingType) {
        if (isNull(caster)) {
            return false;
        }

        if (isNull(skill) || caster.isSkillDisabled(skill) || (skill.isFlyType() && caster.isMovementDisabled())) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillUse(caster, skill, skill.isWithoutAction()), caster, TerminateReturn.class);
        if (nonNull(term) && term.terminate()) {
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (nonNull(castingType) && caster.isCastingNow(castingType)) {
            caster.sendPacket(ActionFailed.of(castingType));
            return false;
        }

        if (!skill.isStatic()) {
            if (skill.isMagic()) {
                if (caster.isMuted()) {
                    caster.sendPacket(ActionFailed.STATIC_PACKET);
                    return false;
                }
            } else if (caster.isPhysicalMuted()) {
                caster.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        }

        if (!checkWeaponUseOnlyOwnSkills(caster, skill)) {
            return false;
        }

        if (!checkSkillConsume(caster, skill)) {
            return false;
        }

        if (caster instanceof Player player) {
            if (player.isInObserverMode()) {
                return false;
            }

            if (player.isInOlympiadMode() && skill.isBlockedInOlympiad()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_SKILL_IN_A_OLYMPIAD_MATCH);
                return false;
            }

            if (caster.hasSkillReuse(skill.getReuseHashCode())) {
                caster.sendPacket(getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE).addSkillName(skill));
                return false;
            }
        }
        return true;
    }

    public static boolean checkSkillConsume(Creature caster, Skill skill) {
        if (caster.getCurrentMp() < (caster.getStats().getMpConsume(skill) + caster.getStats().getMpInitialConsume(skill))) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (caster.getCurrentHp() <= skill.getHpConsume()) {
            caster.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
            caster.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }

        if (skill.getItemConsumeId() > 0 && skill.getItemConsumeCount() > 0 && nonNull(caster.getInventory())) {
            final Item requiredItem = caster.getInventory().getItemByItemId(skill.getItemConsumeId());
            if (isNull(requiredItem) || requiredItem.getCount() < skill.getItemConsumeCount()) {
                if (skill.hasAnyEffectType(EffectType.SUMMON)) {
                    caster.sendPacket(getSystemMessage(SystemMessageId.SUMMONING_A_SERVITOR_COSTS_S2_S1)
                                     .addItemName(skill.getItemConsumeId()).addInt(skill.getItemConsumeCount()));
                } else {
                    caster.sendPacket(getSystemMessage(SystemMessageId.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL));
                }
                return false;
            }
        }
        return true;
    }

    private static boolean checkWeaponUseOnlyOwnSkills(Creature caster, Skill skill) {
        final Weapon weapon = caster.getActiveWeaponItem();
        if (nonNull(weapon) && weapon.useWeaponSkillsOnly() && !caster.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            if(!weapon.hasSkill(ItemSkillType.NORMAL, skill.getId())) {
                caster.sendPacket(SystemMessageId.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPON_S_SKILL);
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        if (instantCast) {
            triggerCast(caster.get(), target.get(), skill, item, false);
            return;
        }

        long nextTaskDelay = 0;
        boolean hasNextPhase = false;
        switch (phase++) {
            case 0: // Start skill casting.
            {
                hasNextPhase = startCasting();
                nextTaskDelay = hitTime;
                break;
            }
            case 1: // Launch the skill.
            {
                hasNextPhase = launchSkill();
                nextTaskDelay = cancelTime;
                break;
            }
            case 2: // Finish launching and apply effects.
            {
                hasNextPhase = finishSkill();
                nextTaskDelay = coolTime;
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

    private boolean startCasting() {
        final Creature caster = this.caster.get();
        final WorldObject target = this.target.get();

        if (isNull(caster) || isNull(target)) {
            return false;
        }

        coolTime = Formulas.calcAtkSpd(caster, skill); // TODO Get proper formula of this.
        // For client purposes, it must be displayed to player the skill casting time + launch time.
        final int displayedCastTime = hitTime + cancelTime;

        if (!instantCast) {
            caster.addSkillCaster(castingType, this);
        }

        int reuseDelay = caster.getStats().getReuseTime(skill);
        if (reuseDelay > 10) {
            if (!skill.isStatic()  && skill.getOperateType() == SkillOperateType.A1 && Formulas.calcSkillMastery(caster, skill)) {
                reuseDelay = 100;
                caster.sendPacket(SystemMessageId.A_SKILL_IS_READY_TO_BE_USED_AGAIN);
            }

            if (reuseDelay > 30000) {
                caster.addTimeStamp(skill, reuseDelay);
            } else {
                caster.disableSkill(skill, reuseDelay);
            }
        }

        if (!instantCast) {
            caster.getAI().clientStopMoving(null);
        }

        if (target != caster) {
            caster.setHeading(calculateHeadingFrom(caster, target));
            caster.broadcastPacket(new ExRotation(caster.getObjectId(), caster.getHeading())); // TODO: Not sent in retail. Probably moveToPawn is enough

            // Send MoveToPawn packet to trigger Blue Bubbles on target become Red, but don't do it while (double) casting, because that will screw up animation... some fucked up stuff, right?
            if (isPlayer(caster) && !caster.isCastingNow() && isCreature(target)) {
                caster.sendPacket(new MoveToPawn(caster, target, (int) MathUtil.calculateDistance2D(caster, target)));
                caster.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }

        if (!skill.isWithoutAction()) {
            caster.stopEffectsOnAction();
        }

        // Consume skill initial MP needed for cast. Retail sends it regardless if > 0 or not.
        final int mpInitialConsume = caster.getStats().getMpInitialConsume(skill);
        if (mpInitialConsume > 0) {
            if (mpInitialConsume > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }

            caster.getStatus().reduceMp(mpInitialConsume);
            caster.sendPacket(new StatusUpdate(caster).addUpdate(StatusUpdateType.CUR_MP, (int) caster.getCurrentMp()));
        }

        // Send a packet starting the casting.
        final int actionId = isSummon(caster) ? ActionManager.getInstance().getSkillActionId(skill.getId()) : -1;
        if (!skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillUse(caster, target, skill, displayedCastTime, reuseDelay, actionId, castingType));
        }

        if (isPlayer(caster) && !instantCast) {
            caster.sendPacket(skill.getId() != CommonSkill.SUMMON_PET.getId() ? getSystemMessage(SystemMessageId.YOU_USE_S1).addSkillName(skill) : getSystemMessage(SystemMessageId.SUMMONING_YOUR_PET));
        }

        consumeSkillItem(caster);

        if (target instanceof  Creature creature && skill.hasEffects(EffectScope.START)) {
            skill.applyEffectScope(EffectScope.START, new BuffInfo(caster, creature, skill, false, item, null), true, false);
        }

        // Start channeling if skill is channeling.
        if (skill.isChanneling()) {
            caster.getSkillChannelizer().startChanneling(skill);
        }
        return true;
    }

    private void consumeSkillItem(Creature caster) {
        if (skill.getItemConsumeId() > 0 && skill.getItemConsumeCount() > 0 && nonNull(caster.getInventory())) {
            final Item requiredItem = caster.getInventory().getItemByItemId(skill.getItemConsumeId());
            if (skill.isBad() || (requiredItem.getAction() == ActionType.NONE)) // Non reagent items are removed at finishSkill or item handler.
            {
                caster.destroyItem(skill.toString(), requiredItem.getObjectId(), skill.getItemConsumeCount(), caster, false);
            }
        }
    }

    public boolean launchSkill() {
        final Creature caster = this.caster.get();
        final WorldObject target = this.target.get();

        if (isNull(caster) || isNull(target)) {
            return false;
        }

        if (skill.getEffectRange() > 0 && !GameUtils.checkIfInRange(skill.getEffectRange(), caster, target, true)) {
            if (isPlayer(caster)) {
                caster.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            }
            return false;
        }

        targets = skill.getTargetsAffected(caster, target);

        if (skill.isFlyType()) {
            handleSkillFly(caster, target);
        }

        // Display animation of launching skill upon targets.
        if (!skill.isNotBroadcastable()) {
            caster.broadcastPacket(new MagicSkillLaunched(caster, skill.getDisplayId(), skill.getDisplayLevel(), castingType, targets));
        }
        return true;
    }

    public boolean finishSkill() {
        final Creature caster = this.caster.get();
        final WorldObject target = this.target.get();

        if (isNull(caster) || isNull(target)) {
            return false;
        }

        if (!consumeSkill(caster, target)) {
            return false;
        }

        EventDispatcher.getInstance().notifyEvent(new OnCreatureSkillFinishCast(caster, target, skill, skill.isWithoutAction()), caster);

        callSkill(caster, targets, skill, item);

        if (!skill.isWithoutAction()) {
            if (skill.isBad() && (skill.getTargetType() != TargetType.DOOR_TREASURE)) {
                caster.getAI().clientStartAutoAttack();
            }
        }

        caster.notifyQuestEventSkillFinished(skill, target);

        if(skill.useSoulShot()) {
            caster.consumeAndRechargeShots(ShotType.SOULSHOTS, targets.size());
        }
        if(skill.useSpiritShot()) {
            caster.consumeAndRechargeShots(ShotType.SPIRITSHOTS, targets.size());
        }
        return true;
    }

    private boolean consumeSkill(Creature caster, WorldObject target) {
        final StatusUpdate su = new StatusUpdate(caster);

        // Consume the required MP or stop casting if not enough.
        final double mpConsume = skill.getMpConsume() > 0 ? caster.getStats().getMpConsume(skill) : 0;
        if (mpConsume > 0) {
            if (mpConsume > caster.getCurrentMp()) {
                caster.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                return false;
            }

            caster.getStatus().reduceMp(mpConsume);
            su.addUpdate(StatusUpdateType.CUR_MP, (int) caster.getCurrentMp());
        }

        // Consume the required HP or stop casting if not enough.
        final double consumeHp = skill.getHpConsume();
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
            if ((skill.getMaxSoulConsumeCount() > 0) && !caster.getActingPlayer().decreaseSouls(skill.getMaxSoulConsumeCount())) {
                return false;
            }

            // Consume charges if necessary.
            if ((skill.getChargeConsumeCount() > 0) && !caster.getActingPlayer().decreaseCharges(skill.getChargeConsumeCount())) {
                return false;
            }
        }

        // Consume skill reduced item on success.
        if ((item != null) && (item.getAction() == ActionType.SKILL_REDUCE_ON_SKILL_SUCCESS) && (skill.getItemConsumeId() > 0) && (skill.getItemConsumeCount() > 0)) {
            return caster.destroyItem(skill.toString(), item.getObjectId(), skill.getItemConsumeCount(), target, true);
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

        final Creature caster = this.caster.get();
        final WorldObject target = this.target.get();
        if (caster == null) {
            return;
        }

        caster.removeSkillCaster(castingType);

        if (caster.isChanneling()) {
            caster.getSkillChannelizer().stopChanneling();
        }

        // If aborted, broadcast casting aborted.
        if (aborted) {
            caster.broadcastPacket(new MagicSkillCanceld(caster.getObjectId())); // broadcast packet to stop animations client-side
            caster.sendPacket(ActionFailed.of(castingType)); // send an "action failed" packet to the caster
        }

        // If there is a queued skill, launch it and wipe the queue.
        if (isPlayer(caster)) {
            final Player currPlayer = caster.getActingPlayer();
            final SkillUseHolder queuedSkill = currPlayer.getQueuedSkill();

            if (queuedSkill != null) {
                ThreadPool.execute(() ->
                {
                    currPlayer.setQueuedSkill(null, false, false);
                    currPlayer.useSkill(queuedSkill.getSkill(), queuedSkill.getItem(), queuedSkill.isCtrlPressed(), queuedSkill.isShiftPressed());
                });

                return;
            }
        }
        // Attack target after skill use.
        if ((skill.getNextAction() != NextActionType.NONE) && (caster.getAI().getNextIntention() == null))
        {
            if ((skill.getNextAction() == NextActionType.ATTACK) && (target != null) && (target != caster) && target.isAutoAttackable(caster))
            {
                caster.getAI().setIntention(AI_INTENTION_ATTACK, target);
            }
            else if ((skill.getNextAction() == NextActionType.CAST) && (target != null) && (target != caster) && target.isAutoAttackable(caster))
            {
                caster.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, false, false);
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
        if (skill.isChanneling()) {
            hitTime = (int) Math.max(skill.getHitTime() - cancelTime, 0);
            this.cancelTime = 2866;
        } else {
            hitTime = (int) Math.max((skill.getHitTime() / timeFactor) - cancelTime, 0);
            this.cancelTime = (int) cancelTime;
        }
        coolTime = (int) (skill.getCoolTime() / timeFactor); // cooltimeMillis / timeFactor
    }

    /**
     * @return the skill that is casting.
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * @return the creature casting the skill.
     */
    public Creature getCaster() {
        return caster.get();
    }

    /**
     * @return the target this skill is being cast on.
     */
    public WorldObject getTarget() {
        return target.get();
    }

    /**
     * @return {@code true} if casting can be aborted through regular means such as cast break while being attacked or while cancelling target, {@code false} otherwise.
     */
    public boolean canAbortCast() {
        return getCaster().getTarget() == null; // When targets are allocated, that means skill is already launched, therefore cannot be aborted.
    }

    public boolean isAnyNormalType() {
        return (castingType == SkillCastingType.NORMAL) || (castingType == SkillCastingType.NORMAL_SECOND);
    }

    @Override
    public String toString() {
        return super.toString() + " [caster: " + caster.get() + " skill: " + skill + " target: " + target.get() + " type: " + castingType + "]";
    }

    private void handleSkillFly(Creature creature, WorldObject target) {
        int x = 0;
        int y = 0;
        int z = 0;
        FlyToLocation.FlyType flyType = FlyToLocation.FlyType.CHARGE;
        switch (skill.getOperateType()) {
            case DA4:
            case DA5: {
                final double course = skill.getOperateType() == SkillOperateType.DA4 ? Math.toRadians(270) : Math.toRadians(90);
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
                x = creature.getX() + (int) (Math.cos(Math.PI + radian) * skill.getCastRange());
                y = creature.getY() + (int) (Math.sin(Math.PI + radian) * skill.getCastRange());
                z = creature.getZ();
                break;
            }
            case DA2:
            case DA1: {
                if (creature == target) {
                    final double course = Math.toRadians(180);
                    final double radian = Math.toRadians(convertHeadingToDegree(creature.getHeading()));
                    x = creature.getX() + (int) (Math.cos(Math.PI + radian + course) * skill.getCastRange());
                    y = creature.getY() + (int) (Math.sin(Math.PI + radian + course) * skill.getCastRange());
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
