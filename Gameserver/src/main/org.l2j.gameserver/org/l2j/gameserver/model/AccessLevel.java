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

import org.l2j.gameserver.data.xml.impl.AdminData;

/**
 * @author HorridoJoho
 */
public class AccessLevel {
    /**
     * Child access levels.
     */
    AccessLevel _childsAccessLevel = null;
    /**
     * The access level.
     */
    private int _accessLevel = 0;
    /**
     * The access level name.
     */
    private String _name = null;
    /**
     * Child access levels.
     */
    private int _child = 0;
    /**
     * The name color for the access level.
     */
    private int _nameColor = 0;
    /**
     * The title color for the access level.
     */
    private int _titleColor = 0;
    /**
     * Flag to determine if the access level has GM access.
     */
    private boolean _isGm = false;
    /**
     * Flag for peace zone attack
     */
    private boolean _allowPeaceAttack = false;
    /**
     * Flag for fixed res
     */
    private boolean _allowFixedRes = false;
    /**
     * Flag for transactions
     */
    private boolean _allowTransaction = false;
    /**
     * Flag for AltG commands
     */
    private boolean _allowAltG = false;
    /**
     * Flag to give damage
     */
    private boolean _giveDamage = false;
    /**
     * Flag to take aggro
     */
    private boolean _takeAggro = false;
    /**
     * Flag to gain exp in party
     */
    private boolean _gainExp = false;

    public AccessLevel(StatsSet set) {
        _accessLevel = set.getInt("level");
        _name = set.getString("name");
        _nameColor = Integer.decode("0x" + set.getString("nameColor", "FFFFFF"));
        _titleColor = Integer.decode("0x" + set.getString("titleColor", "FFFFFF"));
        _child = set.getInt("childAccess", 0);
        _isGm = set.getBoolean("isGM", false);
        _allowPeaceAttack = set.getBoolean("allowPeaceAttack", false);
        _allowFixedRes = set.getBoolean("allowFixedRes", false);
        _allowTransaction = set.getBoolean("allowTransaction", true);
        _allowAltG = set.getBoolean("allowAltg", false);
        _giveDamage = set.getBoolean("giveDamage", true);
        _takeAggro = set.getBoolean("takeAggro", true);
        _gainExp = set.getBoolean("gainExp", true);
    }

    public AccessLevel() {
        _accessLevel = 0;
        _name = "User";
        _nameColor = Integer.decode("0xFFFFFF");
        _titleColor = Integer.decode("0xFFFFFF");
        _child = 0;
        _isGm = false;
        _allowPeaceAttack = false;
        _allowFixedRes = false;
        _allowTransaction = true;
        _allowAltG = false;
        _giveDamage = true;
        _takeAggro = true;
        _gainExp = true;
    }

    /**
     * Returns the access level<br>
     * <br>
     *
     * @return int: access level<br>
     */
    public int getLevel() {
        return _accessLevel;
    }

    /**
     * Returns the access level name<br>
     * <br>
     *
     * @return String: access level name<br>
     */
    public String getName() {
        return _name;
    }

    /**
     * Returns the name color of the access level<br>
     * <br>
     *
     * @return int: the name color for the access level<br>
     */
    public int getNameColor() {
        return _nameColor;
    }

    /**
     * Returns the title color color of the access level<br>
     * <br>
     *
     * @return int: the title color for the access level<br>
     */
    public int getTitleColor() {
        return _titleColor;
    }

    /**
     * Retuns if the access level has gm access or not<br>
     * <br>
     *
     * @return boolean: true if access level have gm access, otherwise false<br>
     */
    public boolean isGm() {
        return _isGm;
    }

    /**
     * Returns if the access level is allowed to attack in peace zone or not<br>
     * <br>
     *
     * @return boolean: true if the access level is allowed to attack in peace zone, otherwise false<br>
     */
    public boolean allowPeaceAttack() {
        return _allowPeaceAttack;
    }

    /**
     * Retruns if the access level is allowed to use fixed res or not<br>
     * <br>
     *
     * @return true if the access level is allowed to use fixed res, otherwise false<br>
     */
    public boolean allowFixedRes() {
        return _allowFixedRes;
    }

    /**
     * Returns if the access level is allowed to perform transactions or not<br>
     * <br>
     *
     * @return boolean: true if access level is allowed to perform transactions, otherwise false<br>
     */
    public boolean allowTransaction() {
        return _allowTransaction;
    }

    /**
     * Returns if the access level is allowed to use AltG commands or not<br>
     * <br>
     *
     * @return boolean: true if access level is allowed to use AltG commands, otherwise false<br>
     */
    public boolean allowAltG() {
        return _allowAltG;
    }

    /**
     * Returns if the access level can give damage or not<br>
     * <br>
     *
     * @return boolean: true if the access level can give damage, otherwise false<br>
     */
    public boolean canGiveDamage() {
        return _giveDamage;
    }

    /**
     * Returns if the access level can take aggro or not<br>
     * <br>
     *
     * @return boolean: true if the access level can take aggro, otherwise false<br>
     */
    public boolean canTakeAggro() {
        return _takeAggro;
    }

    /**
     * Returns if the access level can gain exp or not<br>
     * <br>
     *
     * @return boolean: true if the access level can gain exp, otherwise false<br>
     */
    public boolean canGainExp() {
        return _gainExp;
    }

    /**
     * Returns if the access level contains allowedAccess as child<br>
     *
     * @param accessLevel as AccessLevel<br>
     * @return boolean: true if a child access level is equals to allowedAccess, otherwise false<br>
     */
    public boolean hasChildAccess(AccessLevel accessLevel) {
        if (_childsAccessLevel == null) {
            if (_child <= 0) {
                return false;
            }

            _childsAccessLevel = AdminData.getInstance().getAccessLevel(_child);
        }
        return (_childsAccessLevel != null) && ((_childsAccessLevel.getLevel() == accessLevel.getLevel()) || _childsAccessLevel.hasChildAccess(accessLevel));
    }
}