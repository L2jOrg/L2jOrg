package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.Elementals;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends ServerPacket {
    private final int _itemId;
    private final long _count;
    private final AttributeType _atribute;
    private final int _level;
    private final Set<Integer> _items = new HashSet<>();

    public ExChooseInventoryAttributeItem(Player activeChar, Item stone) {
        _itemId = stone.getDisplayId();
        _count = stone.getCount();
        _atribute = AttributeType.findByClientId(Elementals.getItemElement(_itemId));
        if ((_atribute == AttributeType.NONE)) {
            throw new IllegalArgumentException("Undefined Atribute item: " + stone);
        }
        _level = Elementals.getMaxElementLevel(_itemId);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_CHOOSE_INVENTORY_ATTRIBUTE_ITEM);

        writeInt(_itemId);
        writeLong(_count);
        writeInt(_atribute == AttributeType.FIRE ? 1 : 0); // Fire
        writeInt(_atribute == AttributeType.WATER ? 1 : 0); // Water
        writeInt(_atribute == AttributeType.WIND ? 1 : 0); // Wind
        writeInt(_atribute == AttributeType.EARTH ? 1 : 0); // Earth
        writeInt(_atribute == AttributeType.HOLY ? 1 : 0); // Holy
        writeInt(_atribute == AttributeType.DARK ? 1 : 0); // Unholy
        writeInt(_level); // Item max attribute level
        writeInt(_items.size());
        _items.forEach(this::writeInt);
    }

}
