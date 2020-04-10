package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExCostumeUseItem extends ServerPacket {

    private final int costumeId;
    private final boolean success;

    public ExCostumeUseItem(int costumeId, boolean success) {
        this.costumeId = costumeId;
        this.success = success;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_COSTUME_USE_ITEM);
        writeByte(success);
        writeInt(costumeId);
    }
}
