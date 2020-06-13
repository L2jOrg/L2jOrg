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
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class WareHouseWithdrawalList extends AbstractItemPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(WareHouseWithdrawalList.class);

    public static final int PRIVATE = 1;
    public static final int CLAN = 2;
    public static final int CASTLE = 3; // not sure
    public static final int FREIGHT = 1;
    private final int _sendType;
    private final int _invSize;
    private final List<Integer> _itemsStackable = new ArrayList<>();
    private Player _activeChar;
    private long _playerAdena;
    private Collection<Item> _items;
    /**
     * <ul>
     * <li>0x01-Private Warehouse</li>
     * <li>0x02-Clan Warehouse</li>
     * <li>0x03-Castle Warehouse</li>
     * <li>0x04-Warehouse</li>
     * </ul>
     */
    private int _whType;

    public WareHouseWithdrawalList(int sendType, Player player, int type) {
        _sendType = sendType;
        _activeChar = player;
        _whType = type;

        _playerAdena = _activeChar.getAdena();
        _invSize = player.getInventory().getSize();
        if (_activeChar.getActiveWarehouse() == null) {
            LOGGER.warn("error while sending withdraw request to: " + _activeChar.getName());
            return;
        }

        _items = _activeChar.getActiveWarehouse().getItems();

        for (Item item : _items) {
            if (item.isStackable()) {
                _itemsStackable.add(item.getDisplayId());
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.WAREHOUSE_WITHDRAW_LIST);
        writeByte((byte) _sendType);
        if (_sendType == 2) {
            writeShort((short) 0x00);
            writeInt(_invSize);
            writeInt(_items.size());
            for (Item item : _items) {
                writeItem(item);
                writeInt(item.getObjectId());
                writeInt(0x00);
                writeInt(0x00);
            }
        } else {
            writeShort((short) _whType);
            writeLong(_playerAdena);
            writeInt(_invSize);
            writeInt(_items.size());
        }
    }

}
