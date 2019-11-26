package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.ItemListType;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.itemcontainer.PlayerInventory;
import org.l2j.gameserver.model.items.instance.Item;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
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

        if (((item.getSoulCrystalOptions() != null) && !item.getSoulCrystalOptions().isEmpty()) || ((item.getSoulCrystalSpecialOptions() != null) && !item.getSoulCrystalSpecialOptions().isEmpty())) {
            mask |= ItemListType.SOUL_CRYSTAL.getMask();
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

    protected void writeItem(Item item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(Product item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(ItemInfo item) {
        final int mask = calculateMask(item);
        // cddcQcchQccddc
        writeByte(mask);
        writeInt(item.getObjectId()); // ObjectId
        writeInt(item.getDisplayId()); // ItemId
        writeByte(item.isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocationSlot()); // T1
        writeLong(item.getCount()); // Quantity
        writeByte(item.getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        writeByte(item.getCustomType1()); // Filler (always 0)
        writeShort(item.getEquipped()); // Equipped : 00-No, 01-yes
        writeLong(item.getBodyPart().getId()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        writeByte(item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        writeByte(0x01); // TODO : Find me
        writeInt(-1); // mana
        writeInt(item.getTime());
        writeByte(item.isAvailable()); // GOD Item enabled = 1 disabled (red) = 0
        writeByte(0x00); // 140 protocol
        writeByte(0x00); // 140 protocol
        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item);
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeItemEnsoulOptions(item);
        }
    }

    protected void writeItem(ItemInfo item, long count) {
        final int mask = calculateMask(item);
        writeByte((byte) mask);
        writeInt(item.getObjectId()); // ObjectId
        writeInt(item.getDisplayId()); // ItemId
        writeByte((item.isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocationSlot())); // T1
        writeLong(count); // Quantity
        writeByte(item.getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        writeByte((byte) item.getCustomType1()); // Filler (always 0)
        writeShort((short) item.getEquipped()); // Equipped : 00-No, 01-yes
        writeLong(item.getBodyPart().getId()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        writeByte((byte) item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        writeByte((byte) 0x01); // TODO : Find me
        writeInt(-1); // mana
        writeInt(item.getTime());
        writeByte((byte) (item.isAvailable() ? 1 : 0)); // GOD Item enabled = 1 disabled (red) = 0
        writeByte((byte) 0x00); // 140 protocol
        writeByte((byte) 0x00); // 140 protocol
        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(item);
        }

        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeByte((byte) item.getSoulCrystalOptions().size());
            for (EnsoulOption option : item.getSoulCrystalOptions()) {
                writeInt(option.getId());
            }
            writeByte((byte) item.getSoulCrystalSpecialOptions().size());
            for (EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
                writeInt(option.getId());
            }
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

    protected void writeItemElementalAndEnchant(ByteBuffer packet, ItemInfo item) {
        writeItemElemental(item);
        writeItemEnchantEffect(item);
    }

    protected void writeItemElemental(ItemInfo item) {
        if (item != null) {
            writeShort((short) item.getAttackElementType());
            writeShort((short) item.getAttackElementPower());
            writeShort((short) item.getAttributeDefence(AttributeType.FIRE));
            writeShort((short) item.getAttributeDefence(AttributeType.WATER));
            writeShort((short) item.getAttributeDefence(AttributeType.WIND));
            writeShort((short) item.getAttributeDefence(AttributeType.EARTH));
            writeShort((short) item.getAttributeDefence(AttributeType.HOLY));
            writeShort((short) item.getAttributeDefence(AttributeType.DARK));
        } else {
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
            writeShort((short) 0);
        }
    }

    protected void writeItemEnchantEffect(ItemInfo item) {
        // Enchant Effects
        for (int op : item.getEnchantOptions()) {
            writeInt(op);
        }
    }

    protected void writeItemEnsoulOptions(ItemInfo item) {
        if (item != null) {
            writeByte((byte) item.getSoulCrystalOptions().size()); // Size of regular soul crystal options.
            for (EnsoulOption option : item.getSoulCrystalOptions()) {
                writeInt(option.getId()); // Regular Soul Crystal Ability ID.
            }

            writeByte((byte) item.getSoulCrystalSpecialOptions().size()); // Size of special soul crystal options.
            for (EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
                writeInt(option.getId()); // Special Soul Crystal Ability ID.
            }
        } else {
            writeByte((byte) 0); // Size of regular soul crystal options.
            writeByte((byte) 0); // Size of special soul crystal options.
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
