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
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public final class FlyToLocation extends ServerPacket {
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final int _chaObjId;
    private final int _chaX;
    private final int _chaY;
    private final int _chaZ;
    private final FlyType _type;
    private int _flySpeed;
    private int _flyDelay;
    private int _animationSpeed;

    public FlyToLocation(Creature cha, int destX, int destY, int destZ, FlyType type) {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
    }

    public FlyToLocation(Creature cha, int destX, int destY, int destZ, FlyType type, int flySpeed, int flyDelay, int animationSpeed) {
        _chaObjId = cha.getObjectId();
        _chaX = cha.getX();
        _chaY = cha.getY();
        _chaZ = cha.getZ();
        _destX = destX;
        _destY = destY;
        _destZ = destZ;
        _type = type;
        _flySpeed = flySpeed;
        _flyDelay = flyDelay;
        _animationSpeed = animationSpeed;
    }

    public FlyToLocation(Creature cha, ILocational dest, FlyType type) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
    }

    public FlyToLocation(Creature cha, ILocational dest, FlyType type, int flySpeed, int flyDelay, int animationSpeed) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type, flySpeed, flyDelay, animationSpeed);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.FLY_TO_LOCATION, buffer );

        buffer.writeInt(_chaObjId);
        buffer.writeInt(_destX);
        buffer.writeInt(_destY);
        buffer.writeInt(_destZ);
        buffer.writeInt(_chaX);
        buffer.writeInt(_chaY);
        buffer.writeInt(_chaZ);
        buffer.writeInt(_type.ordinal());
        buffer.writeInt(_flySpeed);
        buffer.writeInt(_flyDelay);
        buffer.writeInt(_animationSpeed);
    }


    public enum FlyType {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        PUSH_HORIZONTAL,
        JUMP_EFFECTED,
        NOT_USED,
        PUSH_DOWN_HORIZONTAL,
        WARP_BACK,
        WARP_FORWARD
    }
}
