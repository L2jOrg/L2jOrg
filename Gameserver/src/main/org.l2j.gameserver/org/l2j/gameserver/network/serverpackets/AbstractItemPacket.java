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

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.ItemListType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;

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

        if ((item.getAttackElementType() >= 0) || (item.getAttributeDefence(AttributeType.FIRE) > 0) || (item.getAttributeDefence(AttributeType.WATER) > 0) || (item.getAttributeDefence(AttributeType.WIND) > 0) || (item.getAttributeDefence(AttributeType.EARTH) > 0) || (item.getAttributeDefence(AttributeType.HOLY) > 0) || (item.getAttributeDefence(AttributeType.DARK) > 0)) {
            mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
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

        if (((item.getSoulCrystalOptions() != null) && !item.getSoulCrystalOptions().isEmpty()) || ((item.getSoulCrystalSpecialOptions() != null) && !item.getSoulCrystalSpecialOptions().isEmpty())) {
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

    protected void writeItem(TradeItem item, long count) {
        writeItem(new ItemInfo(item), count);
    }

    protected void writeItem(TradeItem item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(Item item, Player owner) {
        final int mask = calculateMask(item);
        writeByte(mask);
        writeInt(item.getObjectId());
        writeInt(item.getDisplayId());
        writeByte(item.isQuestItem() || item.isEquipped() ? 0xFF : item.getLocationSlot());
        writeLong(item.getCount());
        writeByte(item.getType2());
        writeByte(0); // Filler (always 0)
        writeShort(item.isEquipped());
        writeLong(item.getBodyPart().getId());
        writeByte(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        writeByte(0x00);
        writeByte(0x00);
        writeInt(-1); // mana
        writeInt(item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) :-9999);
        writeByte(item.isAvailable());
        writeShort(0x00); // locked

        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item);
        }

        if(containsMask(mask, ItemListType.VISUAL_ID)) {
            writeInt(item.getDisplayId()); //TODO visual id
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeSoulCrystalInfo(item);
        }

        if(containsMask(mask, ItemListType.REUSE_DELAY)) {
            writeInt((int) owner.getItemRemainingReuseTime(item.getObjectId()) / 1000);
        }
    }

    private void writeSoulCrystalInfo(Item item) {
        writeByte(item.getSpecialAbilities().size());
        item.getSpecialAbilities().stream()
                .mapToInt(EnsoulOption::getId)
                .forEach(this::writeInt);

        writeByte(item.getAdditionalSpecialAbilities().size());
        item.getAdditionalSpecialAbilities().stream()
                .mapToInt(EnsoulOption::getId)
                .forEach(this::writeInt);
    }

    private void writeItemEnchantEffect(Item item) {
        for (var op : item.getEnchantOptions()) {
            writeInt(op);
        }
    }

    private void writeItemElemental(Item item) {
        writeShort(item.getAttackAttributeType().getClientId());
        writeShort(item.getAttackAttributePower());
        for (var type : AttributeType.ATTRIBUTE_TYPES) {
            writeShort(item.getDefenceAttribute(type));
        }
    }

    private void writeItemAugment(Item item) {
        writeInt(zeroIfNullOrElse(item.getAugmentation(), VariationInstance::getOption1Id));
        writeInt(zeroIfNullOrElse(item.getAugmentation(), VariationInstance::getOption2Id));
    }

    private int calculateMask(Item item) {
        int mask = 0;
        if (nonNull(item.getAugmentation())) {
            mask |= ItemListType.AUGMENT_BONUS.getMask();
        }

        if (hasAttribute(item)) {
            mask |= ItemListType.ELEMENTAL_ATTRIBUTE.getMask();
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
        if(!item.getSpecialAbilities().isEmpty() || !item.getAdditionalSpecialAbilities().isEmpty()) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
        }

        if(item.getReuseDelay() > 0) {
            mask |= ItemListType.REUSE_DELAY.getMask();
        }

        return mask;
    }

    private boolean hasAttribute(Item item) {
        return (item.getAttackAttributeType() != NONE) || (item.getDefenceAttribute(AttributeType.FIRE) > 0) || (item.getDefenceAttribute(AttributeType.WATER) > 0) || (item.getDefenceAttribute(AttributeType.WIND) > 0) || (item.getDefenceAttribute(AttributeType.EARTH) > 0) || (item.getDefenceAttribute(AttributeType.HOLY) > 0) || (item.getDefenceAttribute(AttributeType.DARK) > 0);
    }

    protected void writeItem(Item item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(Product item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(ItemInfo item) {
        writeItem(item, item.getCount());
    }

    protected void writeItem(ItemInfo item, long count) {
        final int mask = calculateMask(item);
        writeByte(mask);
        writeInt(item.getObjectId()); // ObjectId
        writeInt(item.getDisplayId()); // ItemId
        writeByte(item.isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocationSlot()); // T1
        writeLong(count); // Quantity
        writeByte(item.getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        writeByte(0); // Filler (always 0)
        writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
        writeLong(item.getBodyPart().getId()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        writeShort(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        writeByte(0x00);
        writeInt(-1); // mana
        writeInt(item.getTime());
        writeByte(item.isAvailable()); // GOD Item enabled = 1 disabled (red) = 0
        writeShort(0x00); // locked

        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item);
        }

        if(containsMask(mask, ItemListType.VISUAL_ID)) {
            writeInt(item.getDisplayId()); //TODO visual id
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeItemEnsoulOptions(item);
        }

        if(containsMask(mask, ItemListType.REUSE_DELAY)) {
            writeInt(item.getReuse());
        }
    }

    private void writeSoulCrystalInfo(ItemInfo item) {
        writeByte(item.getSoulCrystalOptions().size());
        for (EnsoulOption option : item.getSoulCrystalOptions()) {
            writeInt(option.getId());
        }
        writeByte(item.getSoulCrystalSpecialOptions().size());
        for (EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
            writeInt(option.getId());
        }
    }

    protected void writeItemAugment(ItemInfo item) {
        if ((item != null) && (item.getAugmentation() != null)) {
            writeInt(item.getAugmentation().getOption1Id());
            writeInt(item.getAugmentation().getOption2Id());
        } else {
            writeInt(0);
            writeInt(0);
        }
    }

    protected void writeItemElemental(ItemInfo item) {
        if (item != null) {
            writeShort(item.getAttackElementType());
            writeShort(item.getAttackElementPower());
            writeShort(item.getAttributeDefence(AttributeType.FIRE));
            writeShort(item.getAttributeDefence(AttributeType.WATER));
            writeShort(item.getAttributeDefence(AttributeType.WIND));
            writeShort(item.getAttributeDefence(AttributeType.EARTH));
            writeShort(item.getAttributeDefence(AttributeType.HOLY));
            writeShort(item.getAttributeDefence(AttributeType.DARK));
        } else {
            writeShort(0);
            writeShort(0);
            writeShort(0);
            writeShort(0);
            writeShort(0);
            writeShort(0);
            writeShort(0);
            writeShort(0);
        }
    }

    protected void writeItemEnchantEffect(ItemInfo item) {
        for (int op : item.getEnchantOptions()) {
            writeInt(op);
        }
    }

    protected void writeItemEnsoulOptions(ItemInfo item) {
        if (item != null) {
            writeSoulCrystalInfo(item);
        } else {
            writeByte(0); // Size of regular soul crystal options.
            writeByte(0); // Size of special soul crystal options.
        }
    }

    protected void writeInventoryBlock(PlayerInventory inventory) {
        if (inventory.hasInventoryBlock()) {
            writeShort(inventory.getBlockItems().size());
            writeByte(inventory.getBlockMode().getClientId());
            inventory.getBlockItems().forEach(this::writeInt);
        } else {
            writeShort(0x00);
        }
    }
}
