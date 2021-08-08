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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.settings.CharacterSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author l3x
 */
public class RequestBuySeed extends ClientPacket {
    private static final int BATCH_LENGTH = 12; // length of the one item

    @Override
    public void readImpl() throws InvalidDataPacketException {
        readInt(); // manor Id
        final int count = readInt();
        if (count <= 0 || count > CharacterSettings.maxItemInPacket() || count * BATCH_LENGTH != available()) {
            throw new InvalidDataPacketException();
        }

        List<ItemHolder> _items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int itemId = readInt();
            final long cnt = readLong();
            if ((cnt < 1) || (itemId < 1)) {
                throw new InvalidDataPacketException();
            }
            _items.add(new ItemHolder(itemId, cnt));
        }
    }

    @Override
    public void runImpl() { }
}
