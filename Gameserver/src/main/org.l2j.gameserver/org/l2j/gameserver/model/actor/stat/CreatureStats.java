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
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.EffectList;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.model.stats.*;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.isSummon;

public class CreatureStats {
    private final Creature creature;
    private final Map<Stat, Double> statsAdd = new EnumMap<>(Stat.class);
    private final Map<Stat, Double> statsMul = new EnumMap<>(Stat.class);
    private final Map<Stat, Map<MoveType, Double>> _moveTypeStats = new ConcurrentHashMap<>();
    private final Map<SkillType, Double> reuseStat = Collections.synchronizedMap(new EnumMap<>(SkillType.class));
    private final Map<SkillType, Double> mpConsumeStat = Collections.synchronizedMap(new EnumMap<>(SkillType.class));
    private final Map<SkillType, Stack<Double>> skillEvasionStat = Collections.synchronizedMap(new EnumMap<>(SkillType.class));
    private final Map<Stat, Map<Position, Double>> _positionStats = new ConcurrentHashMap<>();
    private final Deque<StatsHolder> _additionalAdd = new ConcurrentLinkedDeque<>();
    private final Deque<StatsHolder> _additionalMul = new ConcurrentLinkedDeque<>();
    private final Map<Stat, Double> _fixedValue = new ConcurrentHashMap<>();

    private final float[] _attackTraitValues = new float[TraitType.values().length];
    private final float[] _defenceTraitValues = new float[TraitType.values().length];
    private final Set<TraitType> _attackTraits = EnumSet.noneOf(TraitType.class);
    private final Set<TraitType> _defenceTraits = EnumSet.noneOf(TraitType.class);
    private final Set<TraitType> _invulnerableTraits = EnumSet.noneOf(TraitType.class);

    private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
    private long _exp = 0;
    private long _sp = 0;
    private byte _level = 1;
    /**
     * Creature's maximum buff count.
     */
    private int _maxBuffCount = Config.BUFFS_MAX_AMOUNT;
    private double _vampiricSum = 0;
    /**
     * Values to be recalculated after every stat update
     */
    private double _attackSpeedMultiplier = 1;
    private double _mAttackSpeedMultiplier = 1;

    public CreatureStats(Creature activeChar) {
        creature = activeChar;
        for (int i = 0; i < TraitType.values().length; i++)
        {
            _attackTraitValues[i] = 1;
            _defenceTraitValues[i] = 0;
        }
    }

    /**
     * @return the Accuracy (base+modifier) of the Creature in function of the Weapon Expertise Penalty.
     */
    public int getAccuracy() {
        return (int) getValue(Stat.ACCURACY);
    }

    /**
     * @return the Magic Accuracy (base+modifier) of the Creature
     */
    public int getMagicAccuracy() {
        return (int) getValue(Stat.ACCURACY_MAGIC);
    }

    public Creature getCreature() {
        return creature;
    }

    /**
     * @return the Attack Speed multiplier (base+modifier) of the Creature to get proper animations.
     */
    public final double getAttackSpeedMultiplier() {
        return _attackSpeedMultiplier;
    }

    public final double getMAttackSpeedMultiplier() {
        return _mAttackSpeedMultiplier;
    }

    /**
     * @return the CON of the Creature (base+modifier).
     */
    public final int getCON() {
        return (int) getValue(Stat.STAT_CON);
    }

    /**
     * @param init
     * @return the Critical Damage rate (base+modifier) of the Creature.
     */
    public final double getCriticalDmg(double init) {
        return getValue(Stat.CRITICAL_DAMAGE, init);
    }

    /**
     * @return the Critical Hit rate (base+modifier) of the Creature.
     */
    public int getCriticalHit() {
        return (int) getValue(Stat.CRITICAL_RATE);
    }

    /**
     * @return the DEX of the Creature (base+modifier).
     */
    public final int getDEX() {
        return (int) getValue(Stat.STAT_DEX);
    }

    /**
     * @return the Attack Evasion rate (base+modifier) of the Creature.
     */
    public int getEvasionRate() {
        return (int) getValue(Stat.EVASION_RATE);
    }

    /**
     * @return the Attack Evasion rate (base+modifier) of the Creature.
     */
    public int getMagicEvasionRate() {
        return (int) getValue(Stat.MAGIC_EVASION_RATE);
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(long value) {
        _exp = value;
    }

    /**
     * @return the INT of the Creature (base+modifier).
     */
    public int getINT() {
        return (int) getValue(Stat.STAT_INT);
    }

    public byte getLevel() {
        return _level;
    }

    public void setLevel(byte value) {
        _level = value;
    }

    /**
     * @param skill
     * @return the Magical Attack range (base+modifier) of the Creature.
     */
    public final int getMagicalAttackRange(Skill skill) {
        if (skill != null) {
            return (int) getValue(Stat.MAGIC_ATTACK_RANGE, skill.getCastRange());
        }

        return creature.getTemplate().getBaseAttackRange();
    }

    public int getMaxCp() {
        return (int) getValue(Stat.MAX_CP);
    }

    public int getMaxRecoverableCp() {
        return (int) getValue(Stat.MAX_RECOVERABLE_CP, getMaxCp());
    }

    public int getMaxHp() {
        return (int) getValue(Stat.MAX_HP);
    }

    public int getMaxRecoverableHp() {
        return (int) getValue(Stat.MAX_RECOVERABLE_HP, getMaxHp());
    }

    public int getMaxMp() {
        return (int) getValue(Stat.MAX_MP);
    }

    public int getMaxRecoverableMp() {
        return (int) getValue(Stat.MAX_RECOVERABLE_MP, getMaxMp());
    }

    /**
     * Return the MAtk (base+modifier) of the Creature.<br>
     * <B><U>Example of use</U>: Calculate Magic damage
     *
     * @return
     */
    public int getMAtk() {
        return (int) getValue(Stat.MAGIC_ATTACK);
    }

    /**
     * @return the MAtk Speed (base+modifier) of the Creature in function of the Armour Expertise Penalty.
     */
    public int getMAtkSpd() {
        return (int) getValue(Stat.MAGIC_ATTACK_SPEED);
    }

    /**
     * @return the Magic Critical Hit rate (base+modifier) of the Creature.
     */
    public final int getMCriticalHit() {
        return (int) getValue(Stat.MAGIC_CRITICAL_RATE);
    }

    /**
     * <B><U>Example of use </U>: Calculate Magic damage.
     *
     * @return the MDef (base+modifier) of the Creature against a skill in function of abnormal effects in progress.
     */
    public int getMDef() {
        return (int) getValue(Stat.MAGICAL_DEFENCE);
    }

    /**
     * @return the MEN of the Creature (base+modifier).
     */
    public final int getMEN() {
        return (int) getValue(Stat.STAT_MEN);
    }

    public double getMovementSpeedMultiplier() {
        double baseSpeed;
        if (creature.isInsideZone(ZoneType.WATER)) {
            baseSpeed = creature.getTemplate().getBaseValue(creature.isRunning() ? Stat.SWIM_RUN_SPEED : Stat.SWIM_WALK_SPEED, 1);
        } else {
            baseSpeed = creature.getTemplate().getBaseValue(creature.isRunning() ? Stat.RUN_SPEED : Stat.WALK_SPEED, 1);
        }
        return getMoveSpeed() * (1. / baseSpeed);
    }

    /**
     * @return the RunSpeed (base+modifier) of the Creature in function of the Armour Expertise Penalty.
     */
    public double getRunSpeed() {
        return getValue(creature.isInsideZone(ZoneType.WATER) ? Stat.SWIM_RUN_SPEED : Stat.RUN_SPEED);
    }

    /**
     * @return the WalkSpeed (base+modifier) of the Creature.
     */
    public double getWalkSpeed() {
        return getValue(creature.isInsideZone(ZoneType.WATER) ? Stat.SWIM_WALK_SPEED : Stat.WALK_SPEED);
    }

    /**
     * @return the SwimRunSpeed (base+modifier) of the Creature.
     */
    public double getSwimRunSpeed() {
        return getValue(Stat.SWIM_RUN_SPEED);
    }

    /**
     * @return the SwimWalkSpeed (base+modifier) of the Creature.
     */
    public double getSwimWalkSpeed() {
        return getValue(Stat.SWIM_WALK_SPEED);
    }

    /**
     * @return the RunSpeed (base+modifier) or WalkSpeed (base+modifier) of the Creature in function of the movement type.
     */
    public double getMoveSpeed() {
        if (creature.isInsideZone(ZoneType.WATER)) {
            return creature.isRunning() ? getSwimRunSpeed() : getSwimWalkSpeed();
        }
        return creature.isRunning() ? getRunSpeed() : getWalkSpeed();
    }

    /**
     * @return the PAtk (base+modifier) of the Creature.
     */
    public int getPAtk() {
        return (int) getValue(Stat.PHYSICAL_ATTACK);
    }

    /**
     * @return the PAtk Speed (base+modifier) of the Creature in function of the Armour Expertise Penalty.
     */
    public int getPAtkSpd() {
        return (int) getValue(Stat.PHYSICAL_ATTACK_SPEED);
    }

    /**
     * @return the PDef (base+modifier) of the Creature.
     */
    public int getPDef() {
        return (int) getValue(Stat.PHYSICAL_DEFENCE);
    }

    /**
     * @return the Physical Attack range (base+modifier) of the Creature.
     */
    public final int getPhysicalAttackRange() {
        return (int) getValue(Stat.PHYSICAL_ATTACK_RANGE);
    }

    public int getPhysicalAttackRadius() {
        return 40;
    }

    public int getPhysicalAttackAngle() {
        return 240; // 360 - 120
    }

    /**
     * @return the weapon reuse modifier.
     */
    public final double getWeaponReuseModifier() {
        return getValue(Stat.ATK_REUSE, 1);
    }

    /**
     * @return the ShieldDef rate (base+modifier) of the Creature.
     */
    public final int getShldDef() {
        return (int) getValue(Stat.SHIELD_DEFENCE);
    }

    public long getSp() {
        return _sp;
    }

    public void setSp(long value) {
        _sp = value;
    }

    /**
     * @return the STR of the Creature (base+modifier).
     */
    public final int getSTR() {
        return (int) getValue(Stat.STAT_STR);
    }

    /**
     * @return the WIT of the Creature (base+modifier).
     */
    public final int getWIT() {
        return (int) getValue(Stat.STAT_WIT);
    }

    /**
     * @param skill
     * @return the mpConsume.
     */
    public final int getMpConsume(Skill skill) {
        if (skill == null) {
            return 1;
        }
        double mpConsume = skill.getMpConsume();
        final double nextDanceMpCost = Math.ceil(skill.getMpConsume() / 2.);
        if (skill.isDance()) {
            if (Config.DANCE_CONSUME_ADDITIONAL_MP && (creature != null) && (creature.getDanceCount() > 0)) {
                mpConsume += creature.getDanceCount() * nextDanceMpCost;
            }
        }

        return (int) (mpConsume * getMpConsumeTypeValue(skill.getSkillType()));
    }

    /**
     * @param skill
     * @return the mpInitialConsume.
     */
    public final int getMpInitialConsume(Skill skill) {
        if (skill == null) {
            return 1;
        }

        return skill.getMpInitialConsume();
    }

    public AttributeType getAttackElement() {
        final Item weaponInstance = creature.getActiveWeaponInstance();
        // 1st order - weapon element
        if ((weaponInstance != null) && (weaponInstance.getAttackAttributeType() != AttributeType.NONE)) {
            return weaponInstance.getAttackAttributeType();
        }

        // temp fix starts
        int tempVal = 0;
        final int stats[] =
                {
                        getAttackElementValue(AttributeType.FIRE),
                        getAttackElementValue(AttributeType.WATER),
                        getAttackElementValue(AttributeType.WIND),
                        getAttackElementValue(AttributeType.EARTH),
                        getAttackElementValue(AttributeType.HOLY),
                        getAttackElementValue(AttributeType.DARK)
                };

        AttributeType returnVal = AttributeType.NONE;

        for (byte x = 0; x < stats.length; x++) {
            if (stats[x] > tempVal) {
                returnVal = AttributeType.findByClientId(x);
                tempVal = stats[x];
            }
        }

        return returnVal;
    }

    public int getAttackElementValue(AttributeType attackAttribute) {
        switch (attackAttribute) {
            case FIRE: {
                return (int) getValue(Stat.FIRE_POWER);
            }
            case WATER: {
                return (int) getValue(Stat.WATER_POWER);
            }
            case WIND: {
                return (int) getValue(Stat.WIND_POWER);
            }
            case EARTH: {
                return (int) getValue(Stat.EARTH_POWER);
            }
            case HOLY: {
                return (int) getValue(Stat.HOLY_POWER);
            }
            case DARK: {
                return (int) getValue(Stat.DARK_POWER);
            }
            default: {
                return 0;
            }
        }
    }

    public int getDefenseElementValue(AttributeType defenseAttribute) {
        switch (defenseAttribute) {
            case FIRE: {
                return (int) getValue(Stat.FIRE_RES);
            }
            case WATER: {
                return (int) getValue(Stat.WATER_RES);
            }
            case WIND: {
                return (int) getValue(Stat.WIND_RES);
            }
            case EARTH: {
                return (int) getValue(Stat.EARTH_RES);
            }
            case HOLY: {
                return (int) getValue(Stat.HOLY_RES);
            }
            case DARK: {
                return (int) getValue(Stat.DARK_RES);
            }
            default: {
                return (int) getValue(Stat.BASE_ATTRIBUTE_RES);
            }
        }
    }

    public void mergeAttackTrait(TraitType traitType, float value)
    {
        _lock.readLock().lock();
        try
        {
            _attackTraitValues[traitType.ordinal()] += value;
            _attackTraits.add(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public void removeAttackTrait(TraitType traitType, float value)
    {
        _lock.readLock().lock();
        try
        {
            _attackTraitValues[traitType.ordinal()] -= value;
            if (_attackTraitValues[traitType.ordinal()] == 1)
            {
                _attackTraits.remove(traitType);
            }
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public float getAttackTrait(TraitType traitType) {
        _lock.readLock().lock();
        try
        {
            return _attackTraitValues[traitType.ordinal()];
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }


    public boolean hasAttackTrait(TraitType traitType) {
        _lock.readLock().lock();
        try
        {
            return _attackTraits.contains(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public void mergeDefenceTrait(TraitType traitType, float value)
    {
        _lock.readLock().lock();
        try
        {
            _defenceTraitValues[traitType.ordinal()] += value;
            _defenceTraits.add(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public void removeDefenceTrait(TraitType traitType, float value)
    {
        _lock.readLock().lock();
        try
        {
            _defenceTraitValues[traitType.ordinal()] -= value;
            if (_defenceTraitValues[traitType.ordinal()] == 0)
            {
                _defenceTraits.remove(traitType);
            }
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public float getDefenceTrait(TraitType traitType) {
        _lock.readLock().lock();
        try
        {
            return _defenceTraitValues[traitType.ordinal()];
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public boolean hasDefenceTrait(TraitType traitType) {
        _lock.readLock().lock();
        try
        {
            return _defenceTraits.contains(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public void mergeInvulnerableTrait(TraitType traitType)
    {
        _lock.readLock().lock();
        try
        {
            _invulnerableTraits.add(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    public void removeInvulnerableTrait(TraitType traitType)
    {
        _lock.readLock().lock();
        try
        {
            _invulnerableTraits.remove(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }


    public boolean isInvulnerableTrait(TraitType traitType)
    {
        _lock.readLock().lock();
        try
        {
            return _invulnerableTraits.contains(traitType);
        }
        finally
        {
            _lock.readLock().unlock();
        }
    }

    /**
     * Gets the maximum buff count.
     *
     * @return the maximum buff count
     */
    public int getMaxBuffCount() {
        return _maxBuffCount;
    }

    /**
     * Sets the maximum buff count.
     *
     * @param buffCount the buff count
     */
    public void setMaxBuffCount(int buffCount) {
        _maxBuffCount = buffCount;
    }

    /**
     * Merges the stat's value with the values within the map of adds
     *
     * @param stat
     * @param val
     */
    public void mergeAdd(Stat stat, double val) {
        statsAdd.merge(stat, val, stat::functionAdd);
    }

    /**
     * Merges the stat's value with the values within the map of muls
     *
     * @param stat
     * @param val
     */
    public void mergeMul(Stat stat, double val) {
        statsMul.merge(stat, val, stat::functionMul);
    }

    /**
     * @param stat
     * @return the add value
     */
    public double getAdd(Stat stat) {
        return getAdd(stat, 0d);
    }

    /**
     * @param stat
     * @param defaultValue
     * @return the add value
     */
    public double getAdd(Stat stat, double defaultValue) {
        _lock.readLock().lock();
        try {
            return statsAdd.getOrDefault(stat, defaultValue);
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * @param stat
     * @return the mul value
     */
    public double getMul(Stat stat) {
        return getMul(stat, 1d);
    }

    /**
     * @param stat
     * @param defaultValue
     * @return the mul value
     */
    public double getMul(Stat stat, double defaultValue) {
        _lock.readLock().lock();
        try {
            if(statsMul.containsKey(stat)) {
                return statsMul.get(stat) / 100 + 1;
            }
            return defaultValue;
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * @param stat
     * @param baseValue
     * @return the final value of the stat
     */
    public double getValue(Stat stat, double baseValue) {
        final Double fixedValue = _fixedValue.get(stat);
        return fixedValue != null ? fixedValue : stat.finalize(creature, Optional.of(baseValue));
    }

    /**
     * @param stat
     * @return the final value of the stat
     */
    public double getValue(Stat stat) {
        final Double fixedValue = _fixedValue.get(stat);
        return fixedValue != null ? fixedValue : stat.finalize(creature, Optional.empty());
    }

    protected void resetStats() {
        statsAdd.clear();
        statsMul.clear();
        _vampiricSum = 0;
    }

    /**
     * Locks and resets all stats and recalculates all
     *
     * @param broadcast
     */
    public final void recalculateStats(boolean broadcast) {
        // Copy old data before wiping it out
        final Map<Stat, Double> oldAdds = !broadcast ? Collections.emptyMap() : new EnumMap<>(statsAdd);
        final Map<Stat, Double> oldMuls = !broadcast ? Collections.emptyMap() : new EnumMap<>(statsMul);

        _lock.writeLock().lock();
        try {
            // Wipe all the data
            resetStats();

            // Collect all necessary effects
            final EffectList effectList = creature.getEffectList();
            final Stream<BuffInfo> passives = effectList.getPassives().stream().filter(BuffInfo::isInUse).filter(info -> info.getSkill().checkConditions(SkillConditionScope.PASSIVE, creature, creature));
            final Stream<BuffInfo> options = effectList.getOptions().stream().filter(BuffInfo::isInUse);
            final Stream<BuffInfo> effectsStream = Stream.concat(effectList.getEffects().stream().filter(BuffInfo::isInUse), Stream.concat(passives, options));

            // Call pump to each effect
            //@formatter:off
            effectsStream.forEach(info -> info.getEffects().stream()
                    .filter(effect -> canActivate(info, effect))
                    .forEach(effect -> effect.pump(info.getEffected(), info.getSkill())));
            //@formatter:on

            if (isSummon(creature) && falseIfNullOrElse(creature.getActingPlayer(), player -> player.hasAbnormalType(AbnormalType.ABILITY_CHANGE))) {
                //@formatter:off
                creature.getActingPlayer().getEffectList().getEffects().stream().filter(BuffInfo::isInUse)
                        .filter(info -> info.isAbnormalType(AbnormalType.ABILITY_CHANGE))
                        .forEach(info -> info.getEffects().stream()
                                .filter(effect -> canActivate(info, effect))
                                .forEach(effect -> effect.pump(creature, info.getSkill())));
                //@formatter:on
            }

            // Merge with additional stats
            _additionalAdd.stream().filter(holder -> holder.verifyCondition(creature)).forEach(holder -> mergeAdd(holder.getStat(), holder.getValue()));
            _additionalMul.stream().filter(holder -> holder.verifyCondition(creature)).forEach(holder -> mergeMul(holder.getStat(), holder.getValue()));

            _attackSpeedMultiplier = Formulas.calcAtkSpdMultiplier(creature);
            _mAttackSpeedMultiplier = Formulas.calcMAtkSpdMultiplier(creature);
        } finally {
            _lock.writeLock().unlock();
        }

        onRecalculateStats(broadcast);

        if (broadcast) {
            final var modified = Stat.stream().filter(stat -> isStatChanged(oldAdds, oldMuls, stat)).collect(Collectors.toSet());
            creature.broadcastModifiedStats(modified);
        }
    }

    protected boolean isStatChanged(Map<Stat, Double> oldAdds, Map<Stat, Double> oldMuls, Stat stat) {
        return !( Objects.equals( statsAdd.get(stat), oldAdds.get(stat) ) && Objects.equals( statsMul.get(stat), oldMuls.get(stat) ));
    }

    private boolean canActivate(BuffInfo info, AbstractEffect effect) {
        return effect.canStart(info.getEffector(), info.getEffected(), info.getSkill()) && effect.canPump(info.getEffector(), info.getEffected(), info.getSkill());
    }

    protected void onRecalculateStats(boolean broadcast) {
        // Check if Max HP/MP/CP is lower than current due to new stats.
        if (creature.getCurrentCp() > getMaxCp()) {
            creature.setCurrentCp(getMaxCp());
        }
        if (creature.getCurrentHp() > getMaxHp()) {
            creature.setCurrentHp(getMaxHp());
        }
        if (creature.getCurrentMp() > getMaxMp()) {
            creature.setCurrentMp(getMaxMp());
        }
    }

    public double getPositionTypeValue(Stat stat, Position position) {
        return _positionStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(position, 1d);
    }

    public void mergePositionTypeValue(Stat stat, Position position, double value, BiFunction<? super Double, ? super Double, ? extends Double> func) {
        _positionStats.computeIfAbsent(stat, key -> new ConcurrentHashMap<>()).merge(position, value, func);
    }

    public double getMoveTypeValue(Stat stat, MoveType type) {
        return _moveTypeStats.getOrDefault(stat, Collections.emptyMap()).getOrDefault(type, 0d);
    }

    public void mergeMoveTypeValue(Stat stat, MoveType type, double value) {
        _moveTypeStats.computeIfAbsent(stat, key -> new ConcurrentHashMap<>()).merge(type, value, MathUtil::add);
    }

    public double getReuseTypeValue(SkillType magicType) {
        return reuseStat.getOrDefault(magicType, 1d);
    }

    public void mergeReuseTypeValue(SkillType magicType, double value, BiFunction<? super Double, ? super Double, ? extends Double> func) {
        reuseStat.merge(magicType, value, func);
    }

    public double getMpConsumeTypeValue(SkillType magicType) {
        return mpConsumeStat.getOrDefault(magicType, 1d);
    }

    public void mergeMpConsumeTypeValue(SkillType magicType, double value, BiFunction<? super Double, ? super Double, ? extends Double> func) {
        mpConsumeStat.merge(magicType, value, func);
    }

    public double getSkillEvasionTypeValue(SkillType magicType) {
        var stack = skillEvasionStat.get(magicType);
        return isNullOrEmpty(stack) ? 0 : stack.pop();
    }

    public void addSkillEvasionTypeValue(SkillType magicType, double value) {
        skillEvasionStat.computeIfAbsent(magicType, k -> new Stack<>()).add(value);
    }

    public void removeSkillEvasionTypeValue(SkillType magicType, double value) {
        skillEvasionStat.computeIfPresent(magicType, (k, v) -> {
            v.remove(value);
            return !v.isEmpty() ? v : null;
        });
    }

    public void addToVampiricSum(double sum) {
        _vampiricSum += sum;
    }

    public double getVampiricSum() {
        _lock.readLock().lock();
        try {
            return _vampiricSum;
        } finally {
            _lock.readLock().unlock();
        }
    }

    /**
     * Calculates the time required for this skill to be used again.
     *
     * @param skill the skill from which reuse time will be calculated.
     * @return the time in milliseconds this skill is being under reuse.
     */
    public int getReuseTime(Skill skill) {
        return (skill.isStaticReuse() || skill.isStatic()) ? skill.getReuseDelay() : (int) (skill.getReuseDelay() * getReuseTypeValue(skill.getSkillType()));
    }

    /**
     * Adds static value to the 'add' map of the stat everytime recalculation happens
     *
     * @param stat
     * @param value
     * @param condition
     * @return
     */
    public boolean addAdditionalStat(Stat stat, double value, BiPredicate<Creature, StatsHolder> condition) {
        return _additionalAdd.add(new StatsHolder(stat, value, condition));
    }

    /**
     * Adds static value to the 'add' map of the stat everytime recalculation happens
     *
     * @param stat
     * @param value
     * @return
     */
    public boolean addAdditionalStat(Stat stat, double value) {
        return _additionalAdd.add(new StatsHolder(stat, value));
    }

    /**
     * @param stat
     * @param value
     * @return {@code true} if 'add' was removed, {@code false} in case there wasn't such stat and value
     */
    public boolean removeAddAdditionalStat(Stat stat, double value) {
        final Iterator<StatsHolder> it = _additionalAdd.iterator();
        while (it.hasNext()) {
            final StatsHolder holder = it.next();
            if ((holder.getStat() == stat) && (holder.getValue() == value)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Adds static multiplier to the 'mul' map of the stat everytime recalculation happens
     *
     * @param stat
     * @param value
     * @param condition
     * @return
     */
    public boolean mulAdditionalStat(Stat stat, double value, BiPredicate<Creature, StatsHolder> condition) {
        return _additionalMul.add(new StatsHolder(stat, value, condition));
    }

    /**
     * Adds static multiplier to the 'mul' map of the stat everytime recalculation happens
     *
     * @param stat
     * @param value
     * @return {@code true}
     */
    public boolean mulAdditionalStat(Stat stat, double value) {
        return _additionalMul.add(new StatsHolder(stat, value));
    }

    /**
     * @param stat
     * @param value
     * @return {@code true} if 'mul' was removed, {@code false} in case there wasn't such stat and value
     */
    public boolean removeMulAdditionalStat(Stat stat, double value) {
        final Iterator<StatsHolder> it = _additionalMul.iterator();
        while (it.hasNext()) {
            final StatsHolder holder = it.next();
            if ((holder.getStat() == stat) && (holder.getValue() == value)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * @param stat
     * @param value
     * @return true if the there wasn't previously set fixed value, {@code false} otherwise
     */
    public boolean addFixedValue(Stat stat, Double value) {
        return _fixedValue.put(stat, value) == null;
    }

    /**
     * @param stat
     * @return {@code true} if fixed value is removed, {@code false} otherwise
     */
    public boolean removeFixedValue(Stat stat) {
        return _fixedValue.remove(stat) != null;
    }
}
