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
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.Collection;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends AbstractItemPacket {
    private final boolean _done;
    private final int _inventorySlots;
    private final Collection<Item> _sellList;
    private Collection<Item> _refundList = null;
    private double _castleTaxRate = 1;

    public ExBuySellList(Player player, boolean done) {
        final Summon pet = player.getPet();
        _sellList = player.getInventory().getItems(item -> !item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())));
        _inventorySlots = player.getInventory().getSize();
        if (player.hasRefund()) {
            _refundList = player.getRefund().getItems();
        }
        _done = done;
    }

    public ExBuySellList(Player player, boolean done, double castleTaxRate) {
        this(player, done);
        _castleTaxRate = 1 - castleTaxRate;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_BUY_SELL_LIST, buffer );

        buffer.writeInt(0x01); // Type SELL
        buffer.writeInt(_inventorySlots);

        if ((_sellList != null)) {
            buffer.writeShort(_sellList.size());
            for (Item item : _sellList) {
                writeItem(item, buffer);
                buffer.writeLong((long) ((item.getReferencePrice() / 2) * _castleTaxRate));
            }
        } else {
            buffer.writeShort(0x00);
        }

        if ((_refundList != null) && !_refundList.isEmpty()) {
            buffer.writeShort(_refundList.size());
            int i = 0;
            for (Item item : _refundList) {
                writeItem(item, buffer);
                buffer.writeInt(i++);
                buffer.writeLong((item.getReferencePrice() / 2) * item.getCount());
            }
        } else {
            buffer.writeShort(0x00);
        }
        buffer.writeByte(_done);
    }

}
