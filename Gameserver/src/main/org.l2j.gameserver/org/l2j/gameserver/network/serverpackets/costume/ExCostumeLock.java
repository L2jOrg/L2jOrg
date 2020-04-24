package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExCostumeLock extends ServerPacket {

    private final int costumeId;
    private final boolean lock;
    private final boolean result;

    public ExCostumeLock(int costumeId, boolean lock, boolean result) {
        this.costumeId = costumeId;
        this.lock = lock;
        this.result = result;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_COSTUME_LOCK);
        writeByte(result);
        writeInt(costumeId);
        writeByte(lock);
    }
}
