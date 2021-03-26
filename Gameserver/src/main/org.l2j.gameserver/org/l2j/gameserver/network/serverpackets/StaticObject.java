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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class StaticObject extends ServerPacket {
    private final int _staticObjectId;
    private final int _objectId;
    private final int _type;
    private final boolean _isTargetable;
    private final int _meshIndex;
    private final boolean _isClosed;
    private final boolean _isEnemy;
    private final int _maxHp;
    private final int _currentHp;
    private final boolean _showHp;
    private final int _damageGrade;

    public StaticObject(StaticWorldObject staticObject) {
        _staticObjectId = staticObject.getId();
        _objectId = staticObject.getObjectId();
        _type = 0;
        _isTargetable = true;
        _meshIndex = StaticWorldObject.MESH_INDEX;
        _isClosed = false;
        _isEnemy = false;
        _maxHp = 0;
        _currentHp = 0;
        _showHp = false;
        _damageGrade = 0;
    }

    public StaticObject(Door door, boolean targetable) {
        _staticObjectId = door.getId();
        _objectId = door.getObjectId();
        _type = 1;
        _isTargetable = door.isTargetable() || targetable;
        _meshIndex = 1;
        _isClosed = !door.isOpen();
        _isEnemy = door.isEnemy();
        _maxHp = door.getMaxHp();
        _currentHp = (int) door.getCurrentHp();
        _showHp = door.getIsShowHp();
        _damageGrade = door.getDamage();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.STATIC_OBJECT_INFO, buffer );

        buffer.writeInt(_staticObjectId);
        buffer.writeInt(_objectId);
        buffer.writeInt(_type);
        buffer.writeInt(_isTargetable ? 1 : 0);
        buffer.writeInt(_meshIndex);
        buffer.writeInt(_isClosed ? 1 : 0);
        buffer.writeInt(_isEnemy ? 1 : 0);
        buffer.writeInt(_currentHp);
        buffer.writeInt(_maxHp);
        buffer.writeInt(_showHp ? 1 : 0);
        buffer.writeInt(_damageGrade);
    }

}
