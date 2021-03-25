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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;

public class DropItem extends ServerPacket {
    private final Item item;
    private final int playerId;

    /**
     * Constructor of the DropItem server packet
     *
     * @param item        : Item designating the item
     * @param playerObjId : int designating the player ID who dropped the item
     */
    public DropItem(Item item, int playerObjId) {
        this.item = item;
        playerId = playerObjId;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.DROP_ITEM, buffer );

        buffer.writeInt(playerId);
        buffer.writeInt(item.getObjectId());
        buffer.writeInt(item.getDisplayId());

        buffer.writeInt(item.getX());
        buffer.writeInt(item.getY());
        buffer.writeInt(item.getZ());
        // only show item count if it is a stackable item
        buffer.writeByte(item.isStackable());
        buffer.writeLong(item.getCount());

        buffer.writeByte(0x00);
        // writeInt(0x01); if above C == true (1) then readInt()

        buffer.writeByte(item.getEnchantLevel());
        buffer.writeByte(item.isAugmented());
        buffer.writeByte(nonNull(item.getSpecialAbility())); // special ability amount
    }

}
