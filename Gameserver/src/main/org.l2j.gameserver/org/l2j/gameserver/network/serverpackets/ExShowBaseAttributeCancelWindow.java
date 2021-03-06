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
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

public class ExShowBaseAttributeCancelWindow extends ServerPacket {
    private final Collection<Item> _items;

    public ExShowBaseAttributeCancelWindow(Collection<Item> items) {
        this._items = items;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_BASE_ATTRIBUTE_CANCEL_WINDOW, buffer );

        buffer.writeInt(_items.size());
        for (Item item : _items) {
            buffer.writeInt(item.getObjectId());
            buffer.writeLong(getPrice(item));
        }
    }


    /**
     * TODO: Unhardcode! Update prices for Top/Mid/Low S80/S84
     *
     * @param item
     * @return
     */
    private long getPrice(Item item) {
        long _price = 0;
        if (item.getCrystalType() == CrystalType.S) {
            if (item.isWeapon()) {
                _price = 50000;
            } else {
                _price = 40000;
            }
        }
        return _price;
    }
}
