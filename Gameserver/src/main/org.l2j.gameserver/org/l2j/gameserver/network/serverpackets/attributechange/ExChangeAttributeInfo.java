package org.l2j.gameserver.network.serverpackets.attributechange;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mobius
 */
public class ExChangeAttributeInfo extends IClientOutgoingPacket {
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

    public ExChangeAttributeInfo(int crystalItemId, L2ItemInstance item) {
        _crystalItemId = crystalItemId;
        _attributes = 0;
        for (AttributeType e : AttributeType.ATTRIBUTE_TYPES) {
            if (e != item.getAttackAttributeType()) {
                _attributes |= ATTRIBUTE_MASKS.get(e);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CHANGE_ATTRIBUTE_INFO.writeId(packet);
        packet.putInt(_crystalItemId);
        packet.putInt(_attributes);
        packet.putInt(_itemObjId);
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}