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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author Mobius
 */
public class PackageSendableList extends AbstractItemPacket {
    private final Collection<Item> _items;
    private final int _objectId;
    private final long _adena;
    private final int _sendType;

    public PackageSendableList(int sendType, Player player, int objectId) {
        _sendType = sendType;
        _items = player.getInventory().getAvailableItems(true, true, true);
        _objectId = objectId;
        _adena = player.getAdena();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PACKAGE_SENDABLE_LIST, buffer );

        buffer.writeByte(_sendType);
        if (_sendType == 2) {
            buffer.writeInt(_items.size());
            buffer.writeInt(_items.size());
            for (Item item : _items) {
                writeItem(item, buffer);
                buffer.writeInt(item.getObjectId());
            }
        } else {
            buffer.writeInt(_objectId);
            buffer.writeLong(_adena);
            buffer.writeInt(_items.size());
        }
    }

}
