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

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.EnumMap;

/**
 * @author Gnacik
 * @author JoeAlisson
 */
public class ShopPreviewInfo extends ServerPacket {
    private final EnumMap<InventorySlot, Integer> items;

    public ShopPreviewInfo(EnumMap<InventorySlot, Integer> items) {
        this.items = items;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.BUY_PREVIEW_INFO);

        writeInt(InventorySlot.TOTAL_SLOTS);

        var paperdool = getPaperdollOrder();
        for (int i = 0; i < 19; i++) {
            writeInt(getFromList(paperdool[i]));
        }
    }


    private int getFromList(InventorySlot key) {
        return items.getOrDefault(key, 0);
    }
}