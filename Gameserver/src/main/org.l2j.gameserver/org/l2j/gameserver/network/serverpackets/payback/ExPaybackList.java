package org.l2j.gameserver.network.serverpackets.payback;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author JoeAlisson
 */
public class ExPaybackList extends ServerPacket {

    private final byte eventType;

    public ExPaybackList(byte eventType) {
        this.eventType = eventType;
    }

    @Override
    protected void writeImpl(GameClient client) throws Exception {
        writeId(ServerPacketId.EX_PAYBACK_LIST);

        writeInt(2); // payback size
        for (int i = 0; i < 2; i++) {

            writeInt(3); // reward set size
            for (int j = 0; j < 3; j++) {
                writeInt(57 + j); // item
                writeInt(1); // amount
            }

            writeByte(i); // set index
            writeInt(6); // requirement
            writeByte(5); // received
        }

        writeByte(eventType);
        writeInt((int) Instant.now().plus(6, ChronoUnit.HOURS).getEpochSecond()); // end datetime
        writeInt(57); // item consume
        writeInt(2); // user consumption



    }
}
