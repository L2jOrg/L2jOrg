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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class ChangeWaitType extends ServerPacket {

    private final int creatureObjectId;
    private final int type;
    private final int x;
    private final int y;
    private final int z;

    private ChangeWaitType(Creature creature, int newMoveType) {
        creatureObjectId = creature.getObjectId();
        type = newMoveType;
        x = creature.getX();
        y = creature.getY();
        z = creature.getZ();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CHANGE_WAIT_TYPE, buffer );

        buffer.writeInt(creatureObjectId);
        buffer.writeInt(type);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    public static ChangeWaitType sitting(Creature creature) {
        return new ChangeWaitType(creature, 0);
    }

    public static ChangeWaitType standing(Creature creature) {
        return new ChangeWaitType(creature, 1);
    }

    public static ChangeWaitType startFakeDeath(Creature creature) {
        return new ChangeWaitType(creature, 2);
    }

    public static ServerPacket stopFakeDeath(Creature creature) {
        return new ChangeWaitType(creature, 3);
    }

}
