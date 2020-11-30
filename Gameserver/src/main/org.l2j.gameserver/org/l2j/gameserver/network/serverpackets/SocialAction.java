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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class SocialAction extends ServerPacket {
    // TODO: Enum
    public static final int LEVEL_UP = 2122;
    public static final int HERO_CLAIMED = 20016;

    private final int _charObjId;
    private final int _actionId;

    public SocialAction(int objectId, int actionId) {
        _charObjId = objectId;
        _actionId = actionId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SOCIAL_ACTION, buffer );

        buffer.writeInt(_charObjId);
        buffer.writeInt(_actionId);
        buffer.writeInt(0x00); // TODO: Find me!
    }

}
