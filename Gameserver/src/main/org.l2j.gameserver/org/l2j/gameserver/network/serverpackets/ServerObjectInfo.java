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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet, mrTJO
 */
public final class ServerObjectInfo extends ServerPacket {
    private final Npc _activeChar;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _idTemplate;
    private final boolean _isAttackable;
    private final double _collisionHeight;
    private final double _collisionRadius;
    private final String _name;

    public ServerObjectInfo(Npc activeChar, Creature actor) {
        _activeChar = activeChar;
        _idTemplate = _activeChar.getTemplate().getDisplayId();
        _isAttackable = _activeChar.isAutoAttackable(actor);
        _collisionHeight = _activeChar.getCollisionHeight();
        _collisionRadius = _activeChar.getCollisionRadius();
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _heading = _activeChar.getHeading();
        _name = _activeChar.getTemplate().isUsingServerSideName() ? _activeChar.getTemplate().getName() : "";
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SERVER_OBJECT_INFO);

        writeInt(_activeChar.getObjectId());
        writeInt(_idTemplate + 1000000);
        writeString(_name); // name
        writeInt(_isAttackable ? 1 : 0);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_heading);
        writeDouble(1.0); // movement multiplier
        writeDouble(1.0); // attack speed multiplier
        writeDouble(_collisionRadius);
        writeDouble(_collisionHeight);
        writeInt((int) (_isAttackable ? _activeChar.getCurrentHp() : 0));
        writeInt(_isAttackable ? _activeChar.getMaxHp() : 0);
        writeInt(0x01); // object type
        writeInt(0x00); // special effects
    }

}
