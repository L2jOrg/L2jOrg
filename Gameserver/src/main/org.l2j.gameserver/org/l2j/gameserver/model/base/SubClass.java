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
package org.l2j.gameserver.model.base;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.LevelData;

/**
 * Character Sub-Class Definition <BR>
 * Used to store key information about a character's sub-class.
 *
 * @author Tempy
 */
public final class SubClass {
    private static final byte _maxLevel = Config.MAX_SUBCLASS_LEVEL < LevelData.getInstance().getMaxLevel() ? Config.MAX_SUBCLASS_LEVEL : (byte) (LevelData.getInstance().getMaxLevel() - 1);
    private static final int MAX_VITALITY_POINTS = 140000;
    private static final int MIN_VITALITY_POINTS = 0;
    private ClassId _class;
    private long _exp = LevelData.getInstance().getExpForLevel(Config.BASE_SUBCLASS_LEVEL);
    private long _sp = 0;
    private byte _level = Config.BASE_SUBCLASS_LEVEL;
    private int _classIndex = 1;
    private int _vitalityPoints = 0;
    private boolean _dualClass = false;

    public SubClass() {
        // Used for specifying ALL attributes of a sub class directly,
        // using the preset default values.
    }

    public ClassId getClassDefinition() {
        return _class;
    }

    public int getClassId() {
        return _class.getId();
    }

    public void setClassId(int classId) {
        _class = ClassId.getClassId(classId);
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(long expValue) {
        if (!_dualClass && (expValue > (LevelData.getInstance().getMaxExp() - 1))) {
            expValue = LevelData.getInstance().getExpForLevel(_maxLevel + 1) - 1;
        }

        _exp = expValue;
    }

    public long getSp() {
        return _sp;
    }

    public void setSp(long spValue) {
        _sp = spValue;
    }

    public byte getLevel() {
        return _level;
    }

    public void setLevel(byte levelValue) {
        if (!_dualClass && (levelValue > _maxLevel)) {
            levelValue = _maxLevel;
        } else if (levelValue < Config.BASE_SUBCLASS_LEVEL) {
            levelValue = Config.BASE_SUBCLASS_LEVEL;
        }

        _level = levelValue;
    }

    public int getVitalityPoints() {
        return Math.min(Math.max(_vitalityPoints, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
    }

    public void setVitalityPoints(int value) {
        _vitalityPoints = Math.min(Math.max(value, MIN_VITALITY_POINTS), MAX_VITALITY_POINTS);
    }

    /**
     * First Sub-Class is index 1.
     *
     * @return int _classIndex
     */
    public int getClassIndex() {
        return _classIndex;
    }

    public void setClassIndex(int classIndex) {
        _classIndex = classIndex;
    }

    public boolean isDualClass() {
        return _dualClass;
    }

    public void setIsDualClass(boolean dualClass) {
        _dualClass = dualClass;
    }
}
