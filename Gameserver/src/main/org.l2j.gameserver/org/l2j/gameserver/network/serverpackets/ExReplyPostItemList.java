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

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyPostItemList extends AbstractItemPacket {
    private final int _sendType;
    private final Player _activeChar;
    private final Collection<Item> _itemList;

    public ExReplyPostItemList(int sendType, Player activeChar) {
        _sendType = sendType;
        _activeChar = activeChar;
        _itemList = _activeChar.getInventory().getAvailableItems(true, false, false);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_REPLY_POST_ITEM_LIST);
        writeByte((byte) _sendType);
        writeInt(_itemList.size());
        if (_sendType == 2) {
            writeInt(_itemList.size());
            for (Item item : _itemList) {
                writeItem(item);
            }
        }
    }

}
