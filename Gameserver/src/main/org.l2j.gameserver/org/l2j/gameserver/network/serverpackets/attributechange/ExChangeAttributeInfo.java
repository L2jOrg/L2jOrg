package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class ExChangeAttributeInfo extends ServerPacket {
    private static final Map<AttributeType, Byte> ATTRIBUTE_MASKS = new HashMap<>();
    private final int _crystalItemId;
    private int _attributes;
    private int _itemObjId;

    {
        ATTRIBUTE_MASKS.put(AttributeType.FIRE, (byte) 1);
        ATTRIBUTE_MASKS.put(AttributeType.WATER, (byte) 2);
        ATTRIBUTE_MASKS.put(AttributeType.WIND, (byte) 4);
        ATTRIBUTE_MASKS.put(AttributeType.EARTH, (byte) 8);
        ATTRIBUTE_MASKS.put(AttributeType.HOLY, (byte) 16);
        ATTRIBUTE_MASKS.put(AttributeType.DARK, (byte) 32);
    }

    public ExChangeAttributeInfo(int crystalItemId, Item item) {
        _crystalItemId = crystalItemId;
        _attributes = 0;
        for (AttributeType e : AttributeType.ATTRIBUTE_TYPES) {
            if (e != item.getAttackAttributeType()) {
                _attributes |= ATTRIBUTE_MASKS.get(e);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CHANGE_ATTRIBUTE_INFO);
        writeInt(_crystalItemId);
        writeInt(_attributes);
        writeInt(_itemObjId);
    }

}