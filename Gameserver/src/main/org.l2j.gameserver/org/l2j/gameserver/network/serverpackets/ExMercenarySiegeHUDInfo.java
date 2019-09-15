package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExMercenarySiegeHUDInfo extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_HUD_INFO);
        writeInt(3);
        writeInt(0);
        writeLong(System.currentTimeMillis()); // unk  DA D0 59 5D A6 15 00 00
    }
}
