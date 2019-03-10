package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;
import org.l2j.gameserver.mobius.gameserver.enums.ItemListType;
import org.l2j.gameserver.mobius.gameserver.model.ItemInfo;
import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.model.buylist.Product;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.PcInventory;
import org.l2j.gameserver.mobius.gameserver.model.items.L2WarehouseItem;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public abstract class AbstractItemPacket extends AbstractMaskPacket<ItemListType> {
    private static final byte[] MASKS =
            {
                    0x00
            };

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

        if (item.getVisualId() > 0) {
            mask |= ItemListType.VISUAL_ID.getMask();
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

    protected void writeItem(ByteBuffer packet, TradeItem item, long count) {
        writeItem(packet, new ItemInfo(item), count);
    }

    protected void writeItem(ByteBuffer packet, TradeItem item) {
        writeItem(packet, new ItemInfo(item));
    }

    protected void writeItem(ByteBuffer packet, L2WarehouseItem item) {
        writeItem(packet, new ItemInfo(item));
    }

    protected void writeItem(ByteBuffer packet, L2ItemInstance item) {
        writeItem(packet, new ItemInfo(item));
    }

    protected void writeItem(ByteBuffer packet, Product item) {
        writeItem(packet, new ItemInfo(item));
    }

    protected void writeItem(ByteBuffer packet, ItemInfo item) {
        final int mask = calculateMask(item);
        // cddcQcchQccddc
        packet.put((byte) mask);
        packet.putInt(item.getObjectId()); // ObjectId
        packet.putInt(item.getItem().getDisplayId()); // ItemId
        packet.put((byte) (item.getItem().isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocation())); // T1
        packet.putLong(item.getCount()); // Quantity
        packet.put((byte) item.getItem().getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        packet.put((byte) item.getCustomType1()); // Filler (always 0)
        packet.putShort((short) item.getEquipped()); // Equipped : 00-No, 01-yes
        packet.putLong(item.getItem().getBodyPart()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        packet.put((byte) item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        packet.put((byte) 0x01); // TODO : Find me
        packet.putInt(item.getMana());
        packet.putInt(item.getTime());
        packet.put((byte) (item.isAvailable() ? 1 : 0)); // GOD Item enabled = 1 disabled (red) = 0
        packet.put((byte) 0x00); // 140 protocol
        packet.put((byte) 0x00); // 140 protocol
        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(packet, item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(packet, item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(packet, item);
        }
        if (containsMask(mask, ItemListType.VISUAL_ID)) {
            packet.putInt(item.getVisualId()); // Item remodel visual ID
        }
        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            writeItemEnsoulOptions(packet, item);
        }
    }

    protected void writeItem(ByteBuffer packet, ItemInfo item, long count) {
        final int mask = calculateMask(item);
        packet.put((byte) mask);
        packet.putInt(item.getObjectId()); // ObjectId
        packet.putInt(item.getItem().getDisplayId()); // ItemId
        packet.put((byte) (item.getItem().isQuestItem() || (item.getEquipped() == 1) ? 0xFF : item.getLocation())); // T1
        packet.putLong(count); // Quantity
        packet.put((byte) item.getItem().getType2()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        packet.put((byte) item.getCustomType1()); // Filler (always 0)
        packet.putShort((short) item.getEquipped()); // Equipped : 00-No, 01-yes
        packet.putLong(item.getItem().getBodyPart()); // Slot : 0006-lr.ear, 0008-neck, 0030-lr.finger, 0040-head, 0100-l.hand, 0200-gloves, 0400-chest, 0800-pants, 1000-feet, 4000-r.hand, 8000-r.hand
        packet.put((byte) item.getEnchantLevel()); // Enchant level (pet level shown in control item)
        packet.put((byte) 0x01); // TODO : Find me
        packet.putInt(item.getMana());
        packet.putInt(item.getTime());
        packet.put((byte) (item.isAvailable() ? 1 : 0)); // GOD Item enabled = 1 disabled (red) = 0
        packet.put((byte) 0x00); // 140 protocol
        packet.put((byte) 0x00); // 140 protocol
        if (containsMask(mask, ItemListType.AUGMENT_BONUS)) {
            writeItemAugment(packet, item);
        }
        if (containsMask(mask, ItemListType.ELEMENTAL_ATTRIBUTE)) {
            writeItemElemental(packet, item);
        }
        if (containsMask(mask, ItemListType.ENCHANT_EFFECT)) {
            writeItemEnchantEffect(packet, item);
        }
        if (containsMask(mask, ItemListType.VISUAL_ID)) {
            packet.putInt(item.getVisualId()); // Item remodel visual ID
        }
        if (containsMask(mask, ItemListType.SOUL_CRYSTAL)) {
            packet.put((byte) item.getSoulCrystalOptions().size());
            for (EnsoulOption option : item.getSoulCrystalOptions()) {
                packet.putInt(option.getId());
            }
            packet.put((byte) item.getSoulCrystalSpecialOptions().size());
            for (EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
                packet.putInt(option.getId());
            }
        }
    }

    protected void writeItemAugment(ByteBuffer packet, ItemInfo item) {
        if ((item != null) && (item.getAugmentation() != null)) {
            packet.putInt(item.getAugmentation().getOption1Id());
            packet.putInt(item.getAugmentation().getOption2Id());
        } else {
            packet.putInt(0);
            packet.putInt(0);
        }
    }

    protected void writeItemElementalAndEnchant(ByteBuffer packet, ItemInfo item) {
        writeItemElemental(packet, item);
        writeItemEnchantEffect(packet, item);
    }

    protected void writeItemElemental(ByteBuffer packet, ItemInfo item) {
        if (item != null) {
            packet.putShort((short) item.getAttackElementType());
            packet.putShort((short) item.getAttackElementPower());
            packet.putShort((short) item.getAttributeDefence(AttributeType.FIRE));
            packet.putShort((short) item.getAttributeDefence(AttributeType.WATER));
            packet.putShort((short) item.getAttributeDefence(AttributeType.WIND));
            packet.putShort((short) item.getAttributeDefence(AttributeType.EARTH));
            packet.putShort((short) item.getAttributeDefence(AttributeType.HOLY));
            packet.putShort((short) item.getAttributeDefence(AttributeType.DARK));
        } else {
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
            packet.putShort((short) 0);
        }
    }

    protected void writeItemEnchantEffect(ByteBuffer packet, ItemInfo item) {
        // Enchant Effects
        for (int op : item.getEnchantOptions()) {
            packet.putInt(op);
        }
    }

    protected void writeItemEnsoulOptions(ByteBuffer packet, ItemInfo item) {
        if (item != null) {
            packet.put((byte) item.getSoulCrystalOptions().size()); // Size of regular soul crystal options.
            for (EnsoulOption option : item.getSoulCrystalOptions()) {
                packet.putInt(option.getId()); // Regular Soul Crystal Ability ID.
            }

            packet.put((byte) item.getSoulCrystalSpecialOptions().size()); // Size of special soul crystal options.
            for (EnsoulOption option : item.getSoulCrystalSpecialOptions()) {
                packet.putInt(option.getId()); // Special Soul Crystal Ability ID.
            }
        } else {
            packet.put((byte) 0); // Size of regular soul crystal options.
            packet.put((byte) 0); // Size of special soul crystal options.
        }
    }

    protected void writeInventoryBlock(ByteBuffer packet, PcInventory inventory) {
        if (inventory.hasInventoryBlock()) {
            packet.putShort((short) inventory.getBlockItems().size());
            packet.put((byte) inventory.getBlockMode().getClientId());
            for (int id : inventory.getBlockItems()) {
                packet.putInt(id);
            }
        } else {
            packet.putShort((short) 0x00);
        }
    }
}
