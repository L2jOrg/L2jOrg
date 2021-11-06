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
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.ItemListType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.item.container.PlayerInventory;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.enums.AttributeType.NONE;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public abstract class AbstractItemPacket extends AbstractMaskPacket<ItemListType> {
    private static final byte[] MASKS = { 0x00 };

    protected static int calculateMask(ItemInfo item) {
        int mask = 0;
        if (item.getAugmentation() != null) {
            mask |= ItemListType.AUGMENT_BONUS.getMask();
        }

        if (item.getEnchantOptions() != null) {
            for (int id : item.getEnchantOptions()) {
                if (id > 0) {
                    mask |= ItemListType.ENCHANT_EFFECT.getMask();
                    break;
                }
            }
        }

        // TODO VisualId
        if (nonNull(item.getSoulCrystalOption()) || nonNull(item.getSoulCrystalSpecialOption())) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
        }

        if(item.getReuse() > 0) {
            mask |= ItemListType.REUSE_DELAY.getMask();
        }

        return mask;
    }

    @Override
    protected byte[] getMasks() {
        return MASKS;
    }

    protected void writeItem(TradeItem item, long count, WritableBuffer buffer) {
        writeItem(new ItemInfo(item), count, buffer);
    }

    protected void writeItem(TradeItem item, WritableBuffer buffer) {
        writeItem(new ItemInfo(item), buffer);
    }

    protected void writeItem(Item item, Player owner, WritableBuffer buffer) {
        final int mask = calculateMask(item);
        buffer.writeByte(mask);
        buffer.writeInt(item.getObjectId());
        buffer.writeInt(item.getDisplayId());
        buffer.writeByte(item.isQuestItem() || item.isEquipped() ? 0xFF : item.getLocationSlot());
        buffer.writeLong(item.getCount());
        buffer.writeByte(item.getType2());
        buffer.writeByte(0); // Filler (always 0)
        buffer.writeShort(item.isEquipped());
        buffer.writeLong(item.getBodyPart().getId());
        buffer.writeShort(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        buffer.writeByte(0x00);
        buffer.writeInt(-1); // mana
        buffer.writeInt(item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) :-9999);
        buffer.writeByte(item.isAvailable());
        buffer.writeShort(0x00); // locked

        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item, buffer);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item, buffer);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item, buffer);
        }

        if(containsMask(mask, ItemListType.VISUAL_ID)) {
            buffer.writeInt(item.getDisplayId()); //TODO visual id
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeSoulCrystalInfo(item, buffer);
        }

        if(containsMask(mask, ItemListType.REUSE_DELAY)) {
            buffer.writeInt((int) owner.getItemRemainingReuseTime(item.getObjectId()) / 1000);
        }
    }

    private void writeSoulCrystalInfo(Item item, WritableBuffer buffer) {
        var specialAbility = item.getSpecialAbility();
        buffer.writeByte(nonNull(specialAbility)); // special ability amount only 1 for classic
        if(nonNull(specialAbility)) {
            buffer.writeInt(specialAbility.id());
        }

        specialAbility = item.getAdditionalSpecialAbility();
        buffer.writeByte(nonNull(specialAbility));
        if(nonNull(specialAbility)) {
            buffer.writeInt(specialAbility.id());
        }
    }

    private void writeItemEnchantEffect(Item item, WritableBuffer buffer) {
        for (var op : item.getEnchantOptions()) {
            buffer.writeInt(op);
        }
    }

    private void writeItemElemental(Item item, WritableBuffer buffer) {
        buffer.writeShort(NONE.getClientId());
        buffer.writeShort(0x00);
        for (var type : AttributeType.ATTRIBUTE_TYPES) {
            buffer.writeShort(0);
        }
    }

    private void writeItemAugment(Item item, WritableBuffer buffer) {
        buffer.writeInt(zeroIfNullOrElse(item.getAugmentation(), VariationInstance::getOption1Id));
        buffer.writeInt(zeroIfNullOrElse(item.getAugmentation(), VariationInstance::getOption2Id));
    }

    private int calculateMask(Item item) {
        int mask = 0;
        if (item.isAugmented()) {
            mask |= ItemListType.AUGMENT_BONUS.getMask();
        }

        if (nonNull(item.getEnchantOptions())) {
            for (int id : item.getEnchantOptions()) {
                if (id > 0) {
                    mask |= ItemListType.ENCHANT_EFFECT.getMask();
                    break;
                }
            }
        }

        // TODO VisualId
        if(nonNull(item.getSpecialAbility()) || nonNull(item.getAdditionalSpecialAbility())) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
        }

        if(item.getReuseDelay() > 0) {
            mask |= ItemListType.REUSE_DELAY.getMask();
        }

        return mask;
    }

    protected void writeItem(Item item, WritableBuffer buffer) {
        writeItem(new ItemInfo(item), buffer);
    }

    protected void writeItem(Product item, WritableBuffer buffer) {
        writeItem(new ItemInfo(item), buffer);
    }

    protected void writeItem(ItemInfo item, WritableBuffer buffer) {
        writeItem(item, item.getCount(), buffer);
    }

    protected void writeItem(ItemInfo item, long count, WritableBuffer buffer) {
        final int mask = calculateMask(item);
        buffer.writeByte(mask);
        buffer.writeInt(item.getObjectId()); // ObjectId
        buffer.writeInt(item.getDisplayId()); // ItemId
        buffer.writeByte(item.isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocationSlot()); // T1
        buffer.writeLong(count); // Quantity
        buffer.writeByte(item.getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        buffer.writeByte(0); // Filler (always 0)
        buffer.writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
        buffer.writeLong(item.getBodyPart().getId()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        buffer.writeShort(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        buffer.writeByte(0x00);
        buffer.writeInt(-1); // mana
        buffer.writeInt(item.getTime());
        buffer.writeByte(item.isAvailable()); // GOD Item enabled = 1 disabled (red) = 0
        buffer.writeShort(0x00); // locked

        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item, buffer);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item, buffer);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item, buffer);
        }

        if(containsMask(mask, ItemListType.VISUAL_ID)) {
            buffer.writeInt(item.getDisplayId()); //TODO visual id
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeItemEnsoulOptions(item, buffer);
        }

        if(containsMask(mask, ItemListType.REUSE_DELAY)) {
            buffer.writeInt(item.getReuse());
        }
    }

    private void writeSoulCrystalInfo(ItemInfo item, WritableBuffer buffer) {
        var soulCrystal = item.getSoulCrystalOption();
        buffer.writeByte(nonNull(soulCrystal)); // soul crystal amount
        if(nonNull(soulCrystal)) {
            buffer.writeInt(soulCrystal.id());
        }

        soulCrystal = item.getSoulCrystalSpecialOption();
        buffer.writeByte(nonNull(soulCrystal));
        if(nonNull(soulCrystal)) {
            buffer.writeInt(soulCrystal.id());
        }
    }

    protected void writeItemAugment(ItemInfo item, WritableBuffer buffer) {
        if ((item != null) && (item.getAugmentation() != null)) {
            buffer.writeInt(item.getAugmentation().getOption1Id());
            buffer.writeInt(item.getAugmentation().getOption2Id());
        } else {
            buffer.writeInt(0);
            buffer.writeInt(0);
        }
    }

    protected void writeItemElemental(ItemInfo item, WritableBuffer buffer) {
        buffer.writeShort(-2); // element type
        buffer.writeShort(0); // element power
        buffer.writeShort(0); // defense fire
        buffer.writeShort(0); // defense water
        buffer.writeShort(0); // defense wind
        buffer.writeShort(0); // defense earth
        buffer.writeShort(0); // defense holy
        buffer.writeShort(0); // defense dark
    }

    protected void writeItemEnchantEffect(ItemInfo item, WritableBuffer buffer) {
        for (int op : item.getEnchantOptions()) {
            buffer.writeInt(op);
        }
    }

    protected void writeItemEnsoulOptions(ItemInfo item, WritableBuffer buffer) {
        if (item != null) {
            writeSoulCrystalInfo(item, buffer);
        } else {
            buffer.writeByte(0); // Size of regular soul crystal options.
            buffer.writeByte(0); // Size of special soul crystal options.
        }
    }

    protected void writeInventoryBlock(PlayerInventory inventory, WritableBuffer buffer) {
        if (inventory.hasInventoryBlock()) {
            buffer.writeShort(inventory.getBlockItems().size());
            buffer.writeByte(inventory.getBlockMode().getClientId());
            inventory.getBlockItems().forEach(buffer::writeInt);
        } else {
            buffer.writeShort(0x00);
        }
    }
}
