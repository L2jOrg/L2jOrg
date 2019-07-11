package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExAutoSoulShot extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AUTO_SOUL_SHOT);

        writeInt(_itemId);
        writeInt(_enable ? 0x01 : 0x00);
        writeInt(_type);
    }

}
