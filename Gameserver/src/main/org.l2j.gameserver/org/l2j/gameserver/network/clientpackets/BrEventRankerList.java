package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ExBrLoadEventTopRankers;

import java.nio.ByteBuffer;

/**
 * Halloween rank list client packet. Format: (ch)ddd
 */
public class BrEventRankerList extends IClientIncomingPacket {
    private int _eventId;
    private int _day;
    @SuppressWarnings("unused")
    private int _ranking;

    @Override
    public void readImpl(ByteBuffer packet) {
        _eventId = packet.getInt();
        _day = packet.getInt(); // 0 - current, 1 - previous
        _ranking = packet.getInt();
    }

    @Override
    public void runImpl() {
        // TODO count, bestScore, myScore
        final int count = 0;
        final int bestScore = 0;
        final int myScore = 0;
        client.sendPacket(new ExBrLoadEventTopRankers(_eventId, _day, count, bestScore, myScore));
    }
}
