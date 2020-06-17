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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.base.ClassId;

import java.util.EnumMap;
import java.util.List;

import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author mkizub, Zoey76
 * @author JoeAlisson
 */
public class PlayerTemplate extends CreatureTemplate {
    private final ClassId classId;

    private final float[] baseHp;
    private final float[] baseMp;
    private final float[] baseCp;

    private final double[] baseHpReg;
    private final double[] baseMpReg;
    private final double[] baseCpReg;

    private final double fCollisionHeightFemale;
    private final double fCollisionRadiusFemale;

    private final int baseSafeFallHeight;

    private final List<Location> creationPoints;
    private final EnumMap<InventorySlot, Integer> baseSlotDef;

    public PlayerTemplate(StatsSet set, List<Location> creationPoints) {
        super(set);
        classId = ClassId.getClassId(set.getInt("classId"));
        setRace(classId.getRace());
        var maxLevel = LevelData.getInstance().getMaxLevel() +1;
        baseHp = new float[maxLevel];
        baseMp = new float[maxLevel];
        baseCp = new float[maxLevel];
        baseHpReg = new double[maxLevel];
        baseMpReg = new double[maxLevel];
        baseCpReg = new double[maxLevel];


        baseSlotDef = new EnumMap<>(InventorySlot.class);
        baseSlotDef.put(CHEST, set.getInt("basePDefchest", 0));
        baseSlotDef.put(LEGS, set.getInt("basePDeflegs", 0));
        baseSlotDef.put(HEAD, set.getInt("basePDefhead", 0));
        baseSlotDef.put(FEET, set.getInt("basePDeffeet", 0));
        baseSlotDef.put(GLOVES, set.getInt("basePDefgloves", 0));
        baseSlotDef.put(PENDANT, set.getInt("basePDefunderwear", 0));
        baseSlotDef.put(CLOAK, set.getInt("basePDefcloak", 0));

        baseSlotDef.put(RIGHT_EAR, set.getInt("baseMDefrear", 0));
        baseSlotDef.put(LEFT_EAR, set.getInt("baseMDeflear", 0));
        baseSlotDef.put(RIGHT_FINGER, set.getInt("baseMDefrfinger", 0));
        baseSlotDef.put(LEFT_FINGER, set.getInt("baseMDefrfinger", 0));
        baseSlotDef.put(NECK, set.getInt("baseMDefneck", 0));

        fCollisionRadiusFemale = set.getDouble("collisionFemaleradius");
        fCollisionHeightFemale = set.getDouble("collisionFemaleheight");

        baseSafeFallHeight = set.getInt("baseSafeFall", 333);
        this.creationPoints = creationPoints;
    }

    /**
     * @return the template class Id.
     */
    public ClassId getClassId() {
        return classId;
    }

    /**
     * @return random Location of created character spawn.
     */
    public Location getCreationPoint() {
        return creationPoints.get(Rnd.get(creationPoints.size()));
    }

    /**
     * Sets the value of level upgain parameter.
     *
     * @param paramName name of parameter
     * @param level     corresponding character level
     * @param val       value of parameter
     */
    public void setUpgainValue(String paramName, int level, double val) {
        switch (paramName) {
            case "hp" -> baseHp[level] = (float) val;
            case "mp" -> baseMp[level] = (float) val;
            case "cp"-> baseCp[level] = (float) val;
            case "hpRegen" -> baseHpReg[level] = val;
            case "mpRegen" -> baseMpReg[level] = val;
            case "cpRegen" -> baseCpReg[level] = val;
        }
    }

    /**
     * @param level character level to return value
     * @return the baseHpMax for given character level
     */
    public float getBaseHpMax(int level) {
        return baseHp[level];
    }

    /**
     * @param level character level to return value
     * @return the baseMpMax for given character level
     */
    public float getBaseMpMax(int level) {
        return baseMp[level];
    }

    /**
     * @param level character level to return value
     * @return the baseCpMax for given character level
     */
    public float getBaseCpMax(int level) {
        return baseCp[level];
    }

    /**
     * @param level character level to return value
     * @return the base HP Regeneration for given character level
     */
    public double getBaseHpRegen(int level) {
        return baseHpReg[level];
    }

    /**
     * @param level character level to return value
     * @return the base MP Regeneration for given character level
     */
    public double getBaseMpRegen(int level) {
        return baseMpReg[level];
    }

    /**
     * @param level character level to return value
     * @return the base HP Regeneration for given character level
     */
    public double getBaseCpRegen(int level) {
        return baseCpReg[level];
    }

    /**
     * @param slot inventory slot to return value
     * @return defense value of character for EMPTY given slot
     */
    public int getBaseDefBySlot(InventorySlot slot) {
        return baseSlotDef.getOrDefault(slot, 0);
    }

    /**
     * @return the template collision height for female characters.
     */
    public double getFCollisionHeightFemale() {
        return fCollisionHeightFemale;
    }

    /**
     * @return the template collision radius for female characters.
     */
    public double getFCollisionRadiusFemale() {
        return fCollisionRadiusFemale;
    }

    /**
     * @return the safe fall height.
     */
    public int getSafeFallHeight() {
        return baseSafeFallHeight;
    }
}
