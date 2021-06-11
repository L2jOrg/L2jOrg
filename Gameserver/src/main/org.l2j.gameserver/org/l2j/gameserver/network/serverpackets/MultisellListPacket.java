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
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.engine.item.shop.multisell.MultisellIngredient;
import org.l2j.gameserver.engine.item.shop.multisell.MultisellProduct;
import org.l2j.gameserver.engine.item.shop.multisell.PreparedMultisellList;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class MultisellListPacket extends AbstractItemPacket {

    private final PreparedMultisellList list;
    private final boolean finished;
    private int size;
    private int index;

    public MultisellListPacket(PreparedMultisellList list, int index) {
        this.list = list;
        this.index = index;
        size = list.size() - index;

        if (size > MultisellEngine.PAGE_SIZE) {
            finished = false;
            size = MultisellEngine.PAGE_SIZE;
        } else {
            finished = true;
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MULTI_SELL_LIST, buffer );

        buffer.writeByte(0x00); // Helios
        buffer.writeInt(list.id());
        buffer.writeByte(0x00); // GOD Unknown
        buffer.writeInt(1 + (index / MultisellEngine.PAGE_SIZE)); // page started from 1
        buffer.writeInt(finished);
        buffer.writeInt(MultisellEngine.PAGE_SIZE);
        buffer.writeInt(size);
        buffer.writeByte(0x00); // Grand Crusade
        buffer.writeByte(list.isChanceBased());
        buffer.writeInt(size > 0 ? 0x20 : 0x24); // Helios - 32 | 36 when is empty

        for (int i = 0; i < size; i++) {
            writeMultisellItem(buffer);
        }
    }

    private void writeMultisellItem(WritableBuffer buffer) {
        final ItemInfo itemEnchantment = list.getItemEnchantment(index);
        final var entry = list.get(index++);

        buffer.writeInt(index); // Entry ID. Start from 1.
        buffer.writeByte(entry.stackable());

        // Those values will be passed down to MultiSellChoose packet.
        buffer.writeShort(Util.zeroIfNullOrElse(itemEnchantment, ItemInfo::getEnchantLevel)); // enchant level
        writeItemAugment(itemEnchantment, buffer);
        writeItemElemental(itemEnchantment, buffer);
        writeItemEnsoulOptions(itemEnchantment, buffer);

        buffer.writeShort(entry.products().size());
        buffer.writeShort(entry.ingredients().size());

        for (var product : entry.products()) {
            writeMultisellProduct(buffer, itemEnchantment, product);
        }

        for (var ingredient : entry.ingredients()) {
            writeMultisellIngredient(buffer, itemEnchantment, ingredient);
        }
    }

    private void writeMultisellIngredient(WritableBuffer buffer, ItemInfo itemEnchantment, MultisellIngredient ingredient) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(ingredient.id());
        final ItemInfo displayItemEnchantment = (nonNull(itemEnchantment) && nonNull(template) && itemEnchantment.getId() == ingredient.id()) ? itemEnchantment : null;

        buffer.writeInt(ingredient.id());
        buffer.writeShort(nonNull(template) ?  template.getType2() : 0xFFFF);
        buffer.writeLong(list.getIngredientCount(ingredient));
        buffer.writeShort((ingredient.enchant() > 0 ? ingredient.enchant() : nonNull(displayItemEnchantment) ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
        writeItemAugment(displayItemEnchantment, buffer);
        writeItemElemental(displayItemEnchantment, buffer);
        writeItemEnsoulOptions(displayItemEnchantment, buffer);
    }

    private void writeMultisellProduct(WritableBuffer buffer, ItemInfo itemEnchantment, MultisellProduct product) {
        final ItemTemplate template = ItemEngine.getInstance().getTemplate(product.id());
        final ItemInfo displayItemEnchantment = (list.maintainEnchantment() && nonNull(itemEnchantment) && nonNull(template) && template.getItemType() == itemEnchantment.getTemplate().getItemType()) ? itemEnchantment : null;

        buffer.writeInt(product.id());
        buffer.writeLong(nonNull(template) ? template.getBodyPart().getId() : 0);
        buffer.writeShort(nonNull(template) ? template.getType2() : 0xFFFF);
        buffer.writeLong(list.getProductCount(product));
        buffer.writeShort((product.enchant() > 0 ? product.enchant() : nonNull(displayItemEnchantment) ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
        buffer.writeInt((int) Math.ceil(product.chance())); // chance
        writeItemAugment(displayItemEnchantment, buffer);
        writeItemElemental(displayItemEnchantment, buffer);
        writeItemEnsoulOptions(displayItemEnchantment, buffer);
    }
}