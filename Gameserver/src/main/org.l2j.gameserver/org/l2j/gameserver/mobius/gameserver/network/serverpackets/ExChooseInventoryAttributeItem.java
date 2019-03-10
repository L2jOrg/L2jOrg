package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;
import org.l2j.gameserver.mobius.gameserver.model.Elementals;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends IClientOutgoingPacket {
    private final int _itemId;
    private final long _count;
    private final AttributeType _atribute;
    private final int _level;
    private final Set<Integer> _items = new HashSet<>();

    public ExChooseInventoryAttributeItem(L2PcInstance activeChar, L2ItemInstance stone) {
        _itemId = stone.getDisplayId();
        _count = stone.getCount();
        _atribute = AttributeType.findByClientId(Elementals.getItemElement(_itemId));
        if ((_atribute == AttributeType.NONE) || (_atribute == AttributeType.NONE_ARMOR)) {
            throw new IllegalArgumentException("Undefined Atribute item: " + stone);
        }
        _level = Elementals.getMaxElementLevel(_itemId);

        // Register only items that can be put an attribute stone/crystal
        for (L2ItemInstance item : activeChar.getInventory().getItems()) {
            if (item.isElementable()) {
                _items.add(item.getObjectId());
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM.writeId(packet);

        packet.putInt(_itemId);
        packet.putLong(_count);
        packet.putInt(_atribute == AttributeType.FIRE ? 1 : 0); // Fire
        packet.putInt(_atribute == AttributeType.WATER ? 1 : 0); // Water
        packet.putInt(_atribute == AttributeType.WIND ? 1 : 0); // Wind
        packet.putInt(_atribute == AttributeType.EARTH ? 1 : 0); // Earth
        packet.putInt(_atribute == AttributeType.HOLY ? 1 : 0); // Holy
        packet.putInt(_atribute == AttributeType.DARK ? 1 : 0); // Unholy
        packet.putInt(_level); // Item max attribute level
        packet.putInt(_items.size());
        _items.forEach(packet::putInt);
    }
}
