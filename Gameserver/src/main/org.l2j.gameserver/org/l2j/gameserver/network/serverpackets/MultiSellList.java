package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.ItemInfo;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.gameserver.model.holders.MultisellEntryHolder;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MULTI_SELL_LIST.writeId(packet);

        packet.put((byte) 0x00); // Helios
        packet.putInt(_list.getId()); // list id
        packet.put((byte) 0x00); // GOD Unknown
        packet.putInt(1 + (_index / MultisellData.PAGE_SIZE)); // page started from 1
        packet.putInt(_finished ? 0x01 : 0x00); // finished
        packet.putInt(MultisellData.PAGE_SIZE); // size of pages
        packet.putInt(_size); // list length
        packet.put((byte) 0x00); // Grand Crusade
        packet.put((byte) (_list.isChanceMultisell() ? 0x01 : 0x00)); // new multisell window
        packet.putInt(0x20); // Helios - Always 32

        while (_size-- > 0) {
            final ItemInfo itemEnchantment = _list.getItemEnchantment(_index);
            final MultisellEntryHolder entry = _list.getEntries().get(_index++);

            packet.putInt(_index); // Entry ID. Start from 1.
            packet.put((byte) (entry.isStackable() ? 1 : 0));

            // Those values will be passed down to MultiSellChoose packet.
            packet.putShort((short)(itemEnchantment != null ? itemEnchantment.getEnchantLevel() : 0)); // enchant level
            writeItemAugment(packet, itemEnchantment);
            writeItemElemental(packet, itemEnchantment);
            writeItemEnsoulOptions(packet, itemEnchantment);

            packet.putShort((short) entry.getProducts().size());
            packet.putShort((short) entry.getIngredients().size());

            for (ItemChanceHolder product : entry.getProducts()) {
                final L2Item template = ItemTable.getInstance().getTemplate(product.getId());
                final ItemInfo displayItemEnchantment = (_list.isMaintainEnchantment() && (itemEnchantment != null) && (template != null) && template.getClass().equals(itemEnchantment.getItem().getClass())) ? itemEnchantment : null;

                packet.putInt(product.getId());
                if (template != null) {
                    packet.putLong(template.getBodyPart());
                    packet.putShort((short) template.getType2());
                } else {
                    packet.putLong(0);
                    packet.putShort((short) 65535);
                }
                packet.putLong(_list.getProductCount(product));
                packet.putShort((short) (product.getEnchantmentLevel() > 0 ? product.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
                packet.putInt((int) Math.ceil(product.getChance())); // chance
                writeItemAugment(packet, displayItemEnchantment);
                writeItemElemental(packet, displayItemEnchantment);
                writeItemEnsoulOptions(packet, displayItemEnchantment);
            }

            for (ItemChanceHolder ingredient : entry.getIngredients()) {
                final L2Item template = ItemTable.getInstance().getTemplate(ingredient.getId());
                final ItemInfo displayItemEnchantment = ((itemEnchantment != null) && (itemEnchantment.getItem().getId() == ingredient.getId())) ? itemEnchantment : null;

                packet.putInt(ingredient.getId());
                packet.putShort((short)(template != null ? template.getType2() : 65535));
                packet.putLong(_list.getIngredientCount(ingredient));
                packet.putShort((short) (ingredient.getEnchantmentLevel() > 0 ? ingredient.getEnchantmentLevel() : displayItemEnchantment != null ? displayItemEnchantment.getEnchantLevel() : 0)); // enchant level
                writeItemAugment(packet, displayItemEnchantment);
                writeItemElemental(packet, displayItemEnchantment);
                writeItemEnsoulOptions(packet, displayItemEnchantment);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        var entries = _list.getEntries().subList(_index, _size);
        var products = entries.stream().mapToInt(entry -> entry.getProducts().size() + entry.getIngredients().size()).sum();
        return  32 + _size * (54 + products * 80);
    }
}