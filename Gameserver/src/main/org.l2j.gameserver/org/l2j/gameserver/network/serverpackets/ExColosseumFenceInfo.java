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
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.actor.instance.Fence;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author HoridoJoho / FBIagent
 */
public class ExColosseumFenceInfo extends ServerPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _width;
    private final int _length;
    private final int _clientState;

    public ExColosseumFenceInfo(Fence fence) {
        this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
    }

    public ExColosseumFenceInfo(int objId, double x, double y, double z, int width, int length, FenceState state) {
        _objId = objId;
        _x = (int) x;
        _y = (int) y;
        _z = (int) z;
        _width = width;
        _length = length;
        _clientState = state.getClientId();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_COLOSSEUM_FENCE_INFO, buffer );

        buffer.writeInt(_objId);
        buffer.writeInt(_clientState);
        buffer.writeInt(_x);
        buffer.writeInt(_y);
        buffer.writeInt(_z);
        buffer.writeInt(_width);
        buffer.writeInt(_length);
    }

}