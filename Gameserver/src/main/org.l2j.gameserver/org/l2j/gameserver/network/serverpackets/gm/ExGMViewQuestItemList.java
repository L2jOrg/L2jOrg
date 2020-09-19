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
package org.l2j.gameserver.network.serverpackets.gm;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
public class ExGMViewQuestItemList extends AbstractItemPacket {
    private final Collection<Item> items;
    private final int sendType;

    public ExGMViewQuestItemList(int sendType, Player player) {
        this.sendType = sendType;
        items = player.getInventory().getQuestItems();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_GM_VIEW_QUEST_ITEMLIST);

        writeByte(sendType);
        if (sendType == 2) {
            writeInt(items.size());
            writeInt(items.size());
            for (Item item : items) {
                writeItem(item);
            }
        } else {
            writeInt(100);
            writeInt(items.size());
        }
        writeShort(0x00);
    }

}
