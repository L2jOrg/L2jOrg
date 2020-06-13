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

import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.MultisellEntryHolder;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public final class MultiSellList extends AbstractItemPacket {
    private final PreparedMultisellListHolder _list;
    private final boolean _finished;
    private int _size;
    private int _index;

    public MultiSellList(PreparedMultisellListHolder list, int index) {
        _list = list;
        _index = index;
        _size = list.getEntries().size() - index;
        if (_size > MultisellData.PAGE_SIZE) {
            _finished = false;
            _size = MultisellData.PAGE_SIZE;
        } else {
            _finished = true;
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MULTI_SELL_LIST);

        writeByte((byte) 0x00); // Helios
        writeInt(_list.getId()); // list id
        writeByte((byte) 0x00); // GOD Unknown
        writeInt(1 + (_index / MultisellData.PAGE_SIZE)); // page started from 1
        writeInt(_finished ? 0x01 : 0x00); // finished
        writeInt(MultisellData.PAGE_SIZE); // size of pages
        writeInt(_size); // list length
        writeByte((byte) 0x00); // Grand Crusade
        writeByte((byte) (_list.isChanceMultisell() ? 0x01 : 0x00)); // new multisell window
        writeInt(0x20); // Helios - Always 32

        while (_size-- > 0) {
            final ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
            final MultisellEntryHolder entry = _list.getEntries().get(_index++);

            writeInt(_index); // Entry ID. Start from 1.
            writeByte((byte) (entry.isStackable() ? 1 : 0));

            // Those values will be passed down to MultiSellChoose packet.
            writeShort((short)(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0)); // enchant level
            writeItemAugment(itemEnchantment);
            writeItemElemental(itemEnchantment);
            writeItemEnsoulOptions(itemEnchantment);

            writeShort((short) entry.getProducts().size());
            writeShort((short) entry.getIngredients().size());

            for (ItemChanceHolder product : entry.getProducts()) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(product.getId());
                final ItemInfo displayItemEnchantment = (_list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getTemplate().getClass())) ? itemEnchantment : null;

                writeInt(product.getId());
                if (template != null) {
                    writeLong(template.getBodyPart().getId());
                    writeShort((short) template.getType2());
                } else {
                    writeLong(0);
                    writeShort((short) 65535);
                }
                writeLong(_list.getProductCount(product));
                writeShort((short) (product.getEnchantmentLevel() > 0 ? product.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
                writeInt((int) Math.ceil(product.getChance())); // chance
                writeItemAugment(displayItemEnchantment);
                writeItemElemental(displayItemEnchantment);
                writeItemEnsoulOptions(displayItemEnchantment);
            }

            for (ItemChanceHolder ingredient : entry.getIngredients()) {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(ingredient.getId());
                final ItemInfo displayItemEnchantment = ((itemEnchantment != null) && (itemEnchantment.getId() == ingredient.getId())) ? itemEnchantment : null;

                writeInt(ingredient.getId());
                writeShort((short)(template != null ? template.getType2() : 65535));
                writeLong(_list.getIngredientCount(ingredient));
                writeShort((short) (ingredient.getEnchantmentLevel() > 0 ? ingredient.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
                writeItemAugment(displayItemEnchantment);
                writeItemElemental(displayItemEnchantment);
                writeItemEnsoulOptions(displayItemEnchantment);
            }
        }
    }
}