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

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.variables.PlayerVariables;

/**
 * Used to Store data sent to Client for Character.<br>
 * Selection screen.
 *
 * @version $Revision: 1.2.2.2.2.4 $ $Date: 2005/03/27 15:29:33 $
 */
public class CharSelectInfoPackage {
    private final int[][] _paperdoll;
    private final PlayerVariables _vars;
    private String _name;
    private int _objectId = 0;
    private long _exp = 0;
    private long _sp = 0;
    private int _clanId = 0;
    private int _race = 0;
    private int _classId = 0;
    private int _baseClassId = 0;
    private long _deleteTimer = 0;
    private long _lastAccess = 0;
    private int _face = 0;
    private int _hairStyle = 0;
    private int _hairColor = 0;
    private int _sex = 0;
    private int _level = 1;
    private int _maxHp = 0;
    private double _currentHp = 0;
    private int _maxMp = 0;
    private double _currentMp = 0;
    private int _reputation = 0;
    private int _pkKills = 0;
    private int _pvpKills = 0;
    private VariationInstance _augmentation;
    private int _x = 0;
    private int _y = 0;
    private int _z = 0;
    private String _htmlPrefix = null;
    private int _vitalityPoints = 0;
    private int _accessLevel = 0;
    private boolean _isNoble;

    /**
     * Constructor for CharSelectInfoPackage.
     *
     * @param objectId character object Id.
     * @param name     the character's name.
     */
    public CharSelectInfoPackage(int objectId, String name) {
        setObjectId(objectId);
        _name = name;
        _paperdoll = PlayerInventory.restoreVisibleInventory(objectId);
        _vars = new PlayerVariables(_objectId);
    }

    /**
     * @return the character object Id.
     */
    public int getObjectId() {
        return _objectId;
    }

    public void setObjectId(int objectId) {
        _objectId = objectId;
    }

    /**
     * @return the character's access level.
     */
    public int getAccessLevel() {
        return _accessLevel;
    }

    /**
     * @param level the character's access level to be set.
     */
    public void setAccessLevel(int level) {
        _accessLevel = level;
    }

    public int getClanId() {
        return _clanId;
    }

    public void setClanId(int clanId) {
        _clanId = clanId;
    }

    public int getClassId() {
        return _classId;
    }

    public void setClassId(int classId) {
        _classId = classId;
    }

    public int getBaseClassId() {
        return _baseClassId;
    }

    public void setBaseClassId(int baseClassId) {
        _baseClassId = baseClassId;
    }

    public double getCurrentHp() {
        return _currentHp;
    }

    public void setCurrentHp(double currentHp) {
        _currentHp = currentHp;
    }

    public double getCurrentMp() {
        return _currentMp;
    }

    public void setCurrentMp(double currentMp) {
        _currentMp = currentMp;
    }

    public long getDeleteTimer() {
        return _deleteTimer;
    }

    public void setDeleteTimer(long deleteTimer) {
        _deleteTimer = deleteTimer;
    }

    public long getLastAccess() {
        return _lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        _lastAccess = lastAccess;
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(long exp) {
        _exp = exp;
    }

    public int getFace() {
        return _vars.getInt("visualFaceId", _face);
    }

    public void setFace(int face) {
        _face = face;
    }

    public int getHairColor() {
        return _vars.getInt("visualHairColorId", _hairColor);
    }

    public void setHairColor(int hairColor) {
        _hairColor = hairColor;
    }

    public int getHairStyle() {
        return _vars.getInt("visualHairId", _hairStyle);
    }

    public void setHairStyle(int hairStyle) {
        _hairStyle = hairStyle;
    }

    public int getPaperdollObjectId(InventorySlot slot) {
        return _paperdoll[slot.getId()][0];
    }

    public int getPaperdollItemId(int slot) {
        return _paperdoll[slot][1];
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int level) {
        _level = level;
    }

    public int getMaxHp() {
        return _maxHp;
    }

    public void setMaxHp(int maxHp) {
        _maxHp = maxHp;
    }

    public int getMaxMp() {
        return _maxMp;
    }

    public void setMaxMp(int maxMp) {
        _maxMp = maxMp;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public int getRace() {
        return _race;
    }

    public void setRace(int race) {
        _race = race;
    }

    public int getSex() {
        return _sex;
    }

    public void setSex(int sex) {
        _sex = sex;
    }

    public long getSp() {
        return _sp;
    }

    public void setSp(long sp) {
        _sp = sp;
    }

    public int getEnchantEffect(int slot) {
        return _paperdoll[slot][2];
    }

    public int getReputation() {
        return _reputation;
    }

    public void setReputation(int reputation) {
        _reputation = reputation;
    }

    public VariationInstance getAugmentation() {
        return _augmentation;
    }

    public void setAugmentation(VariationInstance augmentation) {
        _augmentation = augmentation;
    }

    public int getPkKills() {
        return _pkKills;
    }

    public void setPkKills(int PkKills) {
        _pkKills = PkKills;
    }

    public int getPvPKills() {
        return _pvpKills;
    }

    public void setPvPKills(int PvPKills) {
        _pvpKills = PvPKills;
    }

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        _y = y;
    }

    public int getZ() {
        return _z;
    }

    public void setZ(int z) {
        _z = z;
    }

    public String getHtmlPrefix() {
        return _htmlPrefix;
    }

    public void setHtmlPrefix(String s) {
        _htmlPrefix = s;
    }

    public int getVitalityPoints() {
        return _vitalityPoints;
    }

    public void setVitalityPoints(int points) {
        _vitalityPoints = points;
    }

    public boolean isHairAccessoryEnabled() {
        return _vars.getBoolean(PlayerVariables.HAIR_ACCESSORY_VARIABLE_NAME, true);
    }

    public int getVitalityItemsUsed() {
        return _vars.getInt(PlayerVariables.VITALITY_ITEMS_USED_VARIABLE_NAME, 0);
    }

    public boolean isNoble() {
        return _isNoble;
    }

    public void setNoble(boolean noble) {
        _isNoble = noble;
    }
}
