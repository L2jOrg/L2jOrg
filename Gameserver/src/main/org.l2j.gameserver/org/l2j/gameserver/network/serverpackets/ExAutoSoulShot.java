package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExAutoSoulShot extends IClientOutgoingPacket {
    private final int _itemId;
    private final boolean _enable;
    private final int _type;

    /**
     * @param itemId
     * @param enable
     * @param type
     */
    public ExAutoSoulShot(int itemId, boolean enable, int type) {
        _itemId = itemId;
        _enable = enable;
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_AUTO_SOUL_SHOT.writeId(packet);

        packet.putInt(_itemId);
        packet.putInt(_enable ? 0x01 : 0x00);
        packet.putInt(_type);
    }
}
