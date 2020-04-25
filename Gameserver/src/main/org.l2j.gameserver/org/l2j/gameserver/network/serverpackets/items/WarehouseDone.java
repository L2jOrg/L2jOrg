package org.l2j.gameserver.network.serverpackets.items;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class WarehouseDone extends ServerPacket {

    private final boolean success;

    public WarehouseDone(boolean success) {
        this.success = success;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.WAREHOUSE_DONE);
        writeInt(success);
    }
}
