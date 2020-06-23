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
package org.l2j.gameserver.model;


import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.model.skills.*;
import org.l2j.gameserver.network.serverpackets.AbnormalStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;
import org.l2j.gameserver.network.serverpackets.PartySpelled;
import org.l2j.gameserver.network.serverpackets.ShortBuffStatusUpdate;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadSpelledInfo;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * Effect lists.<br>
 * Holds all the buff infos that are affecting a creature.<br>
 * Manages the logic that controls whether a buff is added, remove, replaced or set inactive.<br>
 * Uses maps with skill ID as key and buff info DTO as value to avoid iterations.<br>
 * Uses Double-Checked Locking to avoid useless initialization and synchronization issues and overhead.<br>
 * Methods may resemble List interface, although it doesn't implement such interface.
 *
 * @author Zoey76
 */
public final class EffectList {
    private static final Logger LOGGER = LoggerFactory.getLogger(EffectList.class);
    /**
     * Count of specific types of buffs.
     */
    private final AtomicInteger buffCount = new AtomicInteger();
    private final AtomicInteger triggerBuffCount = new AtomicInteger();
    private final AtomicInteger danceCount = new AtomicInteger();
    private final AtomicInteger toggleCount = new AtomicInteger();
    private final AtomicInteger debuffCount = new AtomicInteger();
    /**
     * If {@code true} this effect list has buffs removed on any action.
     */
    private final AtomicInteger hasBuffsRemovedOnAnyAction = new AtomicInteger();
    /**
     * If {@code true} this effect list has buffs removed on damage.
     */
    private final AtomicInteger hasBuffsRemovedOnDamage = new AtomicInteger();
    /**
     * The owner of this effect list.
     */
    private final Creature owner;
    /**
     * Hidden buffs count, prevents iterations.
     */
    private final AtomicInteger hiddenBuffs = new AtomicInteger();
    /**
     * Queue containing all effects from buffs for this effect list.
     */
    private final Queue<BuffInfo> actives = new ConcurrentLinkedQueue<>();
    /**
     * List containing all passives for this effect list. They bypass most of the actions and they are not included in most operations.
     */
    private final Set<BuffInfo> passives = ConcurrentHashMap.newKeySet();
    /**
     * List containing all options for this effect list. They bypass most of the actions and they are not included in most operations.
     */
    private final Set<BuffInfo> options = ConcurrentHashMap.newKeySet();
    /**
     * Map containing the all stacked effect in progress for each {@code AbnormalType}.
     */
    private volatile Set<AbnormalType> stackedEffects = EnumSet.noneOf(AbnormalType.class);
    /**
     * Set containing all {@code AbnormalType}s that shouldn't be added to this creature effect list.
     */
    private final Set<AbnormalType> blockedAbnormalTypes = EnumSet.noneOf(AbnormalType.class);
    /**
     * Set containing all abnormal visual effects this creature currently displays.
     */
    private volatile Set<AbnormalVisualEffect> abnormalVisualEffects = EnumSet.noneOf(AbnormalVisualEffect.class);
    /**
     * Short buff skill ID.
     */
    private BuffInfo shortBuff = null;
    /**
     * Effect flags.
     */
    private long effectFlags;

    /**
     * Constructor for effect list.
     *
     * @param owner the creature that owns this effect list
     */
    public EffectList(Creature owner) {
        this.owner = owner;
    }

    /**
     * Gets passive effects.
     *
     * @return an unmodifiable set containing all passives.
     */
    public Set<BuffInfo> getPassives() {
        return Collections.unmodifiableSet(passives);
    }

    /**
     * Gets option effects.
     *
     * @return an unmodifiable set containing all options.
     */
    public Set<BuffInfo> getOptions() {
        return Collections.unmodifiableSet(options);
    }

    /**
     * Gets all the active effects on this effect list.
     *
     * @return an unmodifiable set containing all the active effects on this effect list
     */
    public Collection<BuffInfo> getEffects() {
        return Collections.unmodifiableCollection(actives);
    }

    /**
     * Gets all the active positive effects on this effect list.
     *
     * @return all the buffs on this effect list
     */
    public List<BuffInfo> getBuffs() {
        return actives.stream().filter(b -> b.getSkill().getBuffType().isBuff()).collect(Collectors.toList());
    }

    /**
     * Gets all the active positive effects on this effect list.
     * @return all the dances songs on this effect list
     */
    public List<BuffInfo> getDances() {
        return actives.stream().filter(b -> b.getSkill().getBuffType().isDance()).collect(Collectors.toList());
    }

        /**
         * Gets all the active negative effects on this effect list.
         *
         * @return all the debuffs on this effect list
         */
    public List<BuffInfo> getDebuffs() {
        return actives.stream().filter(b -> b.getSkill().isDebuff()).collect(Collectors.toList());
    }

    /**
     * Verifies if this effect list contains the given skill ID.<br>
     *
     * @param skillId the skill ID to verify
     * @return {@code true} if the skill ID is present in the effect list (includes active and passive effects), {@code false} otherwise
     */
    public boolean isAffectedBySkill(int skillId) {
        return (actives.stream().anyMatch(i -> i.getSkill().getId() == skillId)) || (passives.stream().anyMatch(i -> i.getSkill().getId() == skillId));
    }

    /**
     * Gets the first {@code BuffInfo} found in this effect list.
     *
     * @param skillId the skill ID
     * @return {@code BuffInfo} of the first active or passive effect found.
     */
    public BuffInfo getBuffInfoBySkillId(int skillId) {
        return Stream.concat(actives.stream(), passives.stream()).filter(b -> b.getSkill().getId() == skillId).findFirst().orElse(null);
    }

    public int remainTimeBySkillIdOrAbnormalType(int skillId, AbnormalType type) {
        return actives.stream().filter(b -> isSkillOrHasType(skillId, type, b)).mapToInt(BuffInfo::getTime).findFirst().orElse(0);
    }

    private boolean isSkillOrHasType(int skillId, AbnormalType type, BuffInfo buff) {
        return buff.getSkill().getId() == skillId || (type != AbnormalType.NONE && buff.getSkill().getAbnormalType() == type);
    }

    /**
     * Check if any active {@code BuffInfo} of this {@code AbnormalType} exists.<br>
     *
     * @param type the abnormal skill type
     * @return {@code true} if there is any {@code BuffInfo} matching the specified {@code AbnormalType}, {@code false} otherwise
     */
    public final boolean hasAbnormalType(AbnormalType type) {
        return stackedEffects.contains(type);
    }

    /**
     * Check if any active {@code BuffInfo} of this {@code AbnormalType} exists.<br>
     *
     * @param types the abnormal skill type
     * @return {@code true} if there is any {@code BuffInfo} matching one of the specified {@code AbnormalType}s, {@code false} otherwise
     */
    public boolean hasAbnormalType(Collection<AbnormalType> types) {
        return stackedEffects.stream().anyMatch(types::contains);
    }

    /**
     * @param type   the {@code AbnormalType} to match for.
     * @param filter any additional filters to match for once a {@code BuffInfo} of this {@code AbnormalType} is found.
     * @return {@code true} if there is any {@code BuffInfo} matching the specified {@code AbnormalType} and given filter, {@code false} otherwise
     */
    public boolean hasAbnormalType(AbnormalType type, Predicate<BuffInfo> filter) {
        return hasAbnormalType(type) && actives.stream().filter(i -> i.isAbnormalType(type)).anyMatch(filter);
    }

    /**
     * Gets the first {@code BuffInfo} found by the given {@code AbnormalType}.<br>
     * <font color="red">There are some cases where there are multiple {@code BuffInfo} per single {@code AbnormalType}</font>.
     *
     * @param type the abnormal skill type
     * @return the {@code BuffInfo} if it's present, {@code null} otherwise
     */
    public BuffInfo getFirstBuffInfoByAbnormalType(AbnormalType type) {
        return hasAbnormalType(type) ? actives.stream().filter(i -> i.isAbnormalType(type)).findFirst().orElse(null) : null;
    }

    /**
     * Adds {@code AbnormalType}s to the blocked buff slot set.
     *
     * @param blockedAbnormalTypes the blocked buff slot set to add
     */
    public void addBlockedAbnormalTypes(Set<AbnormalType> blockedAbnormalTypes) {
        this.blockedAbnormalTypes.addAll(blockedAbnormalTypes);
    }

    /**
     * Removes {@code AbnormalType}s from the blocked buff slot set.
     *
     * @param blockedBuffSlots the blocked buff slot set to remove
     * @return {@code true} if the blocked buff slots set has been modified, {@code false} otherwise
     */
    public boolean removeBlockedAbnormalTypes(Set<AbnormalType> blockedBuffSlots) {
        return blockedAbnormalTypes.removeAll(blockedBuffSlots);
    }

    /**
     * Gets all the blocked {@code AbnormalType}s for this creature effect list.
     *
     * @return the current blocked {@code AbnormalType}s set in unmodifiable view.
     */
    public Set<AbnormalType> getBlockedAbnormalTypes() {
        return Collections.unmodifiableSet(blockedAbnormalTypes);
    }

    /**
     * Sets the Short Buff data and sends an update if the effected is a player.
     *
     * @param info the {@code BuffInfo}
     */
    public void shortBuffStatusUpdate(BuffInfo info) {
        if (isPlayer(owner)) {
            shortBuff = info;
            if (info == null) {
                owner.sendPacket(ShortBuffStatusUpdate.RESET_SHORT_BUFF);
            } else {
                owner.sendPacket(new ShortBuffStatusUpdate(info.getSkill().getId(), info.getSkill().getLevel(), info.getSkill().getSubLevel(), info.getTime()));
            }
        }
    }

    /**
     * Gets the buffs count without including the hidden buffs (after getting an Herb buff).<br>
     * Prevents initialization.
     *
     * @return the number of buffs in this creature effect list
     */
    public int getBuffCount() {
        return !actives.isEmpty() ? (buffCount.get() - hiddenBuffs.get()) : 0;
    }

    /**
     * Gets the Songs/Dances count.<br>
     * Prevents initialization.
     *
     * @return the number of Songs/Dances in this creature effect list
     */
    public int getDanceCount() {
        return danceCount.get();
    }

    /**
     * Gets the triggered buffs count.<br>
     * Prevents initialization.
     *
     * @return the number of triggered buffs in this creature effect list
     */
    public int getTriggeredBuffCount() {
        return triggerBuffCount.get();
    }

    /**
     * Gets the toggled skills count.<br>
     * Prevents initialization.
     *
     * @return the number of toggle skills in this creature effect list
     */
    public int getToggleCount() {
        return toggleCount.get();
    }

    /**
     * Gets the debuff skills count.<br>
     * Prevents initialization.
     *
     * @return the number of debuff effects in this creature effect list
     */
    public int getDebuffCount() {
        return debuffCount.get();
    }

    /**
     * Gets the hidden buff count.
     *
     * @return the number of hidden buffs
     */
    public int getHiddenBuffsCount() {
        return hiddenBuffs.get();
    }

    /**
     * Exits all effects in this effect list.<br>
     * Stops all the effects, clear the effect lists and updates the effect flags and icons.
     *
     * @param broadcast {@code true} to broadcast update packets, {@code false} otherwise.
     */
    public void stopAllEffects(boolean broadcast) {
        stopEffects(b -> !b.getSkill().isIrreplacableBuff(), true, broadcast);
    }

    /**
     * Stops all effects in this effect list except those that last through death.
     */
    public void stopAllEffectsExceptThoseThatLastThroughDeath() {
        stopEffects(info -> !info.getSkill().isStayAfterDeath(), true, true);
    }

    /**
     * Exits all active, passive and option effects in this effect list without excluding anything,<br>
     * like necessary toggles, irreplacable buffs or effects that last through death.<br>
     * Stops all the effects, clear the effect lists and updates the effect flags and icons.
     * @param update set to true to update the effect flags and icons.
     * @param broadcast {@code true} to broadcast update packets, {@code false} otherwise.
     */
    public void stopAllEffectsWithoutExclusions(boolean update, boolean broadcast)
    {
        actives.forEach(this::remove);
        passives.forEach(this::remove);
        options.forEach(this::remove);

        // Update stats, effect flags and icons.
        if (update)
        {
            updateEffectList(broadcast);
        }
    }

    /**
     * Stops all active toggle skills.
     */
    public void stopAllToggles() {
        if (toggleCount.get() > 0) {
            // Ignore necessary toggles.
            stopEffects(b -> b.getSkill().isToggle() &&  !b.getSkill().isIrreplacableBuff(), true, true);
        }
    }

    public void stopAllTogglesOfGroup(int toggleGroup) {
        if (toggleCount.get() > 0) {
            stopEffects(b -> b.getSkill().isToggle(), true, true);
        }
    }

    /**
     * Stops all active dances/songs skills.
     *
     * @param update    set to true to update the effect flags and icons
     * @param broadcast {@code true} to broadcast update packets if updating, {@code false} otherwise.
     */
    public void stopAllPassives(boolean update, boolean broadcast) {
        if (!passives.isEmpty()) {
            passives.forEach(this::remove);
            // Update stats, effect flags and icons.
            if (update) {
                updateEffectList(broadcast);
            }
        }
    }

    /**
     * Stops all active dances/songs skills.
     *
     * @param update    set to true to update the effect flags and icons
     * @param broadcast {@code true} to broadcast update packets if updating, {@code false} otherwise.
     */
    public void stopAllOptions(boolean update, boolean broadcast) {
        if (!options.isEmpty()) {
            options.forEach(this::remove);
            // Update stats, effect flags and icons.
            if (update) {
                updateEffectList(broadcast);
            }
        }
    }

    /**
     * Exit all effects having a specified flag.<br>
     *
     * @param effectFlag the flag of the effect to stop
     */
    public void stopEffects(EffectFlag effectFlag) {
        if (isAffected(effectFlag)) {
            stopEffects(info -> info.getEffects().stream().anyMatch(effect -> (effect != null) && ((effect.getEffectFlags() & effectFlag.getMask()) != 0)), true, true);
        }
    }

    /**
     * Exits all effects created by a specific skill ID.<br>
     * Removes the effects from the effect list.<br>
     * Removes the stats from the creature.<br>
     * Updates the effect flags and icons.<br>
     * Presents overload:<br>
     * {@link #stopSkillEffects(boolean, Skill)}<br>
     *
     * @param removed {@code true} if the effect is removed, {@code false} otherwise
     * @param skillId the skill ID
     */
    public void stopSkillEffects(boolean removed, int skillId) {
        final BuffInfo info = getBuffInfoBySkillId(skillId);
        if (info != null) {
            remove(info, removed, true, true);
        }
    }

    /**
     * Exits all effects created by a specific skill.<br>
     * Removes the effects from the effect list.<br>
     * Removes the stats from the creature.<br>
     * Updates the effect flags and icons.<br>
     * Presents overload:<br>
     * {@link #stopSkillEffects(boolean, int)}<br>
     *
     * @param removed {@code true} if the effect is removed, {@code false} otherwise
     * @param skill   the skill
     */
    public void stopSkillEffects(boolean removed, Skill skill) {
        stopSkillEffects(removed, skill.getId());
    }

    /**
     * Exits all effects created by a specific skill {@code AbnormalType}.<br>
     * <font color="red">This function should not be used recursively, because it updates on every execute.</font>
     *
     * @param type the skill {@code AbnormalType}
     * @return {@code true} if there was any {@code BuffInfo} with the given {@code AbnormalType}, {@code false} otherwise
     */
    public boolean stopEffects(AbnormalType type) {
        if (hasAbnormalType(type)) {
            stopEffects(i -> i.isAbnormalType(type), true, true);
            return true;
        }

        return false;
    }

    /**
     * Exits all effects created by a specific skill {@code AbnormalType}s.<br>
     *
     * @param types the skill {@code AbnormalType}s to be checked and removed.
     * @return {@code true} if there was any {@code BuffInfo} with one of the given {@code AbnormalType}s, {@code false} otherwise
     */
    public boolean stopEffects(Collection<AbnormalType> types) {
        if (hasAbnormalType(types)) {
            stopEffects(i -> types.contains(i.getSkill().getAbnormalType()), true, true);
            return true;
        }

        return false;
    }

    /**
     * Exits all effects matched by a specific filter.<br>
     *
     * @param filter    any filter to apply when selecting which {@code BuffInfo}s to be removed.
     * @param update    update effect flags and icons after the operation finishes.
     * @param broadcast {@code true} to broadcast update packets if updating, {@code false} otherwise.
     */
    public void stopEffects(Predicate<BuffInfo> filter, boolean update, boolean broadcast) {
        if (!actives.isEmpty()) {
            actives.stream().filter(filter).forEach(this::remove);

            // Update stats, effect flags and icons.
            if (update) {
                updateEffectList(broadcast);
            }
        }
    }

    /**
     * Exits all buffs effects of the skills with "removedOnAnyAction" set.<br>
     * Called on any action except movement (attack, cast).
     */
    public void stopEffectsOnAction() {
        if (hasBuffsRemovedOnAnyAction.get() > 0) {
            stopEffects(info -> info.getSkill().isRemovedOnAnyActionExceptMove(), true, true);
        }
    }

    public void stopEffectsOnDamage() {
        if (hasBuffsRemovedOnDamage.get() > 0) {
            stopEffects(info -> info.getSkill().isRemovedOnDamage(), true, true);
        }
    }

    /**
     * Checks if a given effect limitation is exceeded.
     *
     * @param buffTypes the {@code SkillBuffType} of the skill.
     * @return {@code true} if the current effect count for any of the given types is greater than the limit, {@code false} otherwise.
     */
    private boolean isLimitExceeded(SkillBuffType... buffTypes) {
        for (SkillBuffType buffType : buffTypes) {
            switch (buffType) {
                case TRIGGER: {
                    if (triggerBuffCount.get() > Config.TRIGGERED_BUFFS_MAX_AMOUNT) {
                        return true;
                    }
                }
                case DANCE: {
                    if (danceCount.get() > Config.DANCES_MAX_AMOUNT) {
                        return true;
                    }
                }
                // case TOGGLE: Do toggles have limit?
                case DEBUFF: {
                    if (debuffCount.get() > 24) {
                        return true;
                    }
                }
                case BUFF: {
                    if (getBuffCount() > owner.getStats().getMaxBuffCount()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @param info     the {@code BuffInfo} whose buff category will be increased/decreased in count.
     * @param increase {@code true} to increase the category count of this {@code BuffInfo}, {@code false} to decrease.
     * @return the new count of the given {@code BuffInfo}'s category.
     */
    private int increaseDecreaseCount(BuffInfo info, boolean increase) {
        // If it's a hidden buff, manage hidden buff count.
        if (!info.isInUse()) {
            if (increase) {
                hiddenBuffs.incrementAndGet();
            } else {
                hiddenBuffs.decrementAndGet();
            }
        }

        // Update flag for skills being removed on action or damage.
        if (info.getSkill().isRemovedOnAnyActionExceptMove()) {
            if (increase) {
                hasBuffsRemovedOnAnyAction.incrementAndGet();
            } else {
                hasBuffsRemovedOnAnyAction.decrementAndGet();
            }
        }
        if (info.getSkill().isRemovedOnDamage()) {
            if (increase) {
                hasBuffsRemovedOnDamage.incrementAndGet();
            } else {
                hasBuffsRemovedOnDamage.decrementAndGet();
            }
        }

        // Increase specific buff count
        switch (info.getSkill().getBuffType()) {
            case TRIGGER: {
                return increase ? triggerBuffCount.incrementAndGet() : triggerBuffCount.decrementAndGet();
            }
            case DANCE: {
                return increase ? danceCount.incrementAndGet() : danceCount.decrementAndGet();
            }
            case TOGGLE: {
                return increase ? toggleCount.incrementAndGet() : toggleCount.decrementAndGet();
            }
            case DEBUFF: {
                return increase ? debuffCount.incrementAndGet() : debuffCount.decrementAndGet();
            }
            case BUFF: {
                return increase ? buffCount.incrementAndGet() : buffCount.decrementAndGet();
            }
        }

        return 0;
    }

    /**
     * Removes a set of effects from this effect list.<br>
     * <font color="red">Does NOT update effect icons and flags. </font>
     *
     * @param info the effects to remove
     */
    private void remove(BuffInfo info) {
        remove(info, true, false, false);
    }

    /**
     * Removes a set of effects from this effect list.
     *
     * @param info      the effects to remove
     * @param removed   {@code true} if the effect is removed, {@code false} otherwise
     * @param update    {@code true} if effect flags and icons should be updated after this removal, {@code false} otherwise.
     * @param broadcast {@code true} to broadcast update packets if updating, {@code false} otherwise.
     */
    public void remove(BuffInfo info, boolean removed, boolean update, boolean broadcast) {
        if (info == null) {
            return;
        }

        if (info.getOption() != null) {
            // Remove separately if its an option.
            removeOption(info, removed);
        } else if (info.getSkill().isPassive()) {
            // Remove Passive effect.
            removePassive(info, removed);
        } else {
            // Remove active effect.
            removeActive(info, removed);
            if (isNpc(owner)) // Fix for all NPC debuff animations removed.
            {
                updateEffectList(broadcast);
            }
        }

        // Update stats, effect flags and icons.
        if (update) {
            updateEffectList(broadcast);
        }
    }

    /**
     * @param info
     * @param removed
     */
    private synchronized void removeActive(BuffInfo info, boolean removed) {
        if (!actives.isEmpty()) {
            // Removes the buff from the given effect list.
            actives.remove(info);

            // Remove short buff.
            if (info == shortBuff) {
                shortBuffStatusUpdate(null);
            }

            // Stop the buff effects.
            info.stopAllEffects(removed);

            // Decrease specific buff count
            increaseDecreaseCount(info, false);

            info.getSkill().applyEffectScope(EffectScope.END, info, true, false);
        }
    }

    private void removePassive(BuffInfo info, boolean removed) {
        if (!passives.isEmpty()) {
            passives.remove(info);
            info.stopAllEffects(removed);
        }
    }

    private void removeOption(BuffInfo info, boolean removed) {
        if (!options.isEmpty()) {
            options.remove(info);
            info.stopAllEffects(removed);
        }
    }

    /**
     * Adds a set of effects to this effect list.
     *
     * @param info the {@code BuffInfo}
     */
    public void add(BuffInfo info) {
        if (info == null) {
            return;
        }

        // Prevent adding and initializing buffs/effects on dead creatures.
        if (info.getEffected().isDead()) {
            return;
        }

        if (info.getSkill() == null) {
            // Only options are without skills.
            addOption(info);
        } else if (info.getSkill().isPassive()) {
            // Passive effects are treated specially
            addPassive(info);
        } else {
            // Add active effect
            addActive(info);
        }

        // Update stats, effect flags and icons.
        updateEffectList(true);
    }

    private synchronized void addActive(BuffInfo info) {
        final Skill skill = info.getSkill();

        // Cannot add active buff to dead creature. Even in retail if you are dead with Lv. 3 Shillien's Breath, it will disappear instead of going 1 level down.
        if (info.getEffected().isDead()) {
            return;
        }

        if (blockedAbnormalTypes.contains(skill.getAbnormalType())) {
            return;
        }

        // Fix for stacking trigger skills
        if (skill.isTriggeredSkill()) {
            final BuffInfo triggerInfo = info.getEffected().getEffectList().getBuffInfoBySkillId(skill.getId());
            if (triggerInfo != null) {
                if (triggerInfo.getSkill().getLevel() >= skill.getLevel()) {
                    return;
                }
            }
        }

        if (info.getEffector() != null) {
            // Check for debuffs against target.
            if ((info.getEffector() != info.getEffected()) && skill.isBad()) {
                // Check if effected is debuff blocked.
                if ((info.getEffected().isDebuffBlocked() || (info.getEffector().isGM() && !info.getEffector().getAccessLevel().canGiveDamage()))) {
                    return;
                }

                if (isPlayer(info.getEffector()) && isPlayer(info.getEffected())  && info.getEffected().isAffected(EffectFlag.DUELIST_FURY) && !info.getEffector().isAffected(EffectFlag.DUELIST_FURY)) {
                    return;
                }
            }

            // Check if buff skills are blocked.
            if (info.getEffected().isBuffBlocked() && !skill.isBad()) {
                return;
            }
        }

        // Manage effect stacking.
        if (hasAbnormalType(skill.getAbnormalType())) {
            for (BuffInfo existingInfo : actives) {
                final Skill existingSkill = existingInfo.getSkill();
                // Check if existing effect should be removed due to stack.
                // Effects with no abnormal don't stack if their ID is the same. Effects of the same abnormal type don't stack.
                if ((skill.getAbnormalType().isNone() && (existingSkill.getId() == skill.getId())) || (!skill.getAbnormalType().isNone() && (existingSkill.getAbnormalType() == skill.getAbnormalType()))) {
                    // Check if there is subordination abnormal. Skills with subordination abnormal stack with each other, unless the caster is the same.
                    if (!skill.getSubordinationAbnormalType().isNone() && (skill.getSubordinationAbnormalType() == existingSkill.getSubordinationAbnormalType())) {
                        if ((info.getEffectorObjectId() == 0) || (existingInfo.getEffectorObjectId() == 0) || (info.getEffectorObjectId() != existingInfo.getEffectorObjectId())) {
                            continue;
                        }
                    }

                    // The effect we are adding overrides the existing effect. Delete or disable the existing effect.
                    if (skill.getAbnormalLvl() >= existingSkill.getAbnormalLvl()) {
                        // If it is an herb, set as not in use the lesser buff, unless it is the same skill.
                        if ((skill.isAbnormalInstant() || existingSkill.isIrreplacableBuff()) && (skill.getId() != existingSkill.getId())) {
                            existingInfo.setInUse(false);
                            hiddenBuffs.incrementAndGet();
                        } else {
                            // Remove effect that gets overridden.
                            remove(existingInfo);
                        }
                    } else if (skill.isIrreplacableBuff()) // The effect we try to add should be hidden.
                    {
                        info.setInUse(false);
                    } else // The effect we try to add should be overridden.
                    {
                        return;
                    }
                }
            }
        }

        // Increase buff count.
        increaseDecreaseCount(info, true);

        // Check if any effect limit is exceeded.
        if (isLimitExceeded(SkillBuffType.values())) {
            // Check for each category.
            for (BuffInfo existingInfo : actives) {
                if (existingInfo.isInUse() && !skill.is7Signs() && isLimitExceeded(existingInfo.getSkill().getBuffType())) {
                    remove(existingInfo);
                }

                // Break further loops if there is no any other limit exceeding.
                if (!isLimitExceeded(SkillBuffType.values())) {
                    break;
                }
            }
        }

        // After removing old buff (same ID) or stacked buff (same abnormal type),
        // Add the buff to the end of the effect list.
        actives.add(info);
        // Initialize effects.
        info.initializeEffects();
    }

    private void addPassive(BuffInfo info) {
        final Skill skill = info.getSkill();

        // Passive effects don't need stack type!
        if (!skill.getAbnormalType().isNone()) {
            LOGGER.warn("Passive {} with abnormal type: {}!", skill, skill.getAbnormalType());
        }

        // Check for passive skill conditions.
        if (!skill.checkCondition(info.getEffector(), info.getEffected())) {
            return;
        }

        // Remove previous passives of this id.
        passives.stream().filter(Objects::nonNull).filter(b -> b.getSkill().getId() == skill.getId()).forEach(b ->
        {
            b.setInUse(false);
            passives.remove(b);
        });

        passives.add(info);

        // Initialize effects.
        info.initializeEffects();
    }

    private void addOption(BuffInfo info) {
        if (info.getOption() != null) {
            // Remove previous options of this id.
            options.stream().filter(Objects::nonNull).filter(b -> b.getOption().getId() == info.getOption().getId()).forEach(b ->
            {
                b.setInUse(false);
                options.remove(b);
            });

            options.add(info);

            // Initialize effects.
            info.initializeEffects();
        }
    }

    /**
     * Update effect icons.<br>
     * Prevents initialization.
     *
     * @param partyOnly {@code true} only party icons need to be updated.
     */
    public void updateEffectIcons(boolean partyOnly) {
        final Player player = owner.getActingPlayer();
        if (player != null) {
            final Party party = player.getParty();
            final Optional<AbnormalStatusUpdate> asu = (isPlayer(owner) && !partyOnly) ? Optional.of(new AbnormalStatusUpdate()) : Optional.empty();
            final Optional<PartySpelled> ps = ((party != null) || isSummon(owner)) ? Optional.of(new PartySpelled(owner)) : Optional.empty();
            final Optional<ExOlympiadSpelledInfo> os = (player.isInOlympiadMode() && player.isOlympiadStart()) ? Optional.of(new ExOlympiadSpelledInfo(player)) : Optional.empty();

            if (!actives.isEmpty()) {
                //@formatter:off
                actives.stream()
                        .filter(Objects::nonNull)
                        .filter(BuffInfo::isInUse)
                        .forEach(info ->
                        {
                            if (info.getSkill().isHealingPotionSkill()) {
                                shortBuffStatusUpdate(info);
                            } else {
                                asu.ifPresent(a -> a.addSkill(info));
                                ps.filter(p -> !info.getSkill().isToggle()).ifPresent(p -> p.addSkill(info));
                                os.ifPresent(o -> o.addSkill(info));
                            }
                        });
                //@formatter:on
            }

            // Send icon update for player buff bar.
            asu.ifPresent(owner::sendPacket);

            // Player or summon is in party. Broadcast packet to everyone in the party.
            if (party != null) {
                ps.ifPresent(party::broadcastPacket);
            } else // Not in party, then its a summon info for its owner.
            {
                ps.ifPresent(player::sendPacket);
            }

            // Send icon update to all olympiad observers.
            if (os.isPresent()) {
                final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
                if ((game != null) && game.isBattleStarted()) {
                    os.ifPresent(game.getStadium()::broadcastPacketToObservers);
                }
            }
        }

        // Update effect icons for everyone targeting this owner.
        final ExAbnormalStatusUpdateFromTarget upd = new ExAbnormalStatusUpdateFromTarget(owner);

        // @formatter:off
        owner.getStatus().getStatusListener().stream()
                .filter(GameUtils::isPlayer)
                .map(Creature::getActingPlayer)
                .forEach(upd::sendTo);
        // @formatter:on

        if (isPlayer(owner) && (owner.getTarget() == owner)) {
            owner.sendPacket(upd);
        }
    }

    /**
     * Gets the currently applied abnormal visual effects.
     *
     * @return the abnormal visual effects
     */
    public Set<AbnormalVisualEffect> getCurrentAbnormalVisualEffects() {
        return abnormalVisualEffects;
    }

    /**
     * Checks if the creature has the abnormal visual effect.
     *
     * @param ave the abnormal visual effect
     * @return {@code true} if the creature has the abnormal visual effect, {@code false} otherwise
     */
    public boolean hasAbnormalVisualEffect(AbnormalVisualEffect ave) {
        return abnormalVisualEffects.contains(ave);
    }

    /**
     * Adds the abnormal visual and sends packet for updating them in client.
     *
     * @param aves the abnormal visual effects
     */
    public final void startAbnormalVisualEffect(AbnormalVisualEffect... aves) {
        abnormalVisualEffects.addAll(Arrays.asList(aves));
        owner.updateAbnormalVisualEffects();
    }

    /**
     * Removes the abnormal visual and sends packet for updating them in client.
     *
     * @param aves the abnormal visual effects
     */
    public final void stopAbnormalVisualEffect(AbnormalVisualEffect... aves) {
        for (AbnormalVisualEffect ave : aves) {
            abnormalVisualEffects.remove(ave);
        }
        owner.updateAbnormalVisualEffects();
    }

    /**
     * Wrapper to update abnormal icons and effect flags.
     *
     * @param broadcast {@code true} sends update packets to observing players, {@code false} doesn't send any packets.
     */
    private void updateEffectList(boolean broadcast) {
        // Create new empty flags.
        long flags = 0;
        final Set<AbnormalType> abnormalTypeFlags = EnumSet.noneOf(AbnormalType.class);
        final Set<AbnormalVisualEffect> abnormalVisualEffectFlags = EnumSet.noneOf(AbnormalVisualEffect.class);
        final Set<BuffInfo> unhideBuffs = new HashSet<>();

        // Recalculate new flags
        for (BuffInfo info : actives) {
            if (info != null) {
                final Skill skill = info.getSkill();

                // Handle hidden buffs. Check if there was such abnormal before so we can continue.
                if ((hiddenBuffs.get() > 0) && stackedEffects.contains(skill.getAbnormalType())) {
                    // If incoming buff isnt hidden, remove any hidden buffs with its abnormal type.
                    if (info.isInUse()) {
                        unhideBuffs.removeIf(b -> b.isAbnormalType(skill.getAbnormalType()));
                    }
                    // If this incoming buff is hidden and its first of its abnormal, or it removes any previous hidden buff with the same or lower abnormal level and add this instead.
                    else if (!abnormalTypeFlags.contains(skill.getAbnormalType()) || unhideBuffs.removeIf(b -> (b.isAbnormalType(skill.getAbnormalType())) && (b.getSkill().getAbnormalLvl() <= skill.getAbnormalLvl()))) {
                        unhideBuffs.add(info);
                    }
                }

                // Add the EffectType flag.
                for (AbstractEffect e : info.getEffects()) {
                    flags |= e.getEffectFlags();
                }

                // Add the AbnormalType flag.
                abnormalTypeFlags.add(skill.getAbnormalType());

                // Add AbnormalVisualEffect flag.
                if (skill.hasAbnormalVisualEffect()) {
                    var visual = skill.getAbnormalVisualEffect();
                    abnormalVisualEffectFlags.addAll(visual); // TODO review : why two lists ?
                    abnormalVisualEffects.addAll(visual);
                    if (broadcast) {
                        owner.updateAbnormalVisualEffects();
                    }
                }
            }
        }
        // Add passive effect flags.
        for (BuffInfo info : passives)
        {
            if (info != null)
            {
                // Add the EffectType flag.
                for (AbstractEffect e : info.getEffects())
                {
                    flags |= e.getEffectFlags();
                }
            }
        }

        // Replace the old flags with the new flags.
        effectFlags = flags;
        stackedEffects = abnormalTypeFlags;

        // Unhide the selected buffs.
        unhideBuffs.forEach(b ->
        {
            b.setInUse(true);
            hiddenBuffs.decrementAndGet();
        });

        // Recalculate all stats
        owner.getStats().recalculateStats(broadcast);

        if (broadcast) {
            // Check if there is change in AbnormalVisualEffect
            if (!abnormalVisualEffectFlags.containsAll(abnormalVisualEffects)) {
                abnormalVisualEffects = abnormalVisualEffectFlags;
                owner.updateAbnormalVisualEffects();
            }

            // Send updates to the client
            updateEffectIcons(false);
        }
    }

    /**
     * Check if target is affected with special buff
     *
     * @param flag of special buff
     * @return boolean true if affected
     */
    public boolean isAffected(EffectFlag flag) {
        return (effectFlags & flag.getMask()) != 0;
    }
}
