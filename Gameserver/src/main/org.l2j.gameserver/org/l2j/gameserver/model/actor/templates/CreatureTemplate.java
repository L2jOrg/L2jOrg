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
package org.l2j.gameserver.model.actor.templates;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.stats.Stat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Character template.
 *
 * @author Zoey76
 */
public class CreatureTemplate extends ListenersContainer {
    protected final Map<Stat, Double> _baseValues = new EnumMap<>(Stat.class);
    // BaseStats
    private WeaponType _baseAttackType;
    /**
     * For client info use {@link #_fCollisionRadius}
     */
    private int _collisionRadius;
    private double _fCollisionRadius;
    /**
     * For client info use {@link #_fCollisionHeight}
     */
    private int _collisionHeight;
    private double _fCollisionHeight;
    /**
     * The creature's race.
     */
    private Race _race;

    public CreatureTemplate(StatsSet set) {
        set(set);
    }

    public void set(StatsSet set) {

        _baseValues.put(Stat.STAT_STR, set.getDouble("baseSTR", 0));
        _baseValues.put(Stat.STAT_CON, set.getDouble("baseCON", 0));
        _baseValues.put(Stat.STAT_DEX, set.getDouble("baseDEX", 0));
        _baseValues.put(Stat.STAT_INT, set.getDouble("baseINT", 0));
        _baseValues.put(Stat.STAT_WIT, set.getDouble("baseWIT", 0));
        _baseValues.put(Stat.STAT_MEN, set.getDouble("baseMEN", 0));

        _baseValues.put(Stat.MAX_HP, set.getDouble("baseHpMax", 0));
        _baseValues.put(Stat.MAX_MP, set.getDouble("baseMpMax", 0));
        _baseValues.put(Stat.MAX_CP, set.getDouble("baseCpMax", 0));

        // Regenerate HP/MP/CP
        _baseValues.put(Stat.REGENERATE_HP_RATE, set.getDouble("baseHpReg", 0));
        _baseValues.put(Stat.REGENERATE_MP_RATE, set.getDouble("baseMpReg", 0));
        _baseValues.put(Stat.REGENERATE_CP_RATE, set.getDouble("baseCpReg", 0));

        // Attack and Defense
        _baseValues.put(Stat.PHYSICAL_ATTACK, set.getDouble("basePAtk", 0));
        _baseValues.put(Stat.MAGIC_ATTACK, set.getDouble("baseMAtk", 0));
        _baseValues.put(Stat.PHYSICAL_DEFENCE, set.getDouble("basePDef", 0));
        _baseValues.put(Stat.MAGICAL_DEFENCE, set.getDouble("baseMDef", 0));

        // Attack speed
        _baseValues.put(Stat.PHYSICAL_ATTACK_SPEED, set.getDouble("basePAtkSpd", 300));
        _baseValues.put(Stat.MAGIC_ATTACK_SPEED, set.getDouble("baseMAtkSpd", 333));

        // Misc
        _baseValues.put(Stat.SHIELD_DEFENCE, set.getDouble("baseShldDef", 0));
        _baseValues.put(Stat.PHYSICAL_ATTACK_RANGE, set.getDouble("baseAtkRange", 40));
        _baseValues.put(Stat.RANDOM_DAMAGE, set.getDouble("baseRndDam", 0));

        // Shield and critical rates
        _baseValues.put(Stat.SHIELD_DEFENCE_RATE, set.getDouble("baseShldRate", 0));
        _baseValues.put(Stat.CRITICAL_RATE, set.getDouble("baseCritRate", 4));
        _baseValues.put(Stat.MAGIC_CRITICAL_RATE, set.getDouble("baseMCritRate", 0));

        // Breath under water
        _baseValues.put(Stat.BREATH, set.getDouble("baseBreath", 100));

        // Elemental Attributes
        // Attack
        _baseValues.put(Stat.FIRE_POWER, set.getDouble("baseFire", 0));
        _baseValues.put(Stat.WIND_POWER, set.getDouble("baseWind", 0));
        _baseValues.put(Stat.WATER_POWER, set.getDouble("baseWater", 0));
        _baseValues.put(Stat.EARTH_POWER, set.getDouble("baseEarth", 0));
        _baseValues.put(Stat.HOLY_POWER, set.getDouble("baseHoly", 0));
        _baseValues.put(Stat.DARK_POWER, set.getDouble("baseDark", 0));

        // Defense
        _baseValues.put(Stat.FIRE_RES, set.getDouble("baseFireRes", 0));
        _baseValues.put(Stat.WIND_RES, set.getDouble("baseWindRes", 0));
        _baseValues.put(Stat.WATER_RES, set.getDouble("baseWaterRes", 0));
        _baseValues.put(Stat.EARTH_RES, set.getDouble("baseEarthRes", 0));
        _baseValues.put(Stat.HOLY_RES, set.getDouble("baseHolyRes", 0));
        _baseValues.put(Stat.DARK_RES, set.getDouble("baseDarkRes", 0));
        _baseValues.put(Stat.BASE_ATTRIBUTE_RES, set.getDouble("baseElementRes", 0));

        // Geometry
        _fCollisionHeight = set.getDouble("collision_height", 0);
        _fCollisionRadius = set.getDouble("collision_radius", 0);
        _collisionRadius = (int) _fCollisionRadius;
        _collisionHeight = (int) _fCollisionHeight;

        // Speed
        _baseValues.put(Stat.RUN_SPEED, set.getDouble("baseRunSpd", 120));
        _baseValues.put(Stat.WALK_SPEED, set.getDouble("baseWalkSpd", 50));

        // Swimming
        _baseValues.put(Stat.SWIM_RUN_SPEED, set.getDouble("baseSwimRunSpd", 120));
        _baseValues.put(Stat.SWIM_WALK_SPEED, set.getDouble("baseSwimWalkSpd", 50));

        // Flying
        _baseValues.put(Stat.FLY_RUN_SPEED, set.getDouble("baseFlyRunSpd", 120));
        _baseValues.put(Stat.FLY_WALK_SPEED, set.getDouble("baseFlyWalkSpd", 50));

        // Attack type
        _baseAttackType = set.getEnum("baseAtkType", WeaponType.class, WeaponType.FIST);

        // Basic property
        _baseValues.put(Stat.ABNORMAL_RESIST_PHYSICAL, set.getDouble("physicalAbnormalResist", 10));
        _baseValues.put(Stat.ABNORMAL_RESIST_MAGICAL, set.getDouble("magicAbnormalResist", 10));
    }

    /**
     * @return the baseSTR
     */
    public int getBaseSTR() {
        return _baseValues.getOrDefault(Stat.STAT_STR, 0d).intValue();
    }

    /**
     * @return the baseCON
     */
    public int getBaseCON() {
        return _baseValues.getOrDefault(Stat.STAT_CON, 0d).intValue();
    }

    /**
     * @return the baseDEX
     */
    public int getBaseDEX() {
        return _baseValues.getOrDefault(Stat.STAT_DEX, 0d).intValue();
    }

    /**
     * @return the baseINT
     */
    public int getBaseINT() {
        return _baseValues.getOrDefault(Stat.STAT_INT, 0d).intValue();
    }

    /**
     * @return the baseWIT
     */
    public int getBaseWIT() {
        return _baseValues.getOrDefault(Stat.STAT_WIT, 0d).intValue();
    }

    /**
     * @return the baseMEN
     */
    public int getBaseMEN() {
        return _baseValues.getOrDefault(Stat.STAT_MEN, 0d).intValue();
    }

    /**
     * @return the baseHpMax
     */
    public float getBaseHpMax() {
        return _baseValues.getOrDefault(Stat.MAX_HP, 0d).floatValue();
    }

    /**
     * @return the baseCpMax
     */
    public float getBaseCpMax() {
        return _baseValues.getOrDefault(Stat.MAX_CP, 0d).floatValue();
    }

    /**
     * @return the baseMpMax
     */
    public float getBaseMpMax() {
        return _baseValues.getOrDefault(Stat.MAX_MP, 0d).floatValue();
    }

    /**
     * @return the baseHpReg
     */
    public float getBaseHpReg() {
        return _baseValues.getOrDefault(Stat.REGENERATE_HP_RATE, 0d).floatValue();
    }

    /**
     * @return the baseMpReg
     */
    public float getBaseMpReg() {
        return _baseValues.getOrDefault(Stat.REGENERATE_MP_RATE, 0d).floatValue();
    }

    /**
     * @return the _baseFire
     */
    public int getBaseFire() {
        return _baseValues.getOrDefault(Stat.FIRE_POWER, 0d).intValue();
    }

    /**
     * @return the _baseWind
     */
    public int getBaseWind() {
        return _baseValues.getOrDefault(Stat.WIND_POWER, 0d).intValue();
    }

    /**
     * @return the _baseWater
     */
    public int getBaseWater() {
        return _baseValues.getOrDefault(Stat.WATER_POWER, 0d).intValue();
    }

    /**
     * @return the _baseEarth
     */
    public int getBaseEarth() {
        return _baseValues.getOrDefault(Stat.EARTH_POWER, 0d).intValue();
    }

    /**
     * @return the _baseHoly
     */
    public int getBaseHoly() {
        return _baseValues.getOrDefault(Stat.HOLY_POWER, 0d).intValue();
    }

    /**
     * @return the _baseDark
     */
    public int getBaseDark() {
        return _baseValues.getOrDefault(Stat.DARK_POWER, 0d).intValue();
    }

    /**
     * @return the _baseFireRes
     */
    public double getBaseFireRes() {
        return _baseValues.getOrDefault(Stat.FIRE_RES, 0d);
    }

    /**
     * @return the _baseWindRes
     */
    public double getBaseWindRes() {
        return _baseValues.getOrDefault(Stat.WIND_RES, 0d);
    }

    /**
     * @return the _baseWaterRes
     */
    public double getBaseWaterRes() {
        return _baseValues.getOrDefault(Stat.WATER_RES, 0d);
    }

    /**
     * @return the _baseEarthRes
     */
    public double getBaseEarthRes() {
        return _baseValues.getOrDefault(Stat.EARTH_RES, 0d);
    }

    /**
     * @return the _baseHolyRes
     */
    public double getBaseHolyRes() {
        return _baseValues.getOrDefault(Stat.HOLY_RES, 0d);
    }

    /**
     * @return the _baseDarkRes
     */
    public double getBaseDarkRes() {
        return _baseValues.getOrDefault(Stat.DARK_RES, 0d);
    }

    /**
     * @return the _baseElementRes
     */
    public double getBaseElementRes() {
        return _baseValues.getOrDefault(Stat.BASE_ATTRIBUTE_RES, 0d);
    }

    /**
     * @return the basePAtk
     */
    public int getBasePAtk() {
        return _baseValues.getOrDefault(Stat.PHYSICAL_ATTACK, 0d).intValue();
    }

    /**
     * @return the baseMAtk
     */
    public int getBaseMAtk() {
        return _baseValues.getOrDefault(Stat.MAGIC_ATTACK, 0d).intValue();
    }

    /**
     * @return the basePDef
     */
    public int getBasePDef() {
        return _baseValues.getOrDefault(Stat.PHYSICAL_DEFENCE, 0d).intValue();
    }

    /**
     * @return the baseMDef
     */
    public int getBaseMDef() {
        return _baseValues.getOrDefault(Stat.MAGICAL_DEFENCE, 0d).intValue();
    }

    /**
     * @return the basePAtkSpd
     */
    public int getBasePAtkSpd() {
        return _baseValues.getOrDefault(Stat.PHYSICAL_ATTACK_SPEED, 0d).intValue();
    }

    /**
     * @return the baseMAtkSpd
     */
    public int getBaseMAtkSpd() {
        return _baseValues.getOrDefault(Stat.MAGIC_ATTACK_SPEED, 0d).intValue();
    }

    /**
     * @return the random damage
     */
    public int getRandomDamage() {
        return _baseValues.getOrDefault(Stat.RANDOM_DAMAGE, 0d).intValue();
    }

    /**
     * @return the baseShldDef
     */
    public int getBaseShldDef() {
        return _baseValues.getOrDefault(Stat.SHIELD_DEFENCE, 0d).intValue();
    }

    /**
     * @return the baseShldRate
     */
    public int getBaseShldRate() {
        return _baseValues.getOrDefault(Stat.SHIELD_DEFENCE_RATE, 0d).intValue();
    }

    /**
     * @return the baseCritRate
     */
    public int getBaseCritRate() {
        return _baseValues.getOrDefault(Stat.CRITICAL_RATE, 0d).intValue();
    }

    /**
     * @return the baseMCritRate
     */
    public int getBaseMCritRate() {
        return _baseValues.getOrDefault(Stat.MAGIC_CRITICAL_RATE, 0d).intValue();
    }

    /**
     * @return the baseBreath
     */
    public int getBaseBreath() {
        return _baseValues.getOrDefault(Stat.BREATH, 0d).intValue();
    }

    /**
     * @return base abnormal resist by basic property type.
     */
    public int getBaseAbnormalResistPhysical() {
        return _baseValues.getOrDefault(Stat.ABNORMAL_RESIST_PHYSICAL, 0d).intValue();
    }

    /**
     * @return base abnormal resist by basic property type.
     */
    public int getBaseAbnormalResistMagical() {
        return _baseValues.getOrDefault(Stat.ABNORMAL_RESIST_MAGICAL, 0d).intValue();
    }

    /**
     * @return the collisionRadius
     */
    public int getCollisionRadius() {
        return _collisionRadius;
    }

    /**
     * @return the collisionHeight
     */
    public int getCollisionHeight() {
        return _collisionHeight;
    }

    /**
     * @return the fCollisionRadius
     */
    public double getfCollisionRadius() {
        return _fCollisionRadius;
    }

    /**
     * @return the fCollisionHeight
     */
    public double getfCollisionHeight() {
        return _fCollisionHeight;
    }

    /**
     * @return the base attack type (Sword, Fist, Blunt, etc..)
     */
    public WeaponType getBaseAttackType() {
        return _baseAttackType;
    }

    /**
     * Sets base attack type.
     *
     * @param type
     */
    public void setBaseAttackType(WeaponType type) {
        _baseAttackType = type;
    }

    /**
     * @return the baseAtkRange
     */
    public int getBaseAttackRange() {
        return _baseValues.getOrDefault(Stat.PHYSICAL_ATTACK_RANGE, 0d).intValue();
    }

    /**
     * Overridden in NpcTemplate
     *
     * @return the characters skills
     */
    public Map<Integer, Skill> getSkills() {
        return Collections.emptyMap();
    }

    /**
     * Gets the craeture's race.
     *
     * @return the race
     */
    public Race getRace() {
        return _race;
    }

    /**
     * Sets the creature's race.
     *
     * @param race the race
     */
    public void setRace(Race race) {
        _race = race;
    }

    /**
     * @param stat
     * @param defaultValue
     * @return
     */
    public double getBaseValue(Stat stat, double defaultValue) {
        return _baseValues.getOrDefault(stat, defaultValue);
    }
}
