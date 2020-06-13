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

import org.l2j.gameserver.enums.DoorOpenType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * Doors template.
 *
 * @author JIV
 */
public class DoorTemplate extends CreatureTemplate implements IIdentifiable {
    private final int _doorId;
    private final int _nodeX[];
    private final int _nodeY[];
    private final int _nodeZ;
    private final int _height;
    private final int _posX;
    private final int _posY;
    private final int _posZ;
    private final int _emmiter;
    private final int _childDoorId;
    private final String _name;
    private final String _groupName;
    private final boolean _showHp;
    private final boolean _isWall;
    // -1 close, 0 nothing, 1 open
    private final byte _masterDoorClose;
    private final byte _masterDoorOpen;

    private final boolean _isTargetable;
    private final boolean _default_status;
    private final int _closeTime;
    private final int _level;
    private final DoorOpenType _openType;
    private final boolean _checkCollision;
    private final boolean _isAttackableDoor;
    private final boolean _stealth;
    private final boolean _isInverted;
    private int _openTime;
    private int _randomTime;

    public DoorTemplate(StatsSet set) {
        super(set);
        _doorId = set.getInt("id");
        _name = set.getString("name");

        // position
        _height = set.getInt("height", 150);
        _nodeZ = set.getInt("nodeZ");
        _nodeX = new int[4]; // 4 * x
        _nodeY = new int[4]; // 4 * y
        for (int i = 0; i < 4; i++) {
            _nodeX[i] = set.getInt("nodeX_" + i);
            _nodeY[i] = set.getInt("nodeY_" + i);
        }
        _posX = set.getInt("x");
        _posY = set.getInt("y");
        _posZ = Math.min(set.getInt("z"), _nodeZ);

        // optional
        _emmiter = set.getInt("emmiterId", 0);
        _showHp = set.getBoolean("showHp", true);
        _isWall = set.getBoolean("isWall", false);
        _groupName = set.getString("group", null);

        _childDoorId = set.getInt("childId", -1);
        // true if door is opening
        String masterevent = set.getString("masterClose", "act_nothing");
        _masterDoorClose = (byte) (masterevent.equals("act_open") ? 1 : masterevent.equals("act_close") ? -1 : 0);

        masterevent = set.getString("masterOpen", "act_nothing");
        _masterDoorOpen = (byte) (masterevent.equals("act_open") ? 1 : masterevent.equals("act_close") ? -1 : 0);

        _isTargetable = set.getBoolean("targetable", true);
        _default_status = set.getString("default", "close").equals("open");
        _closeTime = set.getInt("closeTime", -1);
        _level = set.getInt("level", 0);

        _openType = set.getEnum("openMethod", DoorOpenType.class, DoorOpenType.NONE);
        _checkCollision = set.getBoolean("isCheckCollision", true);
        if (_openType == DoorOpenType.BY_TIME) {
            _openTime = set.getInt("openTime");
            _randomTime = set.getInt("randomTime", -1);
        }
        _isAttackableDoor = set.getBoolean("attackable", false);
        _stealth = set.getBoolean("stealth", false);
        _isInverted = set.getBoolean("isInverted", false);
    }

    /**
     * Gets the door ID.
     *
     * @return the door ID
     */
    @Override
    public int getId() {
        return _doorId;
    }

    public String getName() {
        return _name;
    }

    public int[] getNodeX() {
        return _nodeX;
    }

    public int[] getNodeY() {
        return _nodeY;
    }

    public int getNodeZ() {
        return _nodeZ;
    }

    public int getHeight() {
        return _height;
    }

    public int getX() {
        return _posX;
    }

    public int getY() {
        return _posY;
    }

    public int getZ() {
        return _posZ;
    }

    public int getEmmiter() {
        return _emmiter;
    }

    public int getChildDoorId() {
        return _childDoorId;
    }

    public String getGroupName() {
        return _groupName;
    }

    public boolean isShowHp() {
        return _showHp;
    }

    public boolean isWall() {
        return _isWall;
    }

    public byte getMasterDoorOpen() {
        return _masterDoorOpen;
    }

    public byte getMasterDoorClose() {
        return _masterDoorClose;
    }

    public boolean isTargetable() {
        return _isTargetable;
    }

    public boolean isOpenByDefault() {
        return _default_status;
    }

    public int getOpenTime() {
        return _openTime;
    }

    public int getRandomTime() {
        return _randomTime;
    }

    public int getCloseTime() {
        return _closeTime;
    }

    public int getLevel() {
        return _level;
    }

    public DoorOpenType getOpenType() {
        return _openType;
    }

    public boolean isCheckCollision() {
        return _checkCollision;
    }

    public boolean isAttackable() {
        return _isAttackableDoor;
    }

    public boolean isStealth() {
        return _stealth;
    }

    public boolean isInverted() {
        return _isInverted;
    }
}
